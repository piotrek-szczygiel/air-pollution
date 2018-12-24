package air.pollution;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static air.pollution.Format.format;
import static java.util.Collections.reverseOrder;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

class CommandWorstStations implements Runnable {
    private Cache cache;
    private List<Station> stations;
    private List<Parameter> parameters;
    private int top;

    private LocalDateTime date;
    private LocalDateTime since;
    private LocalDateTime until;

    private Logger logger = Logger.getLogger(this);

    CommandWorstStations(Cache cache, List<Station> stations, List<Parameter> parameters,
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
        logger.info("showing most polluted stations for %s stations and %s parameters",
                format(stations.size()), format(parameters.size()));

        if (date != null) {
            since = date;
            until = date;
        }

        for (Parameter parameter : parameters) {
            Map<Station, SensorMeasurement> highestPollution = new HashMap<>();

            for (Station station : stations) {

                List<SensorMeasurement> measurements =
                        CommandUtils.getMeasurementsInRange(cache, station, parameter, since, until);

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

            Map<Station, SensorMeasurement> sorted = highestPollution
                    .entrySet()
                    .stream()
                    .sorted(reverseOrder(comparingByValue()))
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

            int counter = 0;
            int show = CommandUtils.howManyToShow(top, sorted.size());

            System.out.printf("%nTop %s most polluted station%s for %s parameter%n",
                    format(show), (show > 1 ? "s" : ""), format(parameter));

            for (Map.Entry<Station, SensorMeasurement> entry : sorted.entrySet()) {
                if (counter >= show) {
                    break;
                }

                Station station = entry.getKey();
                SensorMeasurement measurement = entry.getValue();

                System.out.printf("%s %s: %s%n", format(measurement.getParameter(), measurement.getValue(), true),
                        format(measurement.getDate()), format(station));

                counter++;
            }
        }
    }
}
