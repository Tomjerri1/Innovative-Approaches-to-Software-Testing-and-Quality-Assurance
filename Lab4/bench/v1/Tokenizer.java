package v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Splits text into lowercase words (Latin letters only). */
public final class Tokenizer {

    private static final Pattern WORD = Pattern.compile("[A-Za-z]+");

    private Tokenizer() {
    }

    public static List<String> tokenize(String text) {
        List<String> words = new ArrayList<>();
        Matcher m = WORD.matcher(text);
        while (m.find()) {
            words.add(m.group().toLowerCase(Locale.ROOT));
        }
        return words;
    }
}
