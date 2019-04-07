package test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

/**
 * <p>Translates text to pig-latin.</p>
 *
 * <p>Examples:
 * <table>
 * <tr>
 * <td>null</td>
 * <td>null</td>
 * </tr>
 * <tr>
 * <td>"   "</td>
 * <td>""</td>
 * </tr>
 * <tr>
 * <td>Hello</td>
 * <td>Ellohay</td>
 * </tr>
 * <tr>
 * <td>apple</td>
 * <td>appleway</td>
 * </tr>
 * <tr>
 * <td>stairway</td>
 * <td>stairway</td>
 * </tr>
 * <tr>
 * <td>can’t</td>
 * <td>antca’y</td>
 * </tr>
 * <tr>
 * <td>end.</td>
 * <td>endway.</td>
 * </tr>
 * <tr>
 * <td>this-thing</td>
 * <td>histay-hingtay</td>
 * </tr>
 * <tr>
 * <td>Beach</td>
 * <td>Eachbay</td>
 * </tr>
 * <tr>
 * <td>McCloud</td>
 * <td>CcLoudmay</td>
 * </tr>
 * </table>
 * </p>
 */
public class PigLatinTranslator {

    /**
     * List of punctuations.
     */
    private static final String PUNCTUATIONS = ".,:;'\"";
    /**
     * List of vowels.
     * A character that is neither {@link #PUNCTUATIONS a punctuation} nor a vowel is treated as a consonant.
     */
    private static final String VOWELS = "aeiou";

    /**
     * Translates the text to pig-latin.
     * The word is {@link String#trim() trimmed} before the translation.
     *
     * @param text Text to translate. May be {@code null}.
     * @return Translated word or {@code null}, if the input was {@code null}.
     */
    public String translate(@Nullable String text) {
        if (text == null) {
            return null;
        }

        return Arrays.stream(text.trim().split(" "))
                .map(this::translateWord)
                .collect(Collectors.joining(" "));
    }

    /**
     * Applies these rules:
     * <ul>
     * <li>Capitalization must remain in the same place.</li>
     * <li>Punctuation must remain in the same relative place from the end of the word.</li>
     * </ul>
     */
    private String applyUpperCaseAndPunctuationRules(String word, List<Integer> upperCaseCharIndexes, List<Punctuation> punctuations) {
        List<Character> chars = new ArrayList<>(word.length() + punctuations.size());
        for (char ch : word.toCharArray()) {
            chars.add(ch);
        }

        // Iterate the upper case character indexes from the first upwards
        for (int upperCaseIndex : upperCaseCharIndexes) {
            // Switch the case back to upper
            chars.set(upperCaseIndex, Character.toUpperCase(chars.get(upperCaseIndex)));
        }

        // Iterate the punctuations from the last downwards
        ListIterator<Punctuation> punctuationIterator = punctuations.listIterator(punctuations.size());
        while (punctuationIterator.hasPrevious()) {
            Punctuation punctuation = punctuationIterator.previous();
            // Add the punctuation to the translated word
            chars.add(chars.size() - punctuation.getIndexFromEnd(), punctuation.getCharacter());
        }

        return charListToString(chars);
    }

