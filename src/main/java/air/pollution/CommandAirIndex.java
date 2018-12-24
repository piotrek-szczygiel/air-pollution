package air.pollution;

import java.util.List;

import static air.pollution.Format.format;

class CommandAirIndex implements Runnable {
    private Cache cache;
    private List<Station> stations;

    private Logger logger = Logger.getLogger(this);

    CommandAirIndex(Cache cache, List<Station> stations) {
        this.cache = cache;
        this.stations = stations;
    }

    @Override
    public void run() {
        logger.debug("displaying air index for %s stations", format(stations.size()));

        for (Station station : stations) {
            logger.debug("collecting air index for %s", format(station));
            AirIndex airIndex = cache.getAirIndex(station.getId());

            logger.info("printing air index for %s", format(station));

            System.out.printf("%n%s%n", format(station));
            System.out.printf("%s%n", format(airIndex));
        }
    }

}
