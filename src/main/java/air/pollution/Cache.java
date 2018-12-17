package air.pollution;

import me.tongfei.progressbar.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Cache {
    private ApiObjectCollector apiObjectCollector;

    private Map<String, Station> stationCache = new ConcurrentHashMap<>();
    private Map<Integer, List<Sensor>> sensorCache = new ConcurrentHashMap<>();

    private Logger logger;

    Cache(ApiObjectCollector apiObjectCollector) {
        this.apiObjectCollector = apiObjectCollector;

        logger = new Logger(this);
    }

    void fillWholeCache() {
        logger.debug("filling whole cache...");

        ErrorLevel originalLevel = Logger.getLevel();
        Logger.setLevel(ErrorLevel.ERROR);

        List<Station> stations = getAllStations();

        AtomicInteger counter = new AtomicInteger(0);

        List<Thread> threads = new ArrayList<>();

        for (Station station : stations) {
            Thread thread = new Thread(() -> {
                List<Sensor> sensors = getAllSensors(station);

                for (Sensor sensor : sensors) {
                    fillSensorMeasurements(sensor);
                }
            });

            thread.start();
            threads.add(thread);
        }

        for (Thread thread : ProgressBar.wrap(threads, "fetching all stations data...")) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.fatal(e);
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

    Station getStation(String stationName) {
        if (stationCache.containsKey(stationName)) {
            Station station = stationCache.get(stationName);

            logger.debug("fetched " + station.getNameColored() + " with id "
                    + station.getIdColored() + " from cache");

            return station;
        }

        if (stationCache.size() == 0) {
            if (getAllStations() != null) {
                return getStation(stationName);
            }

            return null;
        }

        logger.error("unable to fetch station: " + Format.stationName(stationName));

        return null;
    }

    private List<Sensor> getAllSensors(Station station) {
        if (sensorCache.containsKey(station.getId())) {
            List<Sensor> sensors = sensorCache.get(station.getId());

            logger.debug("fetched " + Format.size(sensors.size()) + " sensors for "
                    + station.getNameColored() + " from cache");

            return sensors;
        }

        logger.debug("filling sensor cache for " + station.getNameColored() + "...");

        List<Sensor> sensors = apiObjectCollector.getAllSensors(station.getId());

        if (sensors == null || sensors.size() < 1) {
            logger.warn("unable to fetch sensors for " + station.getNameColored() + " from API");
        }

        sensorCache.put(station.getId(), sensors);
        return getAllSensors(station);
    }

    Sensor getSensor(Station station, Parameter parameter) {
        if (sensorCache.containsKey(station.getId())) {
            List<Sensor> sensors = sensorCache.get(station.getId());

            for (Sensor sensor : sensors) {
                if (sensor.getParameter() == parameter) {
                    logger.debug("fetched " + Format.parameter(parameter) + " sensor for "
                            + station.getNameColored() + " from cache");

                    return sensor;
                }
            }

            logger.error(station.getNameColored() + " does not have "
                    + Format.parameter(parameter) + " sensor");

            return null;
        }

        if (getAllSensors(station) != null) {
            return getSensor(station, parameter);
        }

        logger.error("unable to fetch " + Format.parameter(parameter) + " sensor for "
                + station.getNameColored() + " from cache");

        return null;
    }

    boolean fillSensorMeasurements(Sensor sensor) {
        if (sensor.getMeasurements() != null) {
            logger.debug("measurements for " + Format.parameter(sensor.getParameter()) + " sensor with id "
                    + sensor.getIdColored() + " are already filled");

            return true;
        }

        List<SensorMeasurement> measurements = apiObjectCollector.getSensorMeasurements(sensor.getId());

        if (measurements != null) {
            sensor.setMeasurements(measurements);

            logger.debug("filled measurements for " + Format.parameter(sensor.getParameter())
                    + " sensor with id " + sensor.getIdColored());

            return true;
        }

        logger.error("unable to fetch measurements for " + Format.parameter(sensor.getParameter())
                + " sensor with id " + sensor.getIdColored());

        // Set empty array, so cache doesn't try to download it every time
        sensor.setMeasurements(new ArrayList<>());
        return false;
    }
}
