package air.pollution;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Custom made thread-safe logger with colorful output.
 * <p>
 * Logs are displayed in standard error output with timestamp and its caller.
 */
class Logger {

    private static Map<Object, Logger> LOGGERS = new ConcurrentHashMap<>();

    private static ErrorLevel ERROR_LEVEL = ErrorLevel.DEBUG;

    private static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");

    private ErrorLevel localErrorLevel = ERROR_LEVEL;
    private String loggerName;

    private Logger(Object loggerObject) {
        loggerName = loggerObject.getClass().getSimpleName();

        // Disable verbose logging for fetching classes because of useless spam they make
        // If something breaks in those classes and we want to see every fetching log
        // we can always comment this if statement.

        if (loggerName.equals("Cache") || loggerName.equals("ApiObjectCollector")) {
            localErrorLevel = ErrorLevel.ERROR;
        }

        LOGGERS.putIfAbsent(loggerObject, this);
    }

    private Logger(Class loggerClass) {
        loggerName = loggerClass.getSimpleName();

        LOGGERS.putIfAbsent(loggerClass, this);
    }

    /**
     * Return current logger for provided object.
     * <p>
     * If logger does not exists, create new one.
     *
     * @param loggerObject logger parent object
     * @return logger
     */
    static Logger getLogger(Object loggerObject) {
        return LOGGERS.getOrDefault(loggerObject, new Logger(loggerObject));
    }


    /**
     * Return current logger for provided class.
     * <p>
     * If logger does not exists, create new one.
     *
     * @param loggerClass logger parent class
     * @return logger
     */
    static Logger getLogger(Class loggerClass) {
        return LOGGERS.getOrDefault(loggerClass, new Logger(loggerClass));
    }


    /**
     * Set global error level for all loggers.
     *
     * @param errorLevel error level
     */
    static void setGlobalLevel(ErrorLevel errorLevel) {
        ERROR_LEVEL = errorLevel;
    }


    /**
     * Display debug log.
     *
     * @param format format
     * @param args   arguments
     */
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
            String[] strings = new String[args.length];
            for (int i = 0; i < args.length; i++) {
                strings[i] = (String) args[i] + errorLevel.color;
            }

            String message = String.format(format, (Object[]) strings);

            // Synchronization on System.err enables us to do thread-safe logging
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


    /**
     * Display information log.
     *
     * @param format format
     * @param args   arguments
     */
    void info(String format, Object... args) {
        log(ErrorLevel.INFO, format, args);
    }


    /**
     * Display warning log.
     *
     * @param format format
     * @param args   arguments
     */
    void warn(String format, Object... args) {
        log(ErrorLevel.WARN, format, args);
    }


    /**
     * Display error log.
     *
     * @param format format
     * @param args   arguments
     */
    void error(String format, Object... args) {
        log(ErrorLevel.ERROR, format, args);
    }


    /**
     * Display fatal error log.
     * <p>
     * After displaying the log, the program will exit with status code 1.
     *
     * @param format format
     * @param args   arguments
     */
    void fatal(String format, Object... args) {
        log(ErrorLevel.FATAL, format, args);
    }
}
