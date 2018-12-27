package air.pollution;

import static air.pollution.Format.format;

class CommandAveragePollution implements Command {
    private Logger logger = Logger.getLogger(this);

    @Override
    public void execute(Cache cache, Options options) {

        logger.info("showing average pollution for %s stations and %s parameters",
                format(options.stations.size()),
                format(options.parameters.size()));

        System.out.printf("%nAverage pollution for %s station%s%n",
                format(options.stations.size()),
                (options.stations.size() > 1 ? "s" : ""));

        for (Parameter parameter : options.parameters) {
            Float average =
                    CommandUtils.getAveragePollution(
                            cache,
                            options.stations,
                            parameter,
                            options.since,
                            options.until);

            if (average == null) {
                System.out.printf("%s:\t-%n", format(parameter));
            } else {
                System.out.printf("%s:\t%s%n",
                        format(parameter),
                        format(average, parameter, false));
            }
        }
    }
}
