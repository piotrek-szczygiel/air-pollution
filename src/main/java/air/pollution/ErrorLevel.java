package air.pollution;

import org.fusesource.jansi.Ansi;

import static org.fusesource.jansi.Ansi.Color;
import static org.fusesource.jansi.Ansi.ansi;

enum ErrorLevel {
    DEBUG(0, ansi().fg(Color.WHITE)),
    INFO(1, ansi().fg(Color.GREEN)),
    WARN(2, ansi().fg(Color.YELLOW)),
    ERROR(3, ansi().fg(Color.RED)),
    FATAL(4, ansi().bg(Color.RED).fg(Color.BLACK)),
    DISABLE(5, null);

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
