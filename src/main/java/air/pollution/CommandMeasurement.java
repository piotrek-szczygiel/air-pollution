package air.pollution;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class CommandMeasurement implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;
    private int top;

    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger;

    CommandMeasurement(Cache cache, List<Station> stations, List<Parameter> parameters,
                       int top, LocalDateTime date, LocalDateTime since, LocalDateTime until) {
        this.cache = cache;
        this.stations = stations;
        this.parameters = parameters;
        this.top = top;

        this.date = date;
        this.since = since;
        this.until = until;

        this.logger = new Logger(this);
    }

    @Override
    public void run() {
        for (Station station : stations) {
            logger.info("collecting sensors for " + station.getNameColored());
            List<Sensor> sensors = cache.getAllSensors(station.getId());

            if (sensors == null || sensors.size() < 1) {
                continue;
            }

            List<Sensor> matchingSensors = new ArrayList<>();

            for (Sensor searchSensor : sensors) {
                for (Parameter parameter : parameters) {
                    if (searchSensor.getParameter() == parameter) {
                        matchingSensors.add(searchSensor);
                    }
                }
            }

            if (matchingSensors.size() < 1) {
                logger.warn("unable to match any sensors for " + station.getNameColored());
                continue;
            }

            logger.debug("matched " + Format.size(matchingSensors.size())
                    + "~ sensors for " + station.getNameColored());

            for (Sensor sensor : matchingSensors) {
                logger.info("collecting measurements for sensor with id " + sensor.getIdColored());
                List<SensorMeasurement> measurements = cache.getSensorMeasurements(sensor.getId());

                if (measurements == null) {
                    logger.warn("there are no measurements for sensor with id " + sensor.getIdColored());
                    continue;
                }

                List<SensorMeasurement> measurementsInRange = new ArrayList<>();

                if (since == null) {
                    since = LocalDateTime.MIN;
                }

                if (until == null) {
                    until = LocalDateTime.MAX;
                }

                for (SensorMeasurement measurement : measurements) {
                    if (date != null) {
                        if (!date.isEqual(measurement.date)) {
                            continue;
                        }
                    } else if (since != null && until != null) {
                        if (!(measurement.date.compareTo(since) >= 0 && measurement.date.compareTo(until) <= 0)) {
                            continue;
                        }
                    } else {
                        logger.error("invalid date objects");
                        return;
                    }

                    measurementsInRange.add(measurement);
                }

                if (measurementsInRange.size() < 1) {
                    logger.warn("couldn't match any measurement");
                } else {
                    logger.debug("collected " + Format.size(measurementsInRange.size()) + "~ measurements");

                    System.out.println("Showing " + Format.size(measurementsInRange.size())
                            + " measurements of " + sensor.getParameterColored() + " for "
                            + station.getNameColored());

                    for (SensorMeasurement measurement : measurementsInRange) {
                        System.out.println(Format.format(measurement));
                    }
                }

                // Add new line if this measurements are not the last one
                if (stations.indexOf(station) != stations.size() - 1
                        || matchingSensors.indexOf(sensor) != matchingSensors.size() - 1) {
                    System.out.println();
                }
            }
        }
    }
}
