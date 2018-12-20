package air.pollution;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.Normalizer;
import java.util.regex.Pattern;

class Utils {
    static private String[] spinner = {
            "\u2801",
            "\u2802",
            "\u2804",
            "\u2840",
            "\u2880",
            "\u2820",
            "\u2810",
            "\u2808"
    };

    static String normalizeString(final String input) {
        return stripAccents(input.toLowerCase().trim());
    }

    private static String stripAccents(final String input) {
        if (input == null) {
            return null;
        }
        final Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");//$NON-NLS-1$
        final StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        convertRemainingAccentCharacters(decomposed);
        return pattern.matcher(decomposed).replaceAll("");
    }

    private static void convertRemainingAccentCharacters(final StringBuilder decomposed) {
        for (int i = 0; i < decomposed.length(); i++) {
            if (decomposed.charAt(i) == '\u0141') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'L');
            } else if (decomposed.charAt(i) == '\u0142') {
                decomposed.deleteCharAt(i);
                decomposed.insert(i, 'l');
            }
        }
    }

    static void disableStderr() {
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
            }
        }));
    }

    static void hideCursor(PrintStream printStream) {
        printStream.print("\u001b[?25l");
    }

    static void showCursor(PrintStream printStream) {
        printStream.print("\u001b[?25h");
    }

    static String getSpinner(int iteration) {
        return spinner[iteration % spinner.length];
    }
}
