package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

import static air.pollution.Format.format;

class CommandHighestFluctuation implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;

    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandHighestFluctuation(Cache cache, List<Station> stations, List<Parameter> parameters,
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
        logger.info("showing highest fluctuating parameter for %s stations and %s parameters",
                format(stations.size()), format(parameters.size()));

        if (date != null) {
            since = date;
            until = date;
        }

        Float highestFluctuation = null;

        Float highestMinimal = null;
        Float highestMaximal = null;

        Parameter highestParameter = null;

        for (Parameter parameter : parameters) {
            Float average = CommandUtils.getAveragePollution(cache, stations, parameter, since, until);

            Float minimal = CommandUtils.getMinimalPollution(cache, stations, parameter, since, until);
            Float maximal = CommandUtils.getMaximalPollution(cache, stations, parameter, since, until);

            if (average == null || minimal == null || maximal == null) {
                logger.warn("unable to calculate fluctuation for %s", format(parameter));

                continue;
            }

            float fluctuation = (maximal / average) + (minimal / average);

            if (highestFluctuation == null) {
                highestFluctuation = fluctuation;

                highestMinimal = minimal;
                highestMaximal = maximal;

                highestParameter = parameter;
            } else if (fluctuation > highestFluctuation) {
                highestFluctuation = fluctuation;

                highestMinimal = minimal;
                highestMaximal = maximal;

                highestParameter = parameter;
            }
        }

        if (highestFluctuation == null) {
            logger.error("no highest fluctuation found");
        } else {
            System.out.printf("%nHighest fluctuating parameter for %s station%s is %s with fluctuation ratio of %s.%n",
                    format(stations.size()), (stations.size() > 1 ? "s" : ""),
                    format(highestParameter), format(highestFluctuation));

            System.out.printf("%s oscillated between %s and %s.%n", format(highestParameter),
                    format(highestMinimal, highestParameter, false),
                    format(highestMaximal, highestParameter, false));
        }
    }
}
