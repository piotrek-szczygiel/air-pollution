package air.pollution;

import static air.pollution.Format.format;

class CommandAirIndex implements Command {
    private Logger logger = Logger.getLogger(this);

    @Override
    public void execute(Cache cache, Options options) {

        logger.debug("displaying air index for %s stations", format(options.stations.size()));

        for (Station station : options.stations) {
            logger.debug("collecting air index for %s", format(station));
            AirIndex airIndex = cache.getAirIndex(station.getId());

            logger.info("printing air index for %s", format(station));

            System.out.printf("%n%s%n", format(station));
            System.out.printf("%s%n", format(airIndex));
        }
    }

}
