package air.pollution;

import static org.fusesource.jansi.Ansi.ansi;

class Logger {
    private static ErrorLevel globalErrorLevel = ErrorLevel.DEBUG;

    private String loggerName;

    Logger(Object loggerObject) {
        loggerName = loggerObject.getClass().getSimpleName();
    }

    static ErrorLevel getLevel() {
        return globalErrorLevel;
    }

    static void setLevel(ErrorLevel errorLevel) {
        globalErrorLevel = errorLevel;
    }

    void debug(Object message) {
        log(ErrorLevel.DEBUG, message);
    }

    private void log(ErrorLevel errorLevel, Object message) {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

        log(errorLevel, message.toString(), methodName);
    }

    private void log(ErrorLevel errorLevel, String message, String methodName) {
        if (errorLevel == ErrorLevel.DISABLE) {
            return;
        }

        if (globalErrorLevel.contains(errorLevel)) {
            synchronized (System.err) {
                System.err.println(ansi()
                        .a(errorLevel.color)
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
        }

        if (errorLevel == ErrorLevel.FATAL) {
            System.exit(1);
        }
    }

    void info(Object message) {
        log(ErrorLevel.INFO, message);
    }

    void warn(Object message) {
        log(ErrorLevel.WARN, message);
    }

    void error(Object message) {
        log(ErrorLevel.ERROR, message);
    }

    void fatal(Object message) {
        log(ErrorLevel.FATAL, message);
    }
}
