package sorcerstone;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.nio.charset.StandardCharsets;

/**
 * Memory-compact word counter (open addressing, no String / Integer objects per token).
 *
 * <p>Unique words are stored once as bytes in a shared arena; counters live in a primitive int[].
 * Adding an occurrence of an already-known word allocates nothing.
 */
final class ByteWordTable {

    private static final int INITIAL_SLOTS = 1 << 12;   // power of two
    private static final float MAX_LOAD = 0.5f;

    private int[] slots = new int[INITIAL_SLOTS];       // 0 = empty, otherwise (entry index + 1)
    private int[] hashes = new int[256];
    private int[] offsets = new int[256];
    private int[] lengths = new int[256];
    private int[] counts = new int[256];
    private byte[] arena = new byte[4096];
    private int arenaUsed;
    private int size;

    /** Registers one occurrence of the word {@code buf[0..len)}. */
    void add(byte[] buf, int len) {
        int h = hash(buf, len);
        int mask = slots.length - 1;
        int i = h & mask;
        int s;
        while ((s = slots[i]) != 0) {
            int e = s - 1;
            if (hashes[e] == h && lengths[e] == len && Arrays.equals(
                    arena, offsets[e], offsets[e] + len, buf, 0, len)) {
                counts[e]++;
                return;
            }
            i = (i + 1) & mask;
        }
        insert(buf, len, h, i);
    }

    private void insert(byte[] buf, int len, int h, int slot) {
        if (size == counts.length) {
            int cap = size * 2;
            hashes = Arrays.copyOf(hashes, cap);
            offsets = Arrays.copyOf(offsets, cap);
            lengths = Arrays.copyOf(lengths, cap);
            counts = Arrays.copyOf(counts, cap);
        }
        if (arenaUsed + len > arena.length) {
            arena = Arrays.copyOf(arena, Math.max(arena.length * 2, arenaUsed + len));
        }
        System.arraycopy(buf, 0, arena, arenaUsed, len);
        hashes[size] = h;
        offsets[size] = arenaUsed;
        lengths[size] = len;
        counts[size] = 1;
        arenaUsed += len;
        slots[slot] = ++size;
        if (size > slots.length * MAX_LOAD) {
            rehash();
        }
    }

    private void rehash() {
        int[] bigger = new int[slots.length * 2];
        int mask = bigger.length - 1;
        for (int e = 0; e < size; e++) {
            int i = hashes[e] & mask;
            while (bigger[i] != 0) {
                i = (i + 1) & mask;
            }
            bigger[i] = e + 1;
        }
        slots = bigger;
    }

    private static int hash(byte[] buf, int len) {
        int h = 0;
        for (int i = 0; i < len; i++) {
            h = 31 * h + buf[i];
        }
        return h ^ (h >>> 16);
    }

    int distinctCount() {
        return size;
    }

    /** Top N via a bounded heap: O(k log n) and only n WordCount objects instead of k. */
    List<WordCount> top(int n) {
        if (n <= 0) {
            return List.of();
        }
        // "worse" element on top, so it is easy to evict
        PriorityQueue<Integer> heap = new PriorityQueue<>(n + 1, (a, b) -> -compare(a, b));
        for (int e = 0; e < size; e++) {
            heap.add(e);
            if (heap.size() > n) {
                heap.poll();
            }
        }
        List<WordCount> result = new ArrayList<>(heap.size());
        Integer[] sorted = heap.toArray(new Integer[0]);
        Arrays.sort(sorted, this::compare);
        for (int e : sorted) {
            result.add(new WordCount(
                    new String(arena, offsets[e], lengths[e], StandardCharsets.ISO_8859_1), counts[e]));
        }
        return result;
    }

    /** Frequency descending, then alphabetically (the words are ASCII, so byte order == String order). */
    private int compare(int a, int b) {
        int c = Integer.compare(counts[b], counts[a]);
        if (c != 0) {
            return c;
        }
        return Arrays.compare(arena, offsets[a], offsets[a] + lengths[a],
                arena, offsets[b], offsets[b] + lengths[b]);
    }
}
