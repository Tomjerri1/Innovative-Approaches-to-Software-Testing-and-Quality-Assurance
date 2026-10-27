package sorcerstone;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Streaming word counter: the file is read in small chunks, never fully held in memory.
 *
 * <p>harry.txt is windows-1252, in which every non-ASCII byte is NOT a Latin letter, so the
 * tokenizer can work on raw bytes and the result is identical to decoding to String first
 * and matching {@code [A-Za-z]+}.
 */
final class StreamingWordCounter {

    private static final int CHUNK = 8 * 1024;

    private final ByteWordTable table = new ByteWordTable();
    private long totalWords;

    static StreamingWordCounter countFile(Path file) throws IOException {
        StreamingWordCounter c = new StreamingWordCounter();
        try (InputStream in = Files.newInputStream(file)) {
            c.consume(in);
        }
        return c;
    }

    private void consume(InputStream in) throws IOException {
        byte[] chunk = new byte[CHUNK];
        byte[] word = new byte[64];
        int len = 0;
        int read;
        while ((read = in.read(chunk)) != -1) {
            for (int i = 0; i < read; i++) {
                int b = chunk[i];
                if (b >= 'A' && b <= 'Z') {
                    b += 'a' - 'A';
                } else if (b < 'a' || b > 'z') {
                    if (len > 0) {
                        table.add(word, len);
                        totalWords++;
                        len = 0;
                    }
                    continue;
                }
                if (len == word.length) {
                    word = java.util.Arrays.copyOf(word, len * 2);
                }
                word[len++] = (byte) b;
            }
        }
        if (len > 0) {
            table.add(word, len);
            totalWords++;
        }
    }

    long totalWords() {
        return totalWords;
    }

    int distinctCount() {
        return table.distinctCount();
    }

    List<WordCount> top(int n) {
        return table.top(n);
    }
}
