package air.pollution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static air.pollution.Format.format;

class CommandUtils {
    static private Logger logger = Logger.getLogger(CommandUtils.class);

    static LocalDateTime getLowestDate(Cache cache) {
        logger.debug("searching for lowest date in measurements...");

        LocalDateTime lowestDate = null;

        cache.suppressDebug();

        for (Station station : cache.getAllStations()) {
            for (Sensor sensor : cache.getAllSensors(station.getId())) {
                for (SensorMeasurement measurement : cache.getSensorMeasurements(sensor.getId())) {
                    if (lowestDate == null) {
                        lowestDate = measurement.getDate();
                    } else if (lowestDate.compareTo(measurement.getDate()) > 0) {
                        lowestDate = measurement.getDate();
                    }
                }
            }
        }

        cache.restoreDebug();

        if (lowestDate != null) {
            logger.debug("lowest date found: %s", format(lowestDate));
        } else {
            logger.warn("unable to found lowest date");
        }

        return lowestDate;
    }

    static LocalDateTime getHighestDate(Cache cache) {
        logger.debug("searching for highest date in measurements...");

        LocalDateTime highestDate = null;

        cache.suppressDebug();

        for (Station station : cache.getAllStations()) {
            for (Sensor sensor : cache.getAllSensors(station.getId())) {
                for (SensorMeasurement measurement : cache.getSensorMeasurements(sensor.getId())) {
                    if (highestDate == null) {
                        highestDate = measurement.getDate();
                    } else if (highestDate.compareTo(measurement.getDate()) < 0) {
                        highestDate = measurement.getDate();
                    }
                }
            }
        }

        cache.restoreDebug();

        if (highestDate != null) {
            logger.debug("highest date found: %s", format(highestDate));
        } else {
            logger.warn("unable to find highest date");
        }

        return highestDate;
    }

    static Float getAveragePollution(Cache cache, List<Station> stations, Parameter parameter,
                                     LocalDateTime since, LocalDateTime until) {

        float sum = 0.0f;
        int count = 0;

        logger.debug("calculating average %s pollution for %s stations",
                format(parameter), format(stations.size()));

        for (Station station : stations) {

            List<SensorMeasurement> measurements =
                    CommandUtils.getMeasurementsInRange(cache, station, parameter, since, until);

            if (measurements == null) {
                continue;
            }

            for (SensorMeasurement measurement : measurements) {
                sum += measurement.getValue();
                count++;
            }
        }

        if (count > 0) {
            return sum / count;
        }

        return null;
    }

    static List<SensorMeasurement> getMeasurementsInRange(Cache cache, Station station, Parameter parameter,
                                                          LocalDateTime since, LocalDateTime until) {

        // Usage of try is only needed to make sure we always restore logging to cache using finally clause
        try {
            cache.suppressDebug();

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
                return null;
            }

            List<SensorMeasurement> measurements = cache.getSensorMeasurements(sensor.getId());

            if (measurements == null) {
                logger.warn("there are no measurements for sensor with id %s", format(sensor));
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

            return measurementsInRange;

        } finally {
            cache.restoreDebug();
        }
    }

    static Float getMinimalPollution(Cache cache, List<Station> stations, Parameter parameter,
                                     LocalDateTime since, LocalDateTime until) {

        Float minimal = null;

        for (Station station : stations) {

            List<SensorMeasurement> measurements =
                    CommandUtils.getMeasurementsInRange(cache, station, parameter, since, until);

            if (measurements == null) {
                continue;
            }

            for (SensorMeasurement measurement : measurements) {
                if (minimal == null) {
                    minimal = measurement.getValue();
                } else {
                    minimal = Math.min(minimal, measurement.getValue());
                }
            }
        }

        if (minimal == null) {
            logger.warn("unable to find minimal value for %s", format(parameter));
        }

        return minimal;
    }

    static Float getMaximalPollution(Cache cache, List<Station> stations, Parameter parameter,
                                     LocalDateTime since, LocalDateTime until) {

        Float maximal = null;

        for (Station station : stations) {

            List<SensorMeasurement> measurements =
                    CommandUtils.getMeasurementsInRange(cache, station, parameter, since, until);

            if (measurements == null) {
                continue;
            }

            for (SensorMeasurement measurement : measurements) {
                if (maximal == null) {
                    maximal = measurement.getValue();
                } else {
                    maximal = Math.max(maximal, measurement.getValue());
                }
            }
        }

        if (maximal == null) {
            logger.warn("unable to find maximal value for %s", format(parameter));
        }

        return maximal;
    }
}
