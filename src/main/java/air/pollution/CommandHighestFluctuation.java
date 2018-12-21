package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

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
        System.out.println();

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
                logger.warn("unable to calculate fluctuation for " + Format.parameter(parameter));

                continue;
            }

            float fluctuation = (maximal / average) + (minimal / average);

            if (highestFluctuation == null) {
                highestFluctuation = fluctuation;

                highestMinimal = minimal;
                highestMaximal = maximal;

                highestParameter = parameter;
            } else {
                highestFluctuation = Math.max(highestFluctuation, fluctuation);

                highestMinimal = minimal;
                highestMaximal = maximal;

                highestParameter = parameter;
            }
        }

        if (highestFluctuation == null) {
            logger.error("no highest fluctuation found");
        } else {
            System.out.println("Highest fluctuating parameter for " + Format.size(stations.size())
                    + " station" + (stations.size() > 1 ? "s" : "") + " is " + Format.parameter(highestParameter)
                    + " with fluctuation ratio of " + Format.size(String.format("%.2f", highestFluctuation)) + ".");

            System.out.println(Format.parameter(highestParameter) + " oscillated between "
                    + Format.measurementValue(highestParameter, highestMinimal, false) + " and "
                    + Format.measurementValue(highestParameter, highestMaximal, false) + ".");
        }
    }
}
