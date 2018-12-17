package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;

@Command(
        name = "air-pollution",
        mixinStandardHelpOptions = true,
        version = "Air Pollution Information v0.1.3 by Piotr Szczygie\u0142 2018",
        header = "Display information about air pollution in Poland.",
        description = "Displays information about air pollution using JSON API provided by the Polish government.",
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class App implements Runnable {
    @Option(names = {"--verbose", "-v"}, description = "verbose output")
    private boolean[] verbosity = new boolean[0];

    @Option(names = {"--list", "-l"}, description = "list available stations")
    private boolean listStations;

    @Option(names = {"--stations", "-s"}, split = ";", paramLabel = "STATION", description = "semicolon separated " +
            "list of stations")
    private String[] stationNames;

    @Option(names = {"--parameter", "-p"}, paramLabel = "PARAMETER", description = "valid values: " +
            "${COMPLETION-CANDIDATES}")
    private Parameter parameter;

    @Option(names = {"--top", "-t"}, paramLabel = "TOP", description = "display top n values")
    private int top = 0;

    @Option(names = {"--fetch-all", "-F"}, description = "fetch everything provided by API")
    private boolean fetchAll;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        CommandLine.run(new App(), args);
        AnsiConsole.systemUninstall();
    }

    @Override
    public void run() {

        // Set logging level
        if (verbosity.length >= 2) {
            Logger.setLevel(ErrorLevel.DEBUG);
        } else if (verbosity.length == 1) {
            Logger.setLevel(ErrorLevel.INFO);
        } else {
            Logger.setLevel(ErrorLevel.ERROR);
        }

        Logger logger = new Logger(this);

        logger.debug("initializing application...");

        // Initialize important objects
        JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
        AirPollutionService airPollutionService = new AirPollutionService(JsonDecoder.getGson());
        ApiObjectCollector apiObjectCollector = new ApiObjectCollector(airPollutionService, jsonObjectFactory);
        Cache cache = new Cache(apiObjectCollector);

        logger.debug("initialization complete");

        // List available stations
        if (listStations) {
            logger.info("listing stations...");
            List<Station> stations = cache.getAllStations();

            if (stations != null) {
                for (Station station : stations) {
                    System.out.println(station.getName());
                }
            }

            return;
        }

        // Fetch everything to cache
        if (fetchAll) {
            cache.cacheAll();
        }

        // Do something with provided station names
        if (stationNames != null && stationNames.length > 0) {
            List<Station> stations = new ArrayList<>();

            for (Station station : cache.getAllStations()) {
                for (String stationName : stationNames) {
                    if (station.getName().toLowerCase().contains(stationName.toLowerCase())) {
                        stations.add(station);
                    }
                }
            }

            for (Station station : stations) {
                if (parameter == null) {
                    logger.info("collecting air index for " + station.getNameColored());
                    AirIndex airIndex = cache.getAirIndex(station.getId());

                    logger.info("printing air index for " + station.getNameColored());
                    System.out.println(Format.format(airIndex));

                    continue;
                }

                List<Sensor> sensors = cache.getAllSensors(station.getId());

                if (sensors == null || sensors.size() < 1) {
                    continue;
                }

                Sensor sensor = null;

                for (Sensor searchSensor : sensors) {
                    if (searchSensor.getParameter() == parameter) {
                        sensor = searchSensor;
                    }
                }

                if (sensor == null) {
                    continue;
                }

                List<SensorMeasurement> measurements = cache.getSensorMeasurements(sensor.getId());

                if (measurements != null) {
                    logger.info("printing " + (top > 0 ? Format.size(top) : "all") + " last "
                            + Format.parameter(parameter) + " measurements for " + station.getNameColored());

                    System.out.println(Format.format(parameter, measurements, top));
                }
            }

            return;
        }

        // Display usage when no command was executed
        CommandLine.usage(this, System.out);
    }
}
