package air.pollution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static air.pollution.Format.format;
import static org.fusesource.jansi.Ansi.ansi;

class CommandMeasurement implements Runnable {
    private static final DateTimeFormatter MEASUREMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm,  dd MMMM");
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;
    private int top;
    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;
    private Logger logger = Logger.getLogger(this);

    CommandMeasurement(Cache cache, List<Station> stations, List<Parameter> parameters,
                       LocalDateTime date, LocalDateTime since, LocalDateTime until, int top) {
        this.cache = cache;
        this.stations = stations;
        this.parameters = parameters;

        this.date = date;
        this.since = since;
        this.until = until;

        this.top = top;
    }

    @Override
    public void run() {
        logger.info("showing measurements for %s stations", format(stations.size()));

        if (date != null) {
            since = date;
            until = date;
        }

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
                int show = CommandUtils.howManyToShow(top, measurements.size());

                System.out.printf("%nShowing %s measurement%s of %s for %s%n", format(show), (show > 1 ? "s" : ""),
                        format(parameter), format(station));

                for (SensorMeasurement measurement : measurements) {
                    // Stop showing measurements, when we have already shown it top N times
                    if (counter >= show) {
                        break;
                    }

                    System.out.printf("%s\t%s%n", format(measurement.getParameter(), measurement.getValue(), true),
                            ansi().fgBrightBlack().a(measurement.getDate().format(MEASUREMENT_DATE_FORMATTER)).reset());

                    counter++;
                }
            }
        }
    }
}
