package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

class CommandAveragePollution implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;

    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandAveragePollution(Cache cache, List<Station> stations, List<Parameter> parameters,
                            LocalDateTime date, LocalDateTime since, LocalDateTime until) {
        this.cache = cache;
        this.stations = stations;
        this.parameters = parameters;

        this.date = date;
        this.since = since;
        this.until = until;
    }

    @Override
    public void run() {
        System.out.println();

        logger.info("showing average pollution for " + Format.size(stations.size()) + "~ stations and "
                + Format.size(parameters.size()) + "~ parameters");

        System.out.println("Average pollution for " + Format.size(stations.size()) + " station"
                + (stations.size() > 1 ? "s" : ""));

        if (date != null) {
            since = date;
            until = date;
        }

        for (Parameter parameter : parameters) {
            Float average = CommandUtils.getAveragePollution(cache, stations, parameter, since, until);

            if (average == null) {
                System.out.println(Format.parameter(parameter) + ":\t-");
            } else {
                System.out.println(Format.parameter(parameter) + ":\t"
                        + Format.measurementValue(parameter, average, false));
            }
        }
    }
}
