package air.pollution;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

class Logger {
    private static ErrorLevel globalErrorLevel = ErrorLevel.DEBUG;

    private String loggerName;

    Logger(Object loggerObject) {
        loggerName = loggerObject.getClass().getSimpleName();
    }

    static void setLevel(ErrorLevel errorLevel) {
        globalErrorLevel = errorLevel;
    }

    void log(ErrorLevel errorLevel, Ansi message) {
        log(errorLevel, message.toString());
    }

    void log(ErrorLevel errorLevel, String message) {
        if (globalErrorLevel.contains(errorLevel)) {
            System.err.print(errorLevel.color);
            System.err.println(ansi()
                    .a(errorLevel)
                    .a(" [")
                    .a(loggerName)
                    .a("]:")
                    .reset()
                    .a(" ")
                    .a(message)
                    .reset());
        }

        if (errorLevel == ErrorLevel.FATAL) {
            System.exit(1);
        }
    }
}
