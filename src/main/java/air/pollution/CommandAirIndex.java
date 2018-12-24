package air.pollution;

import java.util.List;

import static air.pollution.Format.format;
import static org.fusesource.jansi.Ansi.ansi;

class CommandAirIndex implements Runnable {
    private Cache cache;
    private List<Station> stations;

    private Logger logger = Logger.getLogger(this);

    CommandAirIndex(Cache cache, List<Station> stations) {
        this.cache = cache;
        this.stations = stations;
    }

    @Override
    public void run() {
        System.out.println();

        logger.debug("displaying air index for %s stations", format(stations.size()));

        for (Station station : stations) {
            logger.debug("collecting air index for %s", format(station));
            AirIndex airIndex = cache.getAirIndex(station.getId());

            logger.info("printing air index for %s", format(station));

            System.out.println(format(station));
            System.out.println(formatOutput(airIndex));

            // Add newline if current station is not the last one
            if (stations.indexOf(station) != stations.size() - 1) {
                System.out.println();
            }
        }
    }

    private String formatOutput(AirIndex airIndex) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(ansi().fgCyan())
                .append("Overall air quality: ")
                .append(format(airIndex.getAirQuality()))
                .append("\n----------------------------------");

        for (Parameter parameter : Parameter.values()) {
            Quality quality = airIndex.getValue(parameter);

            stringBuilder
                    .append(ansi().fgCyan())
                    .append("\n")
                    .append(parameter)
                    .append(":\t")
                    .append(format(quality));
        }

        return stringBuilder.toString();
    }
}
