package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

class CommandAveragePollution implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;

    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandAveragePollution(Cache cache, List<Station> stations, List<Parameter> parameters,
                            LocalDateTime since, LocalDateTime until) {
        this.cache = cache;
        this.stations = stations;
        this.parameters = parameters;

        this.since = since;
        this.until = until;
    }

    @Override
    public void run() {
        System.out.println();

        logger.info("showing average pollution for " + Format.size(stations.size()) + "~ stations and "
                + Format.size(parameters.size()) + "~ parameters");

        System.out.println("Average pollution for " + Format.size(stations.size()) + " stations between "
                + Format.timestampDate(since) + " and " + Format.timestampDate(until));

        for (Parameter parameter : parameters) {
            float sum = 0.0f;
            int count = 0;

            logger.info("calculating average " + Format.parameter(parameter) + "~ pollution for "
                    + Format.size(stations.size()) + "~ stations between "
                    + Format.timestampDate(since) + "~ and " + Format.timestampDate(until) + "~...");

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
                float average = sum / count;
                System.out.println(Format.parameter(parameter) + ":\t" + Format.measurementValue(parameter, average));
            } else {
                System.out.println(Format.parameter(parameter) + ":\t-");
            }
        }
    }
}
