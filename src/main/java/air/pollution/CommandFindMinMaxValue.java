package air.pollution;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static air.pollution.Format.format;

class CommandFindMinMaxValue implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;

    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandFindMinMaxValue(Cache cache, List<Station> stations, List<Parameter> parameters,
                           LocalDateTime date, LocalDateTime since, LocalDateTime until) {
        this.cache = cache;
        this.stations = stations;
        this.parameters = parameters;

        this.date = date;
        this.since = since;
        this.until = until;
    }

    @Override
    @SuppressWarnings("Duplicates")
    public void run() {
        logger.info("looking for lowest and highest value for %s parameters in %s stations...",
                format(parameters.size()), format(stations.size()));

        if (date != null) {
            since = date;
            until = date;
        }

        for (Parameter parameter : parameters) {
            Station minStation = null;
            SensorMeasurement minMeasurement = null;

            Station maxStation = null;
            SensorMeasurement maxMeasurement = null;

            for (Station station : stations) {
                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(cache, station, parameter, since, until);

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

            if (minMeasurement != null) {
                System.out.printf("%nLowest measurement for %s parameter is %s in %s at %s",
                        format(parameter), format(minMeasurement.getValue(), minMeasurement.getParameter(), false),
                        format(minStation), format(minMeasurement.getDate()));
            }

            if (maxMeasurement != null) {
                System.out.printf("%nHighest measurement for %s parameter is %s in %s at %s",
                        format(parameter), format(maxMeasurement.getValue(), maxMeasurement.getParameter(), false),
                        format(maxStation), format(maxMeasurement.getDate()));
            }

            System.out.printf("%n");
        }
    }
}
