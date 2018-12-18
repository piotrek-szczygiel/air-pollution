package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDateTime;
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
    private boolean[] optionVerbosity = new boolean[0];

    @Option(names = {"--list", "-l"}, description = "list available stations")
    private boolean optionListStations;

    @Option(names = {"--air-index", "-a"}, description = "show air index")
    private boolean optionAirIndex;

    @Option(names = {"--measurement", "-m"}, description = "show measurement")
    private boolean optionMeasurement;

    @Option(names = {"--date", "-d"}, paramLabel = "DATE", description = "date")
    private LocalDateTime optionDate;

    @Option(names = {"--since", "-S"}, paramLabel = "DATE_SINCE", description = "date since")
    private LocalDateTime optionSince;

    @Option(names = {"--until", "-U"}, paramLabel = "DATE_UNTIL", description = "date until")
    private LocalDateTime optionUntil;

    @Option(names = {"--stations", "-s"}, split = ";",
            paramLabel = "STATION", description = "semicolon separated list of stations")
    private List<String> optionStationNames = new ArrayList<>();

    @Option(names = {"--parameters", "-p"}, split = ";", paramLabel = "PARAMETER",
            description = "semicolon separated list of parameters%nvalid values: ${COMPLETION-CANDIDATES}")
    private List<Parameter> optionParameters = new ArrayList<>();

    @Option(names = {"--top", "-t"}, paramLabel = "TOP", description = "display top n values")
    private int optionTop = 0;

    @Option(names = {"--fetch-all", "-F"}, description = "fetch everything provided by API")
    private boolean optionFetchAll;

    public static void main(String[] args) {
        // Create new instance of this application
        App app = new App();

        // Display usage if no arguments were provided
        if (args.length == 0) {
            CommandLine.usage(app, System.out);
            return;
        }

        AnsiConsole.systemInstall();

        // Launch the application
        CommandLine.run(app, args);

        AnsiConsole.systemUninstall();
    }

    @Override
    public void run() {

        // Set logging level
        if (optionVerbosity.length >= 2) {
            Logger.setLevel(ErrorLevel.DEBUG);
        } else if (optionVerbosity.length == 1) {
            Logger.setLevel(ErrorLevel.INFO);
        } else {
            Logger.setLevel(ErrorLevel.ERROR);
        }

        Logger logger = new Logger(this);

        logger.info("initializing application...");

        // Initialize important objects
        JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
        AirPollutionService airPollutionService = new AirPollutionService(JsonDecoder.getGson());
        ApiObjectCollector apiObjectCollector = new ApiObjectCollector(airPollutionService, jsonObjectFactory);
        Cache cache = new Cache(apiObjectCollector);

        logger.debug("initialization complete");

        // List available stations
        if (optionListStations) {
            logger.info("listing stations...");
            List<Station> stations = cache.getAllStations();

            if (stations != null) {
                for (Station station : stations) {
                    System.out.println(station.getName());
                }
            }

            return;
        }

        // Fetch everything to cache before doing anything
        if (optionFetchAll) {
            cache.cacheAll();
        }

        // Do something with provided station names
        if (optionStationNames.size() > 0) {

            // Collect matching stations using case insensitive simple searching
            List<Station> stations = new ArrayList<>();

            for (Station station : cache.getAllStations()) {
                for (String stationName : optionStationNames) {
                    if (station.getName().toLowerCase().contains(stationName.toLowerCase())) {
                        stations.add(station);
                    }
                }
            }

            // --air-index
            if (optionAirIndex) {
                new CommandAirIndex(cache, stations).run();
            }

            // --measurement
            if (optionMeasurement) {
                new CommandMeasurement(cache, stations, optionParameters, optionTop,
                        optionDate, optionSince, optionUntil).run();
            }
        }

        logger.info("exiting application...");
    }
}
