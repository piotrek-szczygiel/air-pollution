package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Stores information about options provided through command line arguments.
 */
class Options {
    List<Station> stations;
    List<Parameter> parameters;

    LocalDateTime since;
    LocalDateTime until;

    LocalDateTime today;

    int top;

    int hourSince;
    int hourUntil;

    boolean verbose;

    Options(List<Station> stations, List<Parameter> parameters,
            LocalDateTime since, LocalDateTime until, LocalDateTime today,
            int top, int hourSince, int hourUntil, boolean verbose) {

        this.stations = stations;
        this.parameters = parameters;
        this.since = since;
        this.until = until;
        this.today = today;
        this.top = top;
        this.hourSince = hourSince;
        this.hourUntil = hourUntil;
        this.verbose = verbose;
    }
}
