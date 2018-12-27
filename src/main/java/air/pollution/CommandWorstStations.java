package air.pollution;

import java.util.*;

import static air.pollution.Format.format;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

class CommandWorstStations implements Command {
    private Logger logger = Logger.getLogger(this);

    @Override
    public void execute(Cache cache, Options options) {

        logger.info("showing most polluted stations for %s stations and %s parameters",
                format(options.stations.size()), format(options.parameters.size()));

        for (Parameter parameter : options.parameters) {
            Map<Station, SensorMeasurement> highestPollution = new HashMap<>();

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
                    if (!highestPollution.containsKey(station)) {
                        highestPollution.put(station, measurement);
                    } else {
                        if (highestPollution.get(station).getValue() < measurement.getValue()) {
                            highestPollution.put(station, measurement);
                        }
                    }
                }
            }

            if (highestPollution.size() < 1) {
                continue;
            }

            Map<Station, SensorMeasurement> sorted = highestPollution
                    .entrySet()
                    .stream()
                    .sorted(reverseOrder(comparingByValue()))
                    .collect(
                            toMap(
                                    Map.Entry::getKey,
                                    Map.Entry::getValue,
                                    (e1, e2) -> e2,
                                    LinkedHashMap::new));

            int counter = 0;
            int show = CommandUtils.howManyToShow(options.top, sorted.size());

            System.out.printf("%nTop %s most polluted station%s for %s parameter%n",
                    format(show),
                    (show > 1 ? "s" : ""),
                    format(parameter));

            List<String> printLines = new ArrayList<>();

            // Print results to temporary print storage
            for (Map.Entry<Station, SensorMeasurement> entry : sorted.entrySet()) {
                if (counter >= show) {
                    break;
                }

                Station station = entry.getKey();
                SensorMeasurement measurement = entry.getValue();

                printLines.add(String.format("%s %s: %s%n",
                        format(measurement.getValue(), measurement.getParameter(), true),
                        format(measurement.getDate()),
                        format(station)));

                counter++;
            }

            // Now reverse this storage to show results in ascending order
            Collections.reverse(printLines);

            for (String line : printLines) {
                System.out.print(line);
            }
        }
    }
}
