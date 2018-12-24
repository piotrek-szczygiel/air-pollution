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
    private static boolean SUPPRESS = false;
    private ErrorLevel localErrorLevel = ERROR_LEVEL;
    private String loggerName;

    private Logger(Object loggerObject) {
        loggerName = loggerObject.getClass().getSimpleName();

        LOGGERS.putIfAbsent(loggerObject, this);
    }

    private Logger(Class loggerClass) {
        loggerName = loggerClass.getSimpleName();

        LOGGERS.putIfAbsent(loggerClass, this);
    }

    static Logger getLogger(Object loggerObject) {
        return LOGGERS.getOrDefault(loggerObject, new Logger(loggerObject));
    }

    static Logger getLogger(Class loggerClass) {
        return LOGGERS.getOrDefault(loggerClass, new Logger(loggerClass));
    }

    static void setGlobalLevel(ErrorLevel errorLevel) {
        ERROR_LEVEL = errorLevel;
    }

    static void suppress() {
        SUPPRESS = true;
    }

    static void restore() {
        SUPPRESS = false;
    }

    void debug(String format, Object... args) {
        log(ErrorLevel.DEBUG, format, args);
    }

    private void log(ErrorLevel errorLevel, String format, Object... args) {
        String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();

        log(errorLevel, methodName, format, args);
    }

    private void log(ErrorLevel errorLevel, String methodName, String format, Object... args) {
        if (localErrorLevel == ErrorLevel.DISABLE) {
            return;
        }

        if (localErrorLevel.contains(errorLevel)) {
            if (!(SUPPRESS && ErrorLevel.ERROR.contains(errorLevel))) {
                return;
            }

            String[] strings = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                strings[i] = (String) args[i] + errorLevel.color;
            }

            String message = String.format(format, (Object[]) strings);

            // Synchronization on System.err enables us to do multithreaded logging
            synchronized (System.err) {
                System.err.printf("%s%s %s [%s.%s]: %s%s%n",
                        errorLevel.color,
                        ZonedDateTime.now().format(TIME_FORMATTER),
                        errorLevel,
                        loggerName,
                        methodName,
                        message,
                        ansi().reset());
            }
        }

        if (errorLevel == ErrorLevel.FATAL) {
            // Synchronization, because we don't want to exit in the middle of logging maybe important message
            synchronized (System.err) {
                System.exit(1);
            }
        }
    }

    void info(String format, Object... args) {
        log(ErrorLevel.INFO, format, args);
    }

    void warn(String format, Object... args) {
        log(ErrorLevel.WARN, format, args);
    }

    void error(String format, Object... args) {
        log(ErrorLevel.ERROR, format, args);
    }

    void fatal(String format, Object... args) {
        log(ErrorLevel.FATAL, format, args);
    }
}
