package air.pollution;

import java.util.List;

class CommandListAllStations implements Runnable {
    private Cache cache;

    private Logger logger = Logger.getLogger(this);

    CommandListAllStations(Cache cache) {
        this.cache = cache;
    }

    @Override
    public void run() {
        logger.info("collecting stations...");

        List<Station> stations = cache.getAllStations();

        if (stations == null || stations.size() < 1) {
            logger.error("no stations to list");

            return;
        }

        logger.info("listing %s stations...", Format.size(stations.size()));

        for (Station station : stations) {
            System.out.println(station.getName());
        }
    }
}
