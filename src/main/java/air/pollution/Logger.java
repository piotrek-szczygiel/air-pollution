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

    void log(ErrorLevel errorLevel, String message) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        log(errorLevel, message, methodName);
    }

    private void log(ErrorLevel errorLevel, String message, String methodName) {
        if (globalErrorLevel == ErrorLevel.DISABLE) {
            return;
        }

        if (globalErrorLevel.contains(errorLevel)) {
            System.err.print(errorLevel.color);
            System.err.println(ansi()
                    .a(errorLevel)
                    .a(" [")
                    .a(loggerName)
                    .a(".")
                    .a(methodName)
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

    void log(ErrorLevel errorLevel, Ansi message) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();

        log(errorLevel, message.toString(), methodName);
    }
}
