package sorcerstone;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * Entry point: reads text, counts word frequency, and prints the top N.
 *
 * <p>Usage: {@code java sorcerstone.Main [file_path] [top_n]}
 */
public final class Main {

    private static final Path DEFAULT_FILE = Path.of("src", "txt", "harry.txt");
    private static final int DEFAULT_TOP_N = 30;

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        Path file = args.length > 0 ? Path.of(args[0]) : DEFAULT_FILE;
        int topN = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TOP_N;

        Instant start = Instant.now();

        String text = TextLoader.load(file);
        List<String> words = Tokenizer.tokenize(text);
        List<WordCount> top = WordFrequencyCounter.count(words).top(topN);

        top.forEach(wc -> System.out.println(wc.word() + " " + wc.count()));

        System.out.println("------");
        System.out.println("Words: " + words.size());
        System.out.println("Time, ms: " + Duration.between(start, Instant.now()).toMillis());
    }
}
