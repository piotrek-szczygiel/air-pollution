package air.pollution;

import java.util.Collections;
import java.util.List;

import static air.pollution.Format.format;

class CommandFindMinMaxParameter implements Command {
    private Logger logger = Logger.getLogger(this);

    @Override
    @SuppressWarnings("Duplicates")
    public void execute(Cache cache, Options options) {

        logger.info("looking for lowest and highest parameter values in %s stations...",
                format(options.stations.size()));

        Station minStation = null;
        SensorMeasurement minMeasurement = null;

        Station maxStation = null;
        SensorMeasurement maxMeasurement = null;

        for (Station station : options.stations) {
            for (Sensor sensor : cache.getAllSensors(station.getId())) {

                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(
                                cache,
                                station,
                                sensor.getParameter(),
                                options.since,
                                options.until);

                if (measurements == null || measurements.size() < 1) {
                    continue;
                }

                SensorMeasurement currentMaxMeasurement = Collections.max(measurements);
                SensorMeasurement currentMinMeasurement = Collections.min(measurements);

                if (minStation == null || currentMinMeasurement.getValue() < minMeasurement.getValue()) {
                    minStation = station;
                    minMeasurement = currentMinMeasurement;
                }

                if (maxStation == null || currentMaxMeasurement.getValue() > maxMeasurement.getValue()) {
                    maxStation = station;
                    maxMeasurement = currentMaxMeasurement;
                }
            }
        }

        if (minMeasurement != null) {
            System.out.printf("%nParameter with lowest value is %s measured in %s: %s",
                    format(minMeasurement.getParameter()),
                    format(minStation),
                    format(minMeasurement.getValue(), minMeasurement.getParameter(), false));
        } else {
            logger.error("unable to find parameter with lowest value");
        }

        if (maxMeasurement != null) {
            System.out.printf("%nParameter with highest value is %s measured in %s: %s",
                    format(maxMeasurement.getParameter()),
                    format(maxStation),
                    format(maxMeasurement.getValue(), maxMeasurement.getParameter(), false));
        } else {
            logger.error("unable to find parameter with highest value");
        }

        System.out.printf("%n");
    }
}
