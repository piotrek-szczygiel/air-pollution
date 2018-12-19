package air.pollution;

import java.io.OutputStream;
import java.io.PrintStream;

class Utils {
    static String normalizeString(final String input) {
        return input.toLowerCase().trim();
    }

    static void disableOutput() {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // NO-OP
            }
        }));
    }
}
