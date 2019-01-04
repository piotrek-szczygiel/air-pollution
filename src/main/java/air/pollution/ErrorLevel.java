package air.pollution;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Describes error levels and their colors.
 *
 * @see Logger
 */
enum ErrorLevel {

    DEBUG(0, ansi().fgBrightBlack()),
    INFO(1, ansi().fgBrightBlack()),
    WARN(2, ansi().fgYellow()),
    ERROR(3, ansi().fgBrightRed()),
    FATAL(4, ansi().bgBrightRed().fgBlack()),
    DISABLE(5, null);

    Ansi color;
    private Integer severity;


    ErrorLevel(int severity, Ansi color) {
        this.severity = severity;
        this.color = color;
    }


    /**
     * Check if given error level is lower or equal.
     *
     * @param other error level
     * @return is it lower or equal
     */
    boolean contains(ErrorLevel other) {
        return this.severity <= other.severity;
    }
}
