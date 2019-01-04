package air.pollution;

import java.util.List;

import static air.pollution.Format.MEASUREMENT_DATE_FORMATTER;
import static air.pollution.Format.format;
import static org.fusesource.jansi.Ansi.ansi;

class CommandMeasurement implements Command {

    private Logger logger = Logger.getLogger(this);

    @Override
    @SuppressWarnings("Duplicates")
    public void execute(Cache cache, Options options) {
        logger.info("showing measurements for %s stations", format(options.stations.size()));

        for (Station station : options.stations) {
            for (Parameter parameter : options.parameters) {

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


                // How many measurements have been already shown
                int counter = 0;

                // How many measurements are we supposed to show
                int show = CommandUtils.howManyToShow(options.top, measurements.size());

                System.out.printf("%nShowing %s measurement%s of %s for %s%n",
                        format(show),
                        (show > 1 ? "s" : ""),
                        format(parameter),
                        format(station));

                for (SensorMeasurement measurement : measurements) {
                    // Stop showing measurements, when we have already shown it top N times
                    if (counter >= show) {
                        break;
                    }

                    System.out.printf("%s\t%s%s%s%n",
                            format(measurement.getValue(), measurement.getParameter(), true),
                            ansi().fgBrightBlack(),
                            measurement.getDate().format(MEASUREMENT_DATE_FORMATTER),
                            ansi().reset());

                    counter++;
                }
            }
        }
    }
}
