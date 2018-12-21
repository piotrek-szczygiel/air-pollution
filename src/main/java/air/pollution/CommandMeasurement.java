package air.pollution;

import java.time.LocalDateTime;
import java.util.List;

class CommandMeasurement implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;
    private int top;

    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandMeasurement(Cache cache, List<Station> stations, List<Parameter> parameters,
                       LocalDateTime since, LocalDateTime until, int top) {
        this.cache = cache;
        this.stations = stations;
        this.parameters = parameters;
        this.top = top;

        this.since = since;
        this.until = until;
    }

    @Override
    public void run() {
        System.out.println();

        logger.info("showing measurements for " + Format.size(stations.size()) + "~ stations");

        for (Station station : stations) {
            for (Parameter parameter : parameters) {

                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(cache, station, parameter, since, until);

                if (measurements == null) {
                    continue;
                }


                // How many measurements have been already shown
                int counter = 0;

                // How many measurements are we supposed to show
                int show;

                if (top == 0) {
                    // If there was no top option specified, show all measurements
                    show = measurements.size();
                } else {
                    // Otherwise, show at most top N measurements, without showing more than it is possible
                    show = Math.min(top, measurements.size());
                }

                System.out.println("Showing " + Format.size(show) + " measurement" + (show > 1 ? "s" : "")
                        + " of " + Format.parameter(parameter)
                        + " for " + Format.stationName(station.getName()));

                for (SensorMeasurement measurement : measurements) {
                    // Stop showing measurements, when we have already shown it top N times
                    if (counter >= show) {
                        break;
                    }

                    System.out.println(Format.measurement(measurement));
                    counter++;
                }

                // Add new line if these measurements were not the last
                if (stations.indexOf(station) != stations.size() - 1
                        || parameters.indexOf(parameter) != parameters.size() - 1) {

                    System.out.println();
                }
            }
        }
    }
}
