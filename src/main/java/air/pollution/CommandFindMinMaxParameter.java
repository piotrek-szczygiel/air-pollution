package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

import static air.pollution.Format.format;

class CommandFindMinMaxParameter implements Runnable {
    private Cache cache;
    private List<Station> stations;

    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandFindMinMaxParameter(Cache cache, List<Station> stations, LocalDateTime date,
                               LocalDateTime since, LocalDateTime until) {
        this.cache = cache;
        this.stations = stations;

        this.date = date;
        this.since = since;
        this.until = until;
    }

    @Override
    public void run() {
        logger.info("looking for lowest and highest parameter values in %s stations...", format(stations.size()));

        if (date != null) {
            since = date;
            until = date;
        }

        Station minStation = null;
        SensorMeasurement minMeasurement = null;

        Station maxStation = null;
        SensorMeasurement maxMeasurement = null;

        for (Station station : stations) {
            for (Sensor sensor : cache.getAllSensors(station.getId())) {

                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(cache, station, sensor.getParameter(), since, until);

                if (measurements == null) {
                    continue;
                }

                for (SensorMeasurement measurement : measurements) {
                    if (minMeasurement == null || measurement.getValue() < minMeasurement.getValue()) {
                        minStation = station;
                        minMeasurement = measurement;
                    }

                    if (maxMeasurement == null || measurement.getValue() > maxMeasurement.getValue()) {
                        maxStation = station;
                        maxMeasurement = measurement;
                    }
                }
            }
        }

        if (minMeasurement != null) {
            System.out.printf("%nParameter with lowest value is %s measured in %s: %s",
                    format(minMeasurement.getParameter()), format(minStation),
                    format(minMeasurement.getParameter(), minMeasurement.getValue(), false));
        } else {
            logger.error("unable to find parameter with lowest value");
        }

        if (maxMeasurement != null) {
            System.out.printf("%nParameter with highest value is %s measured in %s: %s",
                    format(maxMeasurement.getParameter()), format(maxStation),
                    format(maxMeasurement.getParameter(), maxMeasurement.getValue(), false));
        } else {
            logger.error("unable to find parameter with highest value");
        }

        System.out.printf("%n");
    }
}
