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

import static org.fusesource.jansi.Ansi.ansi;

class Cache {
    private LocalDateTime cacheDate;

    private Map<String, Station> stationCache = new ConcurrentHashMap<>();
    private Map<Integer, List<Sensor>> sensorCache = new ConcurrentHashMap<>();
    private Map<Integer, List<SensorMeasurement>> measurementCache = new ConcurrentHashMap<>();
    private Map<Integer, AirIndex> airIndexCache = new ConcurrentHashMap<>();

    private transient ApiObjectCollector apiObjectCollector;

    private transient Logger logger = Logger.getLogger(this);

    void setApiObjectCollector(ApiObjectCollector apiObjectCollector) {
        this.apiObjectCollector = apiObjectCollector;
    }

    LocalDateTime getCacheDate() {
        return cacheDate;
    }

    void cacheStations(List<Station> stations, int numberOfThreads) {
        if (stations == null || stations.size() < 1) {
            logger.warn("there are no stations to fill cache for");

            return;
        }

        logger.debug("filling cache for " + Format.size(stations.size()) + "~ stations...");

        int processors;

        if (numberOfThreads <= 0) {
            processors = stations.size();
        } else {
            processors = numberOfThreads;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(processors);

        logger.info("fetching data from api using " + Format.size(processors) + "~ thread"
                + ((processors > 1) ? "s" : "") + " with timeout of " + Format.size(2) + "~ minutes...");

        logger.setTemporaryLevel(ErrorLevel.INFO);
        Logger.getLogger(apiObjectCollector).setTemporaryLevel(ErrorLevel.FATAL);

        // Spinner animation
        AtomicInteger spinnerIndex = new AtomicInteger(0);
        Utils.hideCursor(System.err);

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (Station station : stations) {
            executorService.execute(() -> {
                synchronized (System.out) {
                    synchronized (System.err) {

                        // Get spinner animation current character
                        String dot = Utils.getSpinner(spinnerIndex.incrementAndGet() / 8);

                        System.err.print(ansi().cursorToColumn(0).toString() + Format.spinner(dot)
                                + "  fetching " + Format.stationName(station.getName())
                                + ansi().eraseLine().toString());
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
            executorService.awaitTermination(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            logger.restorePreviousLevel();
            logger.error("executing tasks interrupted: " + e);
        }

        stopwatch.stop();

        System.err.print(ansi().cursorToColumn(0).eraseLine().toString());
        Utils.showCursor(System.err);

        logger.restorePreviousLevel();
        Logger.getLogger(apiObjectCollector).restorePreviousLevel();

        logger.info("fetching data from api finished in " + Format.size(stopwatch));

        cacheDate = LocalDateTime.now();
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

            logger.debug("fetched " + Format.size(sensors.size()) + "~ sensors for station with id "
                    + Format.stationId(stationId) + "~ from cache");

            return sensors;
        }

        logger.debug("filling sensor cache for station with id " + Format.stationId(stationId) + "~...");

        List<Sensor> sensors = apiObjectCollector.getAllSensors(stationId);

        if (sensors == null || sensors.size() < 1) {
            logger.warn("unable to fetch sensors for station with id " + Format.stationId(stationId) + "~ from API");
            return null;
        }

        sensorCache.put(stationId, sensors);
        return getAllSensors(stationId);
    }

    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        if (measurementCache.containsKey(sensorId)) {
            List<SensorMeasurement> measurements = measurementCache.get(sensorId);

            logger.debug("fetched " + Format.size(measurements.size()) + "~ measurements for sensor with id "
                    + Format.sensorId(sensorId) + "~ from cache");

            return measurements;
        }

        logger.debug("filling measurements cache for sensor with id " + Format.sensorId(sensorId) + "~...");

        List<SensorMeasurement> measurements = apiObjectCollector.getSensorMeasurements(sensorId);

        if (measurements == null) {
            logger.warn("unable to fetch measurements for sensor with id " + Format.sensorId(sensorId) + "~ from API");
            return null;
        }

        measurementCache.put(sensorId, measurements);
        return getSensorMeasurements(sensorId);
    }

    AirIndex getAirIndex(int stationId) {
        if (airIndexCache.containsKey(stationId)) {
            logger.debug("fetched air index for station with id " + Format.stationId(stationId) + "~ from cache");

            return airIndexCache.get(stationId);
        }

        logger.debug("filling air index cache for station with id " + Format.stationId(stationId) + "~...");

        AirIndex airIndex = apiObjectCollector.getAirIndex(stationId);

        if (airIndex == null) {
            logger.warn("unable to fetch air index for station with id " + Format.stationId(stationId) + "~ from API");
            return null;
        }

        airIndexCache.put(stationId, airIndex);
        return getAirIndex(stationId);
    }
}
