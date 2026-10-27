package v0;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/**
 * Verbatim port of the ORIGINAL v0 Main (see Main.original.txt) into a callable method.
 * Only change: the file path is a parameter and the timer/printing is done by the harness.
 * The algorithm (incl. the String.contains substring bug) is intentionally untouched.
 */
public final class NaiveWordCount {
    public static int run(Path file, int topN) throws Exception {
        String content = new String(Files.readAllBytes(file));
        content = content.replaceAll("[^A-Za-z ]", " ").toLowerCase(Locale.ROOT);
        String[] words = content.split(" +");
        Arrays.sort(words);

        String distinctString = " ";
        for (int i = 0; i < words.length; i++) {
            if (!distinctString.contains(words[i])) {
                distinctString += words[i] + " ";
            }
        }
        String[] distincts = distinctString.split(" ");
        int[] freq = new int[distincts.length];
        for (int i = 0; i < distincts.length; i++) {
            int count = 0;
            for (int j = 0; j < words.length; j++) {
                if (distincts[i].equals(words[j])) count++;
            }
            freq[i] = count;
        }
        for (int i = 0; i < distincts.length; i++) distincts[i] += " " + freq[i];
        Arrays.sort(distincts, Comparator.comparing(str -> Integer.valueOf(str.replaceAll("[^0-9]", ""))));
        for (int i = 0; i < topN; i++) System.out.println(distincts[distincts.length - 1 - i]);
        return words.length;
    }
}
