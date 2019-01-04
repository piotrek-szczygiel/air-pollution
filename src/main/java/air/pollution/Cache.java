package air.pollution;

import com.google.common.base.Stopwatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static air.pollution.Format.format;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * Caches results from API.
 */
class Cache {
    // Timeout in minutes for cacheStations
    private final static int CACHE_STATIONS_TIMEOUT = 5;

    private LocalDateTime cacheDate;

    private Map<String, Station> stationCache = new ConcurrentHashMap<>();
    private Map<Integer, List<Sensor>> sensorCache = new ConcurrentHashMap<>();
    private Map<Integer, List<SensorMeasurement>> measurementCache = new ConcurrentHashMap<>();
    private Map<Integer, AirIndex> airIndexCache = new ConcurrentHashMap<>();

    private transient ApiObjectCollector apiObjectCollector;

    private transient Logger logger = Logger.getLogger(this);


    /**
     * Fetches and stores all possible information inside cache.
     *
     * @param stations        list of stations that we want to fetch the data for
     * @param numberOfThreads number of threads used for making API requests
     */
    void cacheStations(List<Station> stations, int numberOfThreads) {
        if (stations == null || stations.size() < 1) {
            logger.warn("there are no stations to fill cache for");

            return;
        }

        logger.debug("filling cache for %s stations...", format(stations.size()));

        int processors;

        if (numberOfThreads <= 0) {
            processors = stations.size();
        } else {
            processors = numberOfThreads;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(processors);

        logger.info("fetching data from api using %s thread%s with timeout of %s minutes...",
                format(processors),
                ((processors > 1) ? "s" : ""),
                format(CACHE_STATIONS_TIMEOUT));

        // Spinner animation
        AtomicInteger spinnerIndex = new AtomicInteger(0);

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (Station station : stations) {
            executorService.execute(() -> {

                // Synchronize output because this portion will be called from withing multiple threads
                synchronized (System.out) {
                    synchronized (System.err) {

                        // Get spinner animation current character
                        char spinner = Utils.getSpinner(spinnerIndex.incrementAndGet() / ((processors / 10) + 1));

                        System.err.printf("%s fetching %s%s\r",
                                format(spinner),
                                format(station),
                                ansi().eraseLine());
                    }
                }

                List<Sensor> sensors = getAllSensors(station.getId());

                if (sensors != null) {
                    for (Sensor sensor : sensors) {
                        getSensorMeasurements(sensor.getId());
                    }
                }

                getAirIndex(station.getId());
            });
        }

        executorService.shutdown();

        try {
            executorService.awaitTermination(CACHE_STATIONS_TIMEOUT, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.fatal("executing tasks interrupted: %s", e.toString());
        }

        stopwatch.stop();

        System.err.print(ansi().cursorToColumn(0).eraseLine().toString());

        logger.info("fetching data from api finished in %s", format(stopwatch));

        cacheDate = LocalDateTime.now();
    }

    /**
     * Set API collector dependency.
     *
     * @param apiObjectCollector API collector
     * @see ApiObjectCollector
     */
    void setApiObjectCollector(ApiObjectCollector apiObjectCollector) {
        this.apiObjectCollector = apiObjectCollector;
    }

    /**
     * Returns last cache update timestamp.
     *
     * @return date
     */
    LocalDateTime getCacheDate() {
        return cacheDate;
    }

    /**
     * Fetches list of all stations from cache.
     * <p>
     * If stations cache is empty it has to fetch them from API.
     *
     * @return list of stations
     * @see Station
     */
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

    /**
     * Fetches list of all sensors for specified station from cache.
     * <p>
     * If sensors cache is empty it has to fetch them from API.
     *
     * @param stationId id of station
     * @return list of sensors
     * @see Sensor
     */
    List<Sensor> getAllSensors(int stationId) {
        if (sensorCache.containsKey(stationId)) {
            List<Sensor> sensors = sensorCache.get(stationId);

            logger.debug("fetched %s sensors for station with id %s from cache",
                    format(sensors.size()), format(stationId));

            return sensors;
        }

        logger.debug("filling sensor cache for station with id %s...", format(stationId));

        List<Sensor> sensors = apiObjectCollector.getAllSensors(stationId);

        if (sensors == null) {
            logger.warn("unable to fetch sensors for station with id %s from api", format(stationId));
            return null;
        }

        sensorCache.put(stationId, sensors);
        return getAllSensors(stationId);
    }

    /**
     * Fetches list of all measurements for specified sensor from cache.
     * <p>
     * If measurements cache is empty it has to fetch them from API.
     *
     * @param sensorId id of sensor
     * @return list of measurements
     * @see SensorMeasurement
     */
    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        if (measurementCache.containsKey(sensorId)) {
            List<SensorMeasurement> measurements = measurementCache.get(sensorId);

            logger.debug("fetched %s measurements for sensor with id %s from cache",
                    format(measurements.size()), format(sensorId));

            return measurements;
        }

        logger.debug("filling measurements cache for sensor with id %s...", format(sensorId));

        List<SensorMeasurement> measurements = apiObjectCollector.getSensorMeasurements(sensorId);

        if (measurements == null) {
            logger.warn("unable to fetch measurements for sensor with id %s from api", format(sensorId));
            return null;
        }

        measurementCache.put(sensorId, measurements);
        return getSensorMeasurements(sensorId);
    }

    /**
     * Fetches air index for specified station from cache.
     * <p>
     * If air index cache is empty it has to fetch it from API.
     *
     * @param stationId id of station
     * @return air index
     * @see AirIndex
     */
    AirIndex getAirIndex(int stationId) {
        if (airIndexCache.containsKey(stationId)) {
            logger.debug("fetched air index for station with id %s from cache", format(stationId));

            return airIndexCache.get(stationId);
        }

        logger.debug("filling air index cache for station with id %s...", format(stationId));

        AirIndex airIndex = apiObjectCollector.getAirIndex(stationId);

        if (airIndex == null) {
            logger.warn("unable to fetch air index for station with id %s from api", format(stationId));
            return null;
        }

        airIndexCache.put(stationId, airIndex);
        return getAirIndex(stationId);
    }
}
