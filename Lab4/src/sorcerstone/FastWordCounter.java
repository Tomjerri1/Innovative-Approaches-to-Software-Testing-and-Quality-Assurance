package sorcerstone;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Single-pass word counter tuned for speed (v3).
 *
 * <ul>
 *   <li>The file is read with one call into a {@code byte[]} (no 8 KB chunk loop, no copying
 *       of words into a scratch buffer).</li>
 *   <li>A 256-entry lookup table maps a byte to its lowercase letter or 0 (= separator), so the
 *       classification is branch-free and the text is lowercased in place.</li>
 *   <li>The hash is computed in the same pass that finds the word boundaries.</li>
 *   <li>Unique words are not copied anywhere: the table stores the offset of the first occurrence
 *       inside the (already lowercased) input array.</li>
 *   <li>Hash table entries are interleaved in one {@code int[]} (hash, offset, length, count),
 *       so a lookup touches one cache line instead of four separate arrays.</li>
 *   <li>{@link #top} uses a small insertion-sorted array: no lambdas, no boxing, no
 *       {@code PriorityQueue} (this also removes {@code invokedynamic} bootstrap cost at startup).</li>
 * </ul>
 *
 * <p>harry.txt is windows-1252, where every non-ASCII byte is not a Latin letter, so working on
 * raw bytes gives exactly the same result as decoding and matching {@code [A-Za-z]+}.
 */
final class FastWordCounter {

    private static final int STRIDE = 4;           // hash, offset, length, count
    private static final int HASH = 0, OFFSET = 1, LENGTH = 2, COUNT = 3;
    private static final int INITIAL_SLOTS = 1 << 14;   // power of two; fits ~8k unique words w/o rehash

    /** byte -> lowercase letter, or 0 for any non-letter. */
    private static final byte[] LOWER = new byte[256];

    static {
        for (int c = 'a'; c <= 'z'; c++) {
            LOWER[c] = (byte) c;
            LOWER[c - 'a' + 'A'] = (byte) c;
        }
    }

    private byte[] data;
    private int[] table = new int[INITIAL_SLOTS * STRIDE];
    private int mask = INITIAL_SLOTS - 1;
    private int size;
    private long totalWords;

    static FastWordCounter countFile(Path file) throws IOException {
        byte[] bytes;
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            bytes = in.readAllBytes();
        }
        FastWordCounter c = new FastWordCounter();
        c.count(bytes, bytes.length);
        return c;
    }

    /** Counts words of {@code buf[0..n)}. The buffer is lowercased in place and kept by this object. */
    void count(byte[] buf, int n) {
        data = buf;
        final byte[] lower = LOWER;
        long words = 0;
        int i = 0;
        while (i < n) {
            int b = lower[buf[i] & 0xFF];
            if (b == 0) {
                i++;
                continue;
            }
            final int start = i;
            int h = 0;
            do {
                buf[i] = (byte) b;
                h = 31 * h + b;
                i++;
            } while (i < n && (b = lower[buf[i] & 0xFF]) != 0);
            add(start, i - start, h ^ (h >>> 16));
            words++;
        }
        totalWords = words;
    }

    private void add(int start, int len, int h) {
        final int[] t = table;
        final byte[] d = data;
        int slot = h & mask;
        while (true) {
            int base = slot * STRIDE;
            int l = t[base + LENGTH];
            if (l == 0) {
                t[base + HASH] = h;
                t[base + OFFSET] = start;
                t[base + LENGTH] = len;
                t[base + COUNT] = 1;
                if (++size > (mask + 1) >> 1) {
                    grow();
                }
                return;
            }
            if (t[base + HASH] == h && l == len) {
                int o = t[base + OFFSET];
                int k = 0;
                while (k < len && d[o + k] == d[start + k]) {
                    k++;
                }
                if (k == len) {
                    t[base + COUNT]++;
                    return;
                }
            }
            slot = (slot + 1) & mask;
        }
    }

    private void grow() {
        final int[] old = table;
        final int newSlots = (mask + 1) * 2;
        final int[] t = new int[newSlots * STRIDE];
        final int newMask = newSlots - 1;
        for (int base = 0; base < old.length; base += STRIDE) {
            if (old[base + LENGTH] == 0) {
                continue;
            }
            int slot = old[base + HASH] & newMask;
            while (t[slot * STRIDE + LENGTH] != 0) {
                slot = (slot + 1) & newMask;
            }
            System.arraycopy(old, base, t, slot * STRIDE, STRIDE);
        }
        table = t;
        mask = newMask;
    }

    long totalWords() {
        return totalWords;
    }

    int distinctCount() {
        return size;
    }

    /** The {@code n} most frequent words; ties are ordered alphabetically. */
    List<WordCount> top(int n) {
        if (n <= 0) {
            return List.of();
        }
        final int[] t = table;
        final int[] best = new int[Math.min(n, size)];   // table bases, best first
        int filled = 0;
        for (int base = 0; base < t.length; base += STRIDE) {
            if (t[base + LENGTH] == 0) {
                continue;
            }
            if (filled == best.length) {
                if (best.length == 0 || !better(base, best[filled - 1])) {
                    continue;
                }
                filled--;                                   // evict the worst
            }
            int pos = filled++;
            while (pos > 0 && better(base, best[pos - 1])) {
                best[pos] = best[pos - 1];
                pos--;
            }
            best[pos] = base;
        }
        List<WordCount> result = new ArrayList<>(filled);
        for (int k = 0; k < filled; k++) {
            int base = best[k];
            result.add(new WordCount(
                    new String(data, t[base + OFFSET], t[base + LENGTH], StandardCharsets.ISO_8859_1),
                    t[base + COUNT]));
        }
        return result;
    }

    /** Frequency descending, then alphabetically (words are ASCII, so byte order == String order). */
    private boolean better(int a, int b) {
        final int[] t = table;
        int ca = t[a + COUNT], cb = t[b + COUNT];
        if (ca != cb) {
            return ca > cb;
        }
        int oa = t[a + OFFSET], ob = t[b + OFFSET];
        return Arrays.compare(data, oa, oa + t[a + LENGTH], data, ob, ob + t[b + LENGTH]) < 0;
    }
}
