package air.pollution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static air.pollution.CommandUtils.repeatString;
import static air.pollution.Format.format;
import static air.pollution.Format.getMeasurementColor;
import static org.fusesource.jansi.Ansi.ansi;

class CommandGraph implements Command {
    private static final DateTimeFormatter GRAPH_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter SPECIFIC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM");

    private static final int DATE_LENGTH = 5;
    private static final int ASCII_BAR_LENGTH = 70;

    private Logger logger = Logger.getLogger(this);

    @Override
    public void execute(Cache cache, Options options) {

        logger.debug("showing hourly pollution graph for %s stations", format(options.stations.size()));

        List<Integer> hours = new LinkedList<>();

        // Fill needed hours
        if (options.hourUntil >= options.hourSince) {
            for (int i = options.hourSince; i <= options.hourUntil; i++) {
                hours.add(i);
            }
        } else {
            for (int i = options.hourSince; i <= 23; i++) {
                hours.add(i);
            }

            for (int i = 0; i <= options.hourUntil; i++) {
                hours.add(i);
            }
        }

        for (Parameter parameter : options.parameters) {
            Map<Integer, List<SimpleEntry<Station, SensorMeasurement>>> graphData = new TreeMap<>();
            hours.forEach(hour -> graphData.put(hour, new LinkedList<>()));

            float maximum = 0;

            for (Station station : options.stations) {

                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(
                                cache,
                                station,
                                parameter,
                                options.since,
                                options.until);

                if (measurements == null) {
                    continue;
                }

                for (SensorMeasurement measurement : measurements) {
                    int hour = measurement.getDate().getHour();
                    if (hours.contains(hour)) {
                        maximum = Math.max(maximum, measurement.getValue());

                        graphData.get(hour).add(new SimpleEntry<>(station, measurement));
                    }
                }
            }

            if (maximum == 0) {
                continue;
            }

            System.out.printf("%nGraphing %s hourly pollution for %s station%s%n%n",
                    format(parameter),
                    format(options.stations.size()),
                    (options.stations.size() > 1 ? "s" : ""));

            System.out.printf("%s%s%s%s [\u00b5g/m3]%n",
                    repeatString(" ", DATE_LENGTH + ASCII_BAR_LENGTH / 2 + 2),
                    (int) (maximum / 2),
                    repeatString(" ", ASCII_BAR_LENGTH / 2 - 1),
                    (int) maximum);


            System.out.printf("%s \u250c", repeatString(" ", DATE_LENGTH));
            System.out.printf("%s\u253c%s\u2524%n",
                    repeatString("\u2500", ASCII_BAR_LENGTH / 2),
                    repeatString("\u2500", ASCII_BAR_LENGTH / 2));

            for (int hour : hours) {
                List<SimpleEntry<Station, SensorMeasurement>> measurements = graphData.get(hour);

                int i = 0;
                for (SimpleEntry<Station, SensorMeasurement> measurementEntry : measurements) {

                    float value = measurementEntry.getValue().getValue();
                    int barLength = (int) ((value / maximum) * ASCII_BAR_LENGTH);

                    LocalDateTime date = measurementEntry.getValue().getDate();

                    if (i == 0) {
                        System.out.printf("%s \u253c ", GRAPH_DATE_FORMATTER.format(date));
                    } else {
                        System.out.printf("%s \u2502 ", repeatString(" ", DATE_LENGTH));
                    }

                    System.out.printf("%s%s",
                            Format.getMeasurementColor(value, parameter),
                            repeatString("\u2588", barLength));

                    String dateString = SPECIFIC_DATE_FORMATTER.format(date);

                    if (date.getDayOfYear() == options.today.getDayOfYear()) {
                        dateString = "Today";
                    } else if (date.getDayOfYear() == options.today.getDayOfYear() - 1) {
                        dateString = "Yesterday";
                    }

                    System.out.printf(" %s%.2f %s%s, %s%s%n",
                            getMeasurementColor(value, parameter),
                            value,
                            ansi().fgBrightBlack(),
                            dateString,
                            measurementEntry.getKey().getName(),
                            ansi().reset());

                    i++;
                }
            }

            System.out.printf("%s \u2534%n", repeatString(" ", DATE_LENGTH));
        }
    }
}
