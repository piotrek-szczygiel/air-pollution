package air.pollution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static air.pollution.Format.format;

/**
 * Various helper methods used in other commands.
 */
class CommandUtils {
    static private Logger logger = Logger.getLogger(CommandUtils.class);

    /**
     * Repeat given string N times.
     *
     * @param string string to repeat
     * @param count  number of repetitions
     * @return repeated string
     */
    static String repeatString(String string, int count) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < count; i++) {
            stringBuilder.append(string);
        }

        return stringBuilder.toString();
    }

    /**
     * Find earliest date in cache.
     * <p>
     * Used to determine minimal {@link Options#since} value.
     *
     * @param cache cache
     * @return lowest date
     */
    static LocalDateTime getLowestDate(Cache cache) {
        logger.debug("searching for lowest date in measurements...");

        LocalDateTime lowestDate = null;

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

        if (lowestDate != null) {
            logger.debug("lowest date found: %s", format(lowestDate));
        } else {
            logger.warn("unable to found lowest date");
        }

        return lowestDate;
    }

    /**
     * Find latest date in cache.
     * <p>
     * Used to determine maximal {@link Options#until} value.
     *
     * @param cache cache
     * @return highest date
     */
    static LocalDateTime getHighestDate(Cache cache) {
        logger.debug("searching for highest date in measurements...");

        LocalDateTime highestDate = null;

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

        if (highestDate != null) {
            logger.debug("highest date found: %s", format(highestDate));
        } else {
            logger.warn("unable to find highest date");
        }

        return highestDate;
    }

    /**
     * Calculate average pollution of provided parameter.
     *
     * @param cache     cache
     * @param stations  list of stations
     * @param parameter parameter
     * @param since     lower date bound
     * @param until     higher date bound
     * @return average pollution value
     */
    static Float getAveragePollution(Cache cache, List<Station> stations, Parameter parameter,
                                     LocalDateTime since, LocalDateTime until) {

        float sum = 0.0f;
        int count = 0;

        for (Station station : stations) {

            List<SensorMeasurement> measurements =
                    CommandUtils.getMeasurementsInRange(
                            cache,
                            station,
                            parameter,
                            since,
                            until);

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

    /**
     * Return list of measurements for given station and parameter between specified dates.
     *
     * @param cache     cache
     * @param station   station
     * @param parameter parameter
     * @param since     lower date bound
     * @param until     higher date bound
     * @return list of measurements
     */
    static List<SensorMeasurement> getMeasurementsInRange(Cache cache, Station station, Parameter parameter,
                                                          LocalDateTime since, LocalDateTime until) {

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
    }

    /**
     * Get minimal pollution value of parameter.
     *
     * @param cache     cache
     * @param stations  list of stations
     * @param parameter parameter
     * @param since     lower date bound
     * @param until     higher date bound
     * @return minimal pollution value
     */
    static Float getMinimalPollution(Cache cache, List<Station> stations, Parameter parameter,
                                     LocalDateTime since, LocalDateTime until) {

        Float minimal = null;

        for (Station station : stations) {

            List<SensorMeasurement> measurements =
                    CommandUtils.getMeasurementsInRange(
                            cache,
                            station,
                            parameter,
                            since,
                            until);

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

    /**
     * Get maximal pollution value of parameter.
     *
     * @param cache     cache
     * @param stations  stations
     * @param parameter parameter
     * @param since     lower date bound
     * @param until     higher date bound
     * @return maximal pollution value
     */
    static Float getMaximalPollution(Cache cache, List<Station> stations, Parameter parameter,
                                     LocalDateTime since, LocalDateTime until) {

        Float maximal = null;

        for (Station station : stations) {

            List<SensorMeasurement> measurements =
                    CommandUtils.getMeasurementsInRange(
                            cache,
                            station,
                            parameter,
                            since,
                            until);

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

    /**
     * Simple utility returning how many measurements/stations should you show.
     * <p>
     * If top is equal to zero, you should show everything you have.
     * Otherwise you should show at most size.
     *
     * @param top  maximum number of measurements/stations to show
     * @param size size of your list
     * @return how many should you show
     */
    static int howManyToShow(int top, int size) {
        if (top == 0) {
            return size;
        }

        return Math.min(top, size);
    }
}
