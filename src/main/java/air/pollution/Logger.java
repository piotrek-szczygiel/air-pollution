package air.pollution;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.fusesource.jansi.Ansi.ansi;

class Logger {
    private static Map<Object, Logger> LOGGERS = new ConcurrentHashMap<>();

    private static ErrorLevel ERROR_LEVEL = ErrorLevel.DEBUG;
    private static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    private ErrorLevel localErrorLevel = ERROR_LEVEL;
    private ErrorLevel lastErrorLevel = null;
    private String loggerName;

    private Logger(Object loggerObject) {
        loggerName = loggerObject.getClass().getSimpleName();

        LOGGERS.putIfAbsent(loggerObject, this);
    }

    static Logger getLogger(Object loggerObject) {
        return LOGGERS.getOrDefault(loggerObject, new Logger(loggerObject));
    }

    static void setGlobalLevel(ErrorLevel errorLevel) {
        ERROR_LEVEL = errorLevel;
    }

    void setTemporaryLevel(ErrorLevel errorLevel) {
        lastErrorLevel = localErrorLevel;
        localErrorLevel = errorLevel;
    }

    void restorePreviousLevel() {
        if (lastErrorLevel != null) {
            localErrorLevel = lastErrorLevel;
            lastErrorLevel = null;
        }
    }

    void debug(Object message) {
        log(ErrorLevel.DEBUG, message);
    }

    private void log(ErrorLevel errorLevel, Object message) {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

        log(errorLevel, message.toString(), methodName);
    }

    private void log(ErrorLevel errorLevel, String message, String methodName) {
        if (localErrorLevel == ErrorLevel.DISABLE) {
            return;
        }

        if (localErrorLevel.contains(errorLevel)) {
            // '~' in logging message resets color to default for current error level
            message = message.replace("~", ansi().reset().a(errorLevel.color).toString());

            // Synchronization on System.err enables us to do multithreaded logging
            synchronized (System.err) {
                System.err.println(ansi()
                        .a(errorLevel.color)
                        .a(ZonedDateTime.now().format(TIME_FORMATTER))
                        .a(" ")
                        .a(errorLevel)
                        .a(" [")
                        .a(loggerName)
                        .a(".")
                        .a(methodName)
                        .a("]: ")
                        .a(message)
                        .reset());
            }
        }

        if (errorLevel == ErrorLevel.FATAL) {
            // Don't exit while printing
            synchronized (System.err) {
                System.exit(1);
            }
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
