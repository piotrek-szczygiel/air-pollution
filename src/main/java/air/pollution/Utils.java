package air.pollution;

import java.io.OutputStream;
import java.io.PrintStream;

class Utils {
    static String normalizeString(final String input) {
        return input.toLowerCase().trim();
    }

    static void disableStdout() {
        System.setOut(new PrintStream(new OutputStream() {
            public void write(int b) {
                // NO-OP
            }
        }));
    }

    static void disableStderr() {
        System.setErr(new PrintStream(new OutputStream() {
            public void write(int b) {
                // NO-OP
            }
        }));
    }
}
