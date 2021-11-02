package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Locale;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     *
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim().toLowerCase(Locale.ROOT);
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        String preppedSentence = sentence.trim().toLowerCase(Locale.ROOT);

        if (preppedWord.split("\\s+").length == 1) {
            String[] wordsInPreppedSentence = preppedSentence.split("\\s+");
            return Arrays.stream(wordsInPreppedSentence)
                    .anyMatch(t -> t.contains(preppedWord));
        } else {
            return preppedSentence.contains(preppedWord);
        }
    }

    /**
     * Returns a detailed message of the t, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Returns Integer if {@code s} represents an unsigned integer
     * e.g. 0, 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return null for any other non-null string input
     * e.g. empty string, "-1", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static Integer getInt(String s) {
        requireNonNull(s);

        try {
            return Integer.parseInt(s); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static String clean(String s) {
        return s.strip().replaceAll("[\n|\r|\"|\f|\b|']", "").replaceAll("\\s+", " ").strip();
    }

    public static String clean(String s, String toRemove) {
        return clean(s).replaceAll(toRemove, "").strip();
    }

    /**
     * Checks if the given filename is a JSON file.
     *
     * @param fileName Name of the specified file.
     * @return True if the file is a JSON file, false otherwise.
     */
    public static boolean isJson(String fileName) {
        int length = fileName.length();
        if (length <= 5) {
            return false;
        }
        String lastFiveChars = fileName.substring(length - 5);
        return lastFiveChars.equalsIgnoreCase(".json");
    }

    /**
     * Checks if the given filename is a CSV file.
     *
     * @param fileName Name of the specified file.
     * @return True if the file is a CSV file, false otherwise.
     */
    public static boolean isCsv(String fileName) {
        int length = fileName.length();
        if (length <= 4) {
            return false;
        }
        String lastFiveChars = fileName.substring(length - 4);
        return lastFiveChars.equalsIgnoreCase(".csv");
    }
}
