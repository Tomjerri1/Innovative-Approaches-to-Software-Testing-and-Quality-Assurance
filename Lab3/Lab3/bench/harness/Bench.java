package sorcerstone;

import java.io.PrintStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.List;

/** Usage: Bench <v0|v1|v2> <file> <warmupRuns> <measuredRuns>. Prints one CSV line. */
public final class Bench {
    static final com.sun.management.ThreadMXBean TMX =
            (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();

    static String run(String v, Path f) throws Exception {
        switch (v) {
            case "v0": return "" + v0.NaiveWordCount.run(f, 30);
            case "v1": {
                var words = v1.Tokenizer.tokenize(v1.TextLoader.load(f));
                var top = v1.WordFrequencyCounter.count(words).top(30);
                top.forEach(w -> System.out.println(w.word() + " " + w.count()));
                return "" + words.size();
            }
            default: {
                var c = StreamingWordCounter.countFile(f);
                c.top(30).forEach(w -> System.out.println(w.word() + " " + w.count()));
                return "" + c.totalWords();
            }
        }
    }

    public static void main(String[] a) throws Exception {
        String v = a[0]; Path f = Path.of(a[1]);
        int warm = Integer.parseInt(a[2]), runs = Integer.parseInt(a[3]);
        PrintStream real = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        long tid = Thread.currentThread().getId();

        // cold: very first run in a fresh JVM
        long a0 = TMX.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
        String words = run(v, f);
        double cold = (System.nanoTime() - t0) / 1e6;
        long alloc = TMX.getThreadAllocatedBytes(tid) - a0;

        for (int i = 0; i < warm; i++) run(v, f);
        double sum = 0, min = 1e18;
        for (int i = 0; i < runs; i++) {
            long s = System.nanoTime(); run(v, f);
            double ms = (System.nanoTime() - s) / 1e6; sum += ms; min = Math.min(min, ms);
        }
        System.setOut(real);
        System.out.println("Version:            " + v);
        System.out.println("Words:              " + words);
        System.out.println(String.format(java.util.Locale.ROOT, "Cold start, ms:     %.1f", cold));
        if (runs > 0) {
            System.out.println(String.format(java.util.Locale.ROOT, "Warm average, ms:   %.2f", sum / runs));
            System.out.println(String.format(java.util.Locale.ROOT, "Warm best, ms:      %.2f", min));
        }
        System.out.println(String.format(java.util.Locale.ROOT, "Allocated, MB:      %.2f", alloc / 1048576.0));
    }
}
