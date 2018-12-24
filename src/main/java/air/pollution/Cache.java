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

    void cacheStations(List<Station> stations, int numberOfThreads) {
        if (stations == null || stations.size() < 1) {
            logger.warn("there are no stations to fill cache for");

            return;
        }

        logger.debug("filling cache for %s stations...", Format.size(stations.size()));

        int processors;

        if (numberOfThreads <= 0) {
            processors = stations.size();
        } else {
            processors = numberOfThreads;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(processors);

        // fetching timeout in minutes
        final int timeout = 2;

        logger.info("fetching data from api using %s thread%s with timeout of %s minutes...",
                Format.size(processors), ((processors > 1) ? "s" : ""), Format.size(timeout));

        // Spinner animation
        AtomicInteger spinnerIndex = new AtomicInteger(0);

        suppressDebug();

        Stopwatch stopwatch = Stopwatch.createStarted();

        for (Station station : stations) {
            executorService.execute(() -> {
                synchronized (System.out) {
                    synchronized (System.err) {

                        // Get spinner animation current character
                        String spinner = Utils.getSpinner(spinnerIndex.incrementAndGet() / ((processors / 10) + 1));

                        System.err.print(ansi().cursorToColumn(0).toString() + Format.spinner(spinner)
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
            executorService.awaitTermination(timeout, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            restoreDebug();
            logger.fatal("executing tasks interrupted: %s", e);
        }

        stopwatch.stop();

        System.err.print(ansi().cursorToColumn(0).eraseLine().toString());

        restoreDebug();

        logger.info("fetching data from api finished in %s", Format.size(stopwatch));

        cacheDate = LocalDateTime.now();
    }

    void suppressDebug() {
        logger.setTemporaryLevel(ErrorLevel.INFO);
        Logger.getLogger(apiObjectCollector).setTemporaryLevel(ErrorLevel.INFO);
    }

    void setApiObjectCollector(ApiObjectCollector apiObjectCollector) {
        this.apiObjectCollector = apiObjectCollector;
    }

    LocalDateTime getCacheDate() {
        return cacheDate;
    }

    void restoreDebug() {
        logger.restorePreviousLevel();
        Logger.getLogger(apiObjectCollector).restorePreviousLevel();
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

            logger.debug("fetched %s sensors for station with id %s from cache",
                    Format.size(sensors.size()), Format.stationId(stationId));

            return sensors;
        }

        logger.debug("filling sensor cache for station with id %s...", Format.stationId(stationId));

        List<Sensor> sensors = apiObjectCollector.getAllSensors(stationId);

        if (sensors == null || sensors.size() < 1) {
            logger.warn("unable to fetch sensors for station with id %s from api", Format.stationId(stationId));
            return null;
        }

        sensorCache.put(stationId, sensors);
        return getAllSensors(stationId);
    }

    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        if (measurementCache.containsKey(sensorId)) {
            List<SensorMeasurement> measurements = measurementCache.get(sensorId);

            logger.debug("fetched %s measurements for sensor with id %s from cache",
                    Format.size(measurements.size()), Format.sensorId(sensorId));

            return measurements;
        }

        logger.debug("filling measurements cache for sensor with id %s...", Format.sensorId(sensorId));

        List<SensorMeasurement> measurements = apiObjectCollector.getSensorMeasurements(sensorId);

        if (measurements == null) {
            logger.warn("unable to fetch measurements for sensor with id %s from api", Format.sensorId(sensorId));
            return null;
        }

        measurementCache.put(sensorId, measurements);
        return getSensorMeasurements(sensorId);
    }

    AirIndex getAirIndex(int stationId) {
        if (airIndexCache.containsKey(stationId)) {
            logger.debug("fetched air index for station with id %s from cache", Format.stationId(stationId));

            return airIndexCache.get(stationId);
        }

        logger.debug("filling air index cache for station with id %s...", Format.stationId(stationId));

        AirIndex airIndex = apiObjectCollector.getAirIndex(stationId);

        if (airIndex == null) {
            logger.warn("unable to fetch air index for station with id %s from api", Format.stationId(stationId));
            return null;
        }

        airIndexCache.put(stationId, airIndex);
        return getAirIndex(stationId);
    }
}
