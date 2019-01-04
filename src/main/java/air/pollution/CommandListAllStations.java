package air.pollution;

import java.util.List;

import static air.pollution.Format.format;

/**
 * Command listing all available stations.
 */
class CommandListAllStations implements Command {

    private Logger logger = Logger.getLogger(this);

    @Override
    public void execute(Cache cache, Options options) {

        logger.info("collecting stations...");

        List<Station> stations = cache.getAllStations();

        if (stations == null || stations.size() < 1) {
            logger.error("no stations to list");

            return;
        }

        logger.info("listing %s stations...", format(stations.size()));

        for (Station station : stations) {
            System.out.printf("%s%n", station.getName());
        }
    }
}
