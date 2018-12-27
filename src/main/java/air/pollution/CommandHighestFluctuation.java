package air.pollution;

import static air.pollution.Format.format;

class CommandHighestFluctuation implements Command {
    private Logger logger = Logger.getLogger(this);

    @Override
    public void execute(Cache cache, Options options) {

        logger.info("showing highest fluctuating parameter for %s stations and %s parameters",
                format(options.stations.size()), format(options.parameters.size()));

        Float highestFluctuation = null;

        Float highestMinimal = null;
        Float highestMaximal = null;

        Parameter highestParameter = null;

        for (Parameter parameter : options.parameters) {
            Float average =
                    CommandUtils.getAveragePollution(
                            cache,
                            options.stations,
                            parameter,
                            options.since,
                            options.until);

            Float minimal =
                    CommandUtils.getMinimalPollution(
                            cache,
                            options.stations,
                            parameter,
                            options.since,
                            options.until);

            Float maximal =
                    CommandUtils.getMaximalPollution(
                            cache,
                            options.stations,
                            parameter,
                            options.since,
                            options.until);

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
            System.out.printf(
                    "%nHighest fluctuating parameter for %s station%s is %s with fluctuation ratio of %s.%n",
                    format(options.stations.size()),
                    (options.stations.size() > 1 ? "s" : ""),
                    format(highestParameter),
                    format(highestFluctuation));

            System.out.printf("%s oscillated between %s and %s.%n",
                    format(highestParameter),
                    format(highestMinimal, highestParameter, false),
                    format(highestMaximal, highestParameter, false));
        }
    }
}