    /**
     * Gets upper case character indexes and removes punctuation.
     */
    private UpperCaseAndPunctuation getUpperCaseIndexesAndPunctuation(String word) {
        StringBuilder resultingWordBuilder = new StringBuilder();
        List<Integer> upperCaseIndexes = new ArrayList<>();
        List<Punctuation> punctuations = new ArrayList<>();

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);
            if (isPunctuation(ch)) {
                punctuations.add(new Punctuation(word.length() - i - 1, ch));
            } else {
                if (Character.isUpperCase(word.charAt(i))) {
                    upperCaseIndexes.add(i - punctuations.size());
                }
                resultingWordBuilder.append(Character.toLowerCase(ch));
            }
        }

        return new UpperCaseAndPunctuation(resultingWordBuilder.toString(), upperCaseIndexes, punctuations);
    }

    /**
     * Translates a single word to pig-latin.
     *
     * @param word Must not be {@code null}.
     * @return Translated word. Never {@code null}.
     */
    private String translateWord(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Input word must not be null.");
        }

        if (word.length() == 0) {
            return "";
        }

        // Hyphens are treated as two words
        int hyphenIndex = word.indexOf('-');
        if (hyphenIndex >= 0) {
            return translateWord(word.substring(0, hyphenIndex)) + "-" + translateWord(word.substring(hyphenIndex + 1));
        }

        UpperCaseAndPunctuation upperCaseAndPunctuation = getUpperCaseIndexesAndPunctuation(word);

        String wordInLowerCaseWithoutPunctuation = upperCaseAndPunctuation.getWordInLowerCaseWithoutPunctuation();

        String processedWord;

        // Words that end in “way” are not modified
        if (wordInLowerCaseWithoutPunctuation.endsWith("way")) {
            processedWord = wordInLowerCaseWithoutPunctuation;
        } else {
            char firstCharacter = wordInLowerCaseWithoutPunctuation.charAt(0);
            if (isVowel(firstCharacter)) {
                // Words that start with a vowel have the letters “way” added to the end
                processedWord = wordInLowerCaseWithoutPunctuation + "way";
            } else {
                // First character is neither a punctuation nor a vowel - we treat it as a consonant
                // Words that start with a consonant have their first letter moved to the end of the word
                // and the letters “ay” added to the end
                processedWord = wordInLowerCaseWithoutPunctuation.substring(1) + firstCharacter + "ay";
            }
        }

        return applyUpperCaseAndPunctuationRules(processedWord, upperCaseAndPunctuation.getUpperCaseIndexes(), upperCaseAndPunctuation.getPunctuations());
    }

    /**
     * Converts list of characters into String.
     */
    private static String charListToString(List<Character> chars) {
        StringBuilder builder = new StringBuilder();
        chars.forEach(builder::append);
        return builder.toString();
    }

    private static boolean isPunctuation(char ch) {
        return PUNCTUATIONS.indexOf(ch) >= 0;
    }

    private static boolean isVowel(char ch) {
        return VOWELS.indexOf(ch) >= 0;
    }

    /**
     * Punctuation that was found in the translated word.
     */
    private static class Punctuation {

        /**
         * Punctuation index from the end of the word (e.g. 2 for 're, 0 for ol').
         */
        private final int indexFromEnd;
        /**
         * Punctuation character.
         */
        private final char character;

        private Punctuation(int indexFromEnd, char character) {
            this.indexFromEnd = indexFromEnd;
            this.character = character;
        }

        private char getCharacter() {
            return character;
        }

        private int getIndexFromEnd() {
            return indexFromEnd;
        }
    }

    /**
     * Word in lower case with punctuations removed + list of punctuations + list of upper case character indexes.
     */
    private static class UpperCaseAndPunctuation {

        /**
         * Word in lowe case with the punctuations removed.
         */
        private final String wordInLowerCaseWithoutPunctuation;
        /**
         * Indexes of upper case characters.
         */
        private final List<Integer> upperCaseIndexes;
        /**
         * List of the removed punctuations (ordered by their index from 0 upwards).
         */
        private final List<Punctuation> punctuations;

        private UpperCaseAndPunctuation(String wordInLowerCaseWithoutPunctuation, List<Integer> upperCaseIndexes, List<Punctuation> punctuations) {
            this.wordInLowerCaseWithoutPunctuation = wordInLowerCaseWithoutPunctuation;
            this.upperCaseIndexes = upperCaseIndexes;
            this.punctuations = punctuations;
        }

        private List<Punctuation> getPunctuations() {
            return punctuations;
        }

        private List<Integer> getUpperCaseIndexes() {
            return upperCaseIndexes;
        }

        private String getWordInLowerCaseWithoutPunctuation() {
            return wordInLowerCaseWithoutPunctuation;
        }
    }
}
