package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.List;

@Command(
        name = "air-pollution",
        mixinStandardHelpOptions = true,
        version = "Air Pollution Information v0.1.1 by Piotr Szczygie\u0142 2018",
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
    boolean[] verbosity = new boolean[0];

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

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        CommandLine.run(new App(), args);
        AnsiConsole.systemUninstall();
    }

    @Override
    public void run() {
        switch (verbosity.length) {
            case 1:
                Logger.setLevel(ErrorLevel.INFO);
                break;
            case 2:
                Logger.setLevel(ErrorLevel.DEBUG);
                break;
            default:
                Logger.setLevel(ErrorLevel.ERROR);
                break;
        }

        Logger logger = new Logger(this);
        logger.debug("initializing application...");

        JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
        AirPollutionService airPollutionService = new AirPollutionService(JsonDecoder.getGson());
        ApiObjectCollector apiObjectCollector = new ApiObjectCollector(airPollutionService, jsonObjectFactory);

        Cache cache = new Cache(apiObjectCollector);

        logger.debug("initialization complete");

        if (listStations) {
            logger.info("listing stations...");
            List<Station> stations = cache.getAllStations();

            if (stations != null) {
                for (Station station : stations) {
                    System.out.println(station.getName());
                }
            }
        } else if (stationNames != null && stationNames.length > 0) {
            for (String stationName : stationNames) {
                Station station = cache.getStation(stationName);

                if (station == null) {
                    continue;
                }

                if (parameter == null) {
                    logger.info("collecting air index for " + station.getNameColored());
                    AirIndex airIndex = apiObjectCollector.getAirIndex(station.getId());

                    logger.info("printing air index for " + station.getNameColored());
                    System.out.println(Format.format(airIndex));

                    continue;
                }

                Sensor sensor = cache.getSensor(station, parameter);

                if (sensor == null) {
                    continue;
                }

                if (cache.fillSensorMeasurements(sensor)) {
                    logger.info("printing " + (top > 0 ? Format.size(top) : "all") + " last "
                            + Format.parameter(parameter) + " measurements for " + station.getNameColored());
                    System.out.println(Format.format(sensor, top));
                }
            }
        } else {
            CommandLine.usage(this, System.out);
        }
    }
}
