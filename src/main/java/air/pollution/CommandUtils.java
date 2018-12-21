package air.pollution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CommandUtils {
    static private Logger logger = Logger.getLogger(CommandUtils.class);

    static List<SensorMeasurement> getMeasurementsInRange(Cache cache, Station station, Parameter parameter,
                                                          LocalDateTime since, LocalDateTime until) {

        logger.debug("collecting sensors for " + Format.stationName(station.getName()));
        List<Sensor> sensors = cache.getAllSensors(station.getId());

        if (sensors == null || sensors.size() < 1) {
            return null;
        }

        Sensor sensor = null;

        for (Sensor searchSensor : sensors) {
            if (searchSensor.getParameter() == parameter) {
                sensor = searchSensor;
            }
        }

        if (sensor == null) {
            logger.warn("unable to match any sensors for " + Format.stationName(station.getName()));
            return null;
        }

        logger.debug("collecting measurements for sensor with id " + Format.sensorId(sensor.getId()));
        List<SensorMeasurement> measurements = cache.getSensorMeasurements(sensor.getId());

        if (measurements == null) {
            logger.warn("there are no measurements for sensor with id " + Format.sensorId(sensor.getId()));
            return null;
        }

        List<SensorMeasurement> measurementsInRange = new ArrayList<>();

        for (SensorMeasurement measurement : measurements) {
            if (since != null && until != null) {
                if (!(measurement.getDate().compareTo(since) >= 0
                        && measurement.getDate().compareTo(until) <= 0)) {
                    continue;
                }
            } else {
                logger.error("invalid date objects");
                return null;
            }

            measurementsInRange.add(measurement);
        }

        if (measurementsInRange.size() < 1) {
            logger.warn("couldn't match any measurement");
        } else {
            logger.debug("collected " + Format.size(measurementsInRange.size()) + "~ measurements");
        }

        return measurementsInRange;
    }
}
