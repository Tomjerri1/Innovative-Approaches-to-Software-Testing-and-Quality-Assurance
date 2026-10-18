package sorcerstone;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Calculates word frequencies in a single pass — O(n) instead of O(n·k). */
final class WordFrequencyCounter {

    private static final Comparator<WordCount> BY_COUNT_DESC_THEN_WORD =
            Comparator.comparingInt(WordCount::count).reversed()
                    .thenComparing(WordCount::word);

    private final Map<String, Integer> counts;

    private WordFrequencyCounter(Map<String, Integer> counts) {
        this.counts = counts;
    }

    static WordFrequencyCounter count(List<String> words) {
        Map<String, Integer> counts = new HashMap<>();
        for (String w : words) {
            counts.merge(w, 1, Integer::sum);
        }
        return new WordFrequencyCounter(counts);
    }

    int distinctCount() {
        return counts.size();
    }

    /** The {@code n} most frequent words; in case of a tie in frequency, sorted alphabetically. */
    List<WordCount> top(int n) {
        return counts.entrySet().stream()
                .map(e -> new WordCount(e.getKey(), e.getValue()))
                .sorted(BY_COUNT_DESC_THEN_WORD)
                .limit(n)
                .toList();
    }
}
