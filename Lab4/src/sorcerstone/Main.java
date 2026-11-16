package sorcerstone;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point: reads the text, counts word frequency, and prints the top N.
 *
 * <p>Usage: {@code java sorcerstone.Main [file_path] [top_n]}
 *
 * <p>Deliberately avoids lambdas, string concatenation ({@code +}) and {@code java.time}: each of
 * them pulls in {@code invokedynamic} / extra class loading, which costs more than the actual
 * counting of a 440 KB file in a short-lived JVM.
 */
public final class Main {

    private static final Path DEFAULT_FILE = Path.of("src", "txt", "harry.txt");
    private static final int DEFAULT_TOP_N = 30;

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        Path file = args.length > 0 ? Path.of(args[0]) : DEFAULT_FILE;
        int topN = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TOP_N;

        long start = System.nanoTime();

        FastWordCounter counter = FastWordCounter.countFile(file);
        List<WordCount> top = counter.top(topN);

        StringBuilder out = new StringBuilder(1024);
        for (WordCount wc : top) {
            out.append(wc.word()).append(' ').append(wc.count()).append('\n');
        }
        PrintStream stdout = System.out;
        stdout.print(out);

        out.setLength(0);
        out.append("------\n");
        out.append("Words: ").append(counter.totalWords()).append('\n');
        out.append("Time, ms: ").append((System.nanoTime() - start) / 1_000_000).append('\n');
        stdout.print(out);
        stdout.flush();
    }
}
