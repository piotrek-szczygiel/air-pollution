package air.pollution;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.ansi;

enum ErrorLevel {
    DEBUG(0, ansi().fgBrightBlack()),
    INFO(1, ansi().fgBrightGreen()),
    WARN(2, ansi().fgYellow()),
    ERROR(3, ansi().fgBrightRed()),
    FATAL(4, ansi().bgBrightRed().fgBlack());

    Ansi color;
    private Integer severity;

    ErrorLevel(int severity, Ansi color) {
        this.severity = severity;
        this.color = color;
    }

    boolean contains(ErrorLevel other) {
        return this.severity <= other.severity;
    }
}
