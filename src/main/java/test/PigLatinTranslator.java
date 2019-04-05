package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * TODO javadoc
 */
public class PigLatinTranslator {

    private static final String PUNCTUATION = ".,:;'\"";
    private static final String VOWELS = "aeiouAEIOU";

    private static String charListToString(List<Character> chars) {
        StringBuilder builder = new StringBuilder();
        chars.forEach(builder::append);
        return builder.toString();
    }

    private static boolean isPunctuation(char ch) {
        return PUNCTUATION.indexOf(ch) >= 0;
    }

    private static boolean isVowel(char ch) {
        return VOWELS.indexOf(ch) >= 0;
    }

    /**
     * TODO javadoc
     * Examples:
     * <table>
     *     <tr>
     *         <td>null</td>
     *         <td>null</td>
     *     </tr>
     *     <tr>
     *         <td>"   "</td>
     *         <td>""</td>
     *     </tr>
     *     <tr>
     *         <td></td>
     *         <td></td>
     *     </tr>
     *     <tr>
     *         <td></td>
     *         <td></td>
     *     </tr>
     *     <tr>
     *         <td></td>
     *         <td></td>
     *     </tr>
     *     <tr>
     *         <td></td>
     *         <td></td>
     *     </tr>
     * </table>
     * @param text
     * @return
     */
    public String translate(String text) {
        if (text == null) {
            return null;
        }

        return Arrays.stream(text.trim().split(" "))
                .map(this::translateWord)
                .collect(Collectors.joining(" "));
    }

    private String applyUpperCaseAndPunctuationCharRules(String word, List<Integer> upperCaseCharIndexes, List<Punctuation> punctuations) {
        List<Character> chars = new ArrayList<>();
        for (char ch : word.toCharArray()) {
            chars.add(ch);
        }

        for (int upperCaseIndex : upperCaseCharIndexes) {
            chars.set(upperCaseIndex, Character.toUpperCase(chars.get(upperCaseIndex)));
        }

        ListIterator<Punctuation> punctuationIterator = punctuations.listIterator(punctuations.size());
        while (punctuationIterator.hasPrevious()) {
            Punctuation punctuation = punctuationIterator.previous();
            chars.add(chars.size() - punctuation.getIndexFromEnd(), punctuation.getCh());
        }

        return charListToString(chars);
    }

    private List<Integer> getUpperCaseCharIndexes(String word) {
        List<Integer> upperCaseIndexes = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (Character.isUpperCase(ch)) {
                upperCaseIndexes.add(i);
            }
        }

        return upperCaseIndexes;
    }

    private PunctuationRemovalResult removePunctuation(String word) {
        StringBuilder resultingWordBuilder = new StringBuilder();
        List<Punctuation> punctuations = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (isPunctuation(ch)) {
                punctuations.add(new Punctuation(word.length() - i - 1, ch));
            } else {
                resultingWordBuilder.append(ch);
            }
        }

        return new PunctuationRemovalResult(resultingWordBuilder.toString(), punctuations);
    }

    private String translateWord(String word) {
        if (word.length() == 0) {
            return "";
        }

        // Hyphens are treated as two words
        int hyphenIndex = word.indexOf('-');
        if (hyphenIndex >= 0) {
            return translateWord(word.substring(0, hyphenIndex)) + "-" + translateWord(word.substring(hyphenIndex + 1));
        }

        PunctuationRemovalResult punctuationRemovalResult = removePunctuation(word);

        List<Integer> upperCaseCharIndexes = getUpperCaseCharIndexes(punctuationRemovalResult.getWord());

        String wordInLowerCase = punctuationRemovalResult.getWord().toLowerCase();

        String processedWord;

        // Words that end in “way” are not modified
        if (wordInLowerCase.endsWith("way")) {
            processedWord = wordInLowerCase;
        } else {
            char firstCharacter = wordInLowerCase.charAt(0);
            if (isVowel(firstCharacter)) {
                // Words that start with a vowel have the letters “way” added to the end
                processedWord = wordInLowerCase + "way";
            } else {
                // Words that start with a consonant have their first letter moved to the end of the word
                // and the letters “ay” added to the end
                processedWord = wordInLowerCase.substring(1) + firstCharacter + "ay";
            }
        }

        return applyUpperCaseAndPunctuationCharRules(processedWord, upperCaseCharIndexes, punctuationRemovalResult.getPunctuations());
    }

    /**
     * TODO javadoc
     */
    private static class Punctuation {

        private final int indexFromEnd;
        private final char ch;

        private Punctuation(int indexFromEnd, char ch) {
            this.indexFromEnd = indexFromEnd;
            this.ch = ch;
        }

        private char getCh() {
            return ch;
        }

        private int getIndexFromEnd() {
            return indexFromEnd;
        }
    }

    /**
     * TODO javadoc
     */
    private static class PunctuationRemovalResult {

        private final String word;
        private final List<Punctuation> punctuations;

        private PunctuationRemovalResult(String word, List<Punctuation> punctuations) {
            this.word = word;
            this.punctuations = punctuations;
        }

        private List<Punctuation> getPunctuations() {
            return punctuations;
        }

        private String getWord() {
            return word;
        }
    }
}
