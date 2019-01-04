package air.pollution;

import java.util.Collections;
import java.util.List;

import static air.pollution.Format.format;

/**
 * Command displaying lowest and highest values for specified stations and parameters.
 */
class CommandFindMinMaxValue implements Command {

    private Logger logger = Logger.getLogger(this);

    @Override
    @SuppressWarnings("Duplicates")
    public void execute(Cache cache, Options options) {

        logger.info("looking for lowest and highest value for %s parameters in %s stations...",
                format(options.parameters.size()),
                format(options.stations.size()));

        for (Parameter parameter : options.parameters) {
            Station minStation = null;
            SensorMeasurement minMeasurement = null;

            Station maxStation = null;
            SensorMeasurement maxMeasurement = null;

            for (Station station : options.stations) {
                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(
                                cache,
                                station,
                                parameter,
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

            boolean printed = false;

            if (minMeasurement != null) {
                System.out.printf("%nLowest measurement for %s parameter is %s in %s at %s",
                        format(parameter),
                        format(minMeasurement.getValue(), minMeasurement.getParameter(), false),
                        format(minStation),
                        format(minMeasurement.getDate()));

                printed = true;
            }

            if (maxMeasurement != null) {
                System.out.printf("%nHighest measurement for %s parameter is %s in %s at %s",
                        format(parameter),
                        format(maxMeasurement.getValue(), maxMeasurement.getParameter(), false),
                        format(maxStation),
                        format(maxMeasurement.getDate()));

                printed = true;
            }

            if (printed) {
                System.out.printf("%n");
            }
        }
    }
}
