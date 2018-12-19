package air.pollution;

import java.util.List;

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
        logger.debug("displaying air index for " + Format.size(stations.size()) + "~ stations");

        for (Station station : stations) {
            logger.debug("collecting air index for " + station.getNameColored());
            AirIndex airIndex = cache.getAirIndex(station.getId());

            logger.info("printing air index for " + station.getNameColored());

            System.out.println("Air index for " + station.getNameColored());
            System.out.println(Format.format(airIndex));

            // Add newline if current station is not the last one
            if (stations.indexOf(station) != stations.size() - 1) {
                System.out.println();
            }
        }
    }
}
