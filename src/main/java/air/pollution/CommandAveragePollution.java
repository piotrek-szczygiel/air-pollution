package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

import static air.pollution.Format.format;

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
        logger.info("showing average pollution for %s stations and %s parameters",
                format(stations.size()), format(parameters.size()));

        System.out.printf("%nAverage pollution for %s station%s%n",
                format(stations.size()), (stations.size() > 1 ? "s" : ""));

        if (date != null) {
            since = date;
            until = date;
        }

        for (Parameter parameter : parameters) {
            Float average = CommandUtils.getAveragePollution(cache, stations, parameter, since, until);

            if (average == null) {
                System.out.printf("%s:\t-%n", format(parameter));
            } else {
                System.out.printf("%s:\t%s%n", format(parameter),
                        format(average, parameter, false));
            }
        }
    }
}
