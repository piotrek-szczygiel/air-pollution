package air.pollution;

import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Cache {
    private ApiObjectCollector apiObjectCollector;

    private Map<String, Station> stationCache = new ConcurrentHashMap<>();
    private Map<Integer, List<Sensor>> sensorCache = new ConcurrentHashMap<>();
    private Map<Integer, List<SensorMeasurement>> measurementCache = new ConcurrentHashMap<>();
    private Map<Integer, AirIndex> airIndexCache = new ConcurrentHashMap<>();

    private Logger logger;

    Cache(ApiObjectCollector apiObjectCollector) {
        this.apiObjectCollector = apiObjectCollector;

        logger = new Logger(this);
    }

    void cacheAll() {
        logger.debug("filling whole cache...");

        ErrorLevel originalLevel = Logger.getLevel();
        Logger.setLevel(ErrorLevel.ERROR);

        List<Station> stations = getAllStations();

        if (stations == null) {
            return;
        }

        List<Thread> threads = new ArrayList<>();

        for (Station station : stations) {
            Thread thread = new Thread(() -> {
                List<Sensor> sensors = getAllSensors(station.getId());

                if (sensors != null) {
                    for (Sensor sensor : sensors) {
                        getSensorMeasurements(sensor.getId());
                    }
                }

                getAirIndex(station.getId());
            });

            thread.start();
            threads.add(thread);
        }

        for (Thread thread : ProgressBar.wrap(threads, "fetching all stations data...")) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("error while joining " + thread.getName() + ": " + e);
            }
        }

        Logger.setLevel(originalLevel);
    }

    List<Station> getAllStations() {
        if (stationCache.size() == 0) {
            List<Station> stations = apiObjectCollector.getAllStations();

            if (stations == null) {
                logger.error("unable to fill station cache");
                return null;
            }

            logger.debug("filling station cache...");
            for (Station station : stations) {
                stationCache.put(station.getName(), station);
            }
        }

        logger.debug("fetched all stations from cache");

        return new ArrayList<>(stationCache.values());
    }

    List<Sensor> getAllSensors(int stationId) {
        if (sensorCache.containsKey(stationId)) {
            List<Sensor> sensors = sensorCache.get(stationId);

            logger.debug("fetched " + Format.size(sensors.size()) + " sensors for station with id "
                    + Format.stationId(stationId) + " from cache");

            return sensors;
        }

        logger.debug("filling sensor cache for station with id " + Format.stationId(stationId) + "...");

        List<Sensor> sensors = apiObjectCollector.getAllSensors(stationId);

        if (sensors == null || sensors.size() < 1) {
            logger.warn("unable to fetch sensors for station with id " + Format.stationId(stationId) + " from API");
            return null;
        }

        sensorCache.put(stationId, sensors);
        return getAllSensors(stationId);
    }

    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        if (measurementCache.containsKey(sensorId)) {
            List<SensorMeasurement> measurements = measurementCache.get(sensorId);

            logger.debug("fetched " + Format.size(measurements.size()) + " measurements for sensor with id "
                    + Format.sensorId(sensorId) + " from cache");

            return measurements;
        }

        logger.debug("filling measurements cache for sensor with id " + Format.sensorId(sensorId) + "...");

        List<SensorMeasurement> measurements = apiObjectCollector.getSensorMeasurements(sensorId);

        if (measurements == null) {
            logger.warn("unable to fetch measurements for sensor with id " + Format.sensorId(sensorId) + " from API");
            return null;
        }

        measurementCache.put(sensorId, measurements);
        return getSensorMeasurements(sensorId);
    }

    AirIndex getAirIndex(int stationId) {
        if (airIndexCache.containsKey(stationId)) {
            logger.debug("fetched air index for station with id " + Format.stationId(stationId) + " from cache");

            return airIndexCache.get(stationId);
        }

        logger.debug("filling air index cache for station with id " + Format.stationId(stationId) + "...");

        AirIndex airIndex = apiObjectCollector.getAirIndex(stationId);

        if (airIndex == null) {
            logger.warn("unable to fetch air index for station with id" + Format.stationId(stationId) + " from API");
            return null;
        }

        airIndexCache.put(stationId, airIndex);
        return getAirIndex(stationId);
    }
}
