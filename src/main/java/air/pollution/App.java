package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Command(
        name = "air-pollution",
        mixinStandardHelpOptions = true,
        sortOptions = false,
        version = "Air Pollution Information v0.1.4 by Piotr Szczygie\u0142 2018",
        header = "Display information about air pollution in Poland.",
        description = "Displays information about air pollution using JSON API provided by the Polish government.",
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class App implements Runnable {
    @Option(names = {"--list", "-l"}, description = "List all available stations.")
    private boolean optionListStations;

    @Option(names = {"--stations", "-s"}, split = ";",
            paramLabel = "STATION", description = "Semicolon separated list of stations. "
            + "If this argument is not provided, use all available stations.")
    private List<String> optionStationNames = new ArrayList<>();

    @Option(names = {"--parameters", "-p"}, split = ";", paramLabel = "PARAMETER",
            description = "Semicolon separated list of parameters. Accepted values: ${COMPLETION-CANDIDATES}.")
    private List<Parameter> optionParameters = new ArrayList<>();

    @Option(names = {"--air-index", "-a"}, description = "Show Air Index for provided stations.")
    private boolean optionAirIndex;

    @Option(names = {"--measurements", "-m"}, description = "Show parameter measurements for provided stations.")
    private boolean optionMeasurement;

    @Option(names = {"--date", "-d"}, paramLabel = "DATE", description = "Provides specific date for commands.")
    private LocalDateTime optionDate;

    @Option(names = {"--since", "-S"}, paramLabel = "DATE_SINCE", description = "Provides specific beginning date "
            + "for commands. If this argument is not provided, assume it as the lowest possible.")
    private LocalDateTime optionSince;

    @Option(names = {"--until", "-U"}, paramLabel = "DATE_UNTIL", description = "Provides specific ending date "
            + "for commands. If this argument is not provided, assume it as the highest possible.")
    private LocalDateTime optionUntil;

    @Option(names = {"--top", "-t"}, paramLabel = "TOP", description = "Display only the first N values.")
    private int optionTop = 0;

    @Option(names = {"--verbose", "-v"}, description = "Show verbose output. Use -vv for highest verbosity mode.")
    private boolean[] optionVerbosity = new boolean[0];

    public static void main(String[] args) {
        // Create new instance of this application
        App app = new App();

        CommandLine commandLine = new CommandLine(app);

        // Register default formatting for LocalDateTime
        // Example: 16, 21:00 -> will convert to 16th of current year and month, at 21:00:00
        commandLine.registerConverter(LocalDateTime.class,
                d -> {
                    DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                            .appendPattern("dd, HH:mm")
                            .parseDefaulting(ChronoField.YEAR, LocalDate.now().getYear())
                            .parseDefaulting(ChronoField.MONTH_OF_YEAR, LocalDate.now().getMonthValue())
                            .toFormatter();

                    return LocalDateTime.parse(d, dateTimeFormatter);
                });

        // Display usage if no arguments were provided
        if (args.length == 0) {
            CommandLine.usage(app, System.out);
            return;
        }

        AnsiConsole.systemInstall();

        // Parse application arguments
        commandLine.parse(args);

        // Display usage help
        if (commandLine.isUsageHelpRequested()) {
            commandLine.usage(System.out);
            return;
        }

        // Display application version
        if (commandLine.isVersionHelpRequested()) {
            commandLine.printVersionHelp(System.out);
            return;
        }

        // Set logging error level
        if (app.optionVerbosity.length >= 2) {
            Logger.setGlobalLevel(ErrorLevel.DEBUG);
        } else if (app.optionVerbosity.length == 1) {
            Logger.setGlobalLevel(ErrorLevel.INFO);
        } else {
            Logger.setGlobalLevel(ErrorLevel.ERROR);
        }

        Logger logger = Logger.getLogger(app);

        logger.debug("starting application...");

        // Start the application
        CommandLine.run(app);

        logger.debug("exiting application...");

        AnsiConsole.systemUninstall();
    }

    @Override
    public void run() {
        Logger logger = Logger.getLogger(this);

        logger.debug("initializing components...");

        // Initialize important objects
        JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
        AirPollutionService airPollutionService = new AirPollutionService(JsonDecoder.getGson());
        ApiObjectCollector apiObjectCollector = new ApiObjectCollector(airPollutionService, jsonObjectFactory);
        Cache cache = new Cache(apiObjectCollector);

        logger.debug("initialization complete");

        // --list
        if (optionListStations) {
            new CommandListAllStations(cache).run();
            return;
        }

        List<Station> stations = new ArrayList<>();

        // Match provided station names
        if (optionStationNames.size() > 0) {

            // Collect matching stations using case insensitive simple searching
            logger.debug("matching stations...");

            List<Station> allStations = cache.getAllStations();
            if (allStations == null || allStations.size() < 1) {
                logger.fatal("unable to fetch any stations");
                return;
            }

            for (Station station : allStations) {
                for (String stationName : optionStationNames) {
                    String compared = Utils.normalizeString(station.getName());
                    String searched = Utils.normalizeString(stationName);

                    if (compared.contains(searched)) {
                        stations.add(station);
                    }
                }
            }

            if (stations.size() < 1) {
                logger.fatal("unable to match any of the provided stations");
                return;
            }

            logger.debug("matched " + Format.size(stations.size()) + "~ stations");
        } else {
            logger.info("no stations provided, assuming usage of all stations");

            stations = cache.getAllStations();

            if (stations == null || stations.size() < 1) {
                logger.fatal("unable to fetch any stations");
            }
        }

        // Cache matched stations
        cache.cacheStations(stations);

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
}
