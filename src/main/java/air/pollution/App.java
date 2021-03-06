package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static air.pollution.Format.format;

/**
 * Main program class.
 */
@CommandLine.Command(
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

    // Options provided through command line arguments.

    @Option(names = {"--stations", "-s"},
            split = ";",
            paramLabel = "STATION",
            description = "Semicolon separated list of stations. If this argument is not provided, "
                    + "use all available stations.")
    private List<String> optionStationNames = new ArrayList<>();


    @Option(names = {"--parameters", "-p"},
            split = ";",
            paramLabel = "PARAMETER",
            description = "Semicolon separated list of parameters. If this argument is not provided, "
                    + "use all parameters. Accepted values: ${COMPLETION-CANDIDATES}.")
    private List<Parameter> optionParameters = new ArrayList<>();


    @Option(names = {"--list", "-l"},
            description = "List all available stations.")
    private boolean optionListStations;


    @Option(names = {"--air-index", "-a"},
            description = "Show Air Index for provided stations.")
    private boolean optionAirIndex;


    @Option(names = {"--measurements", "-m"},
            description = "Show parameter measurements for provided stations.")
    private boolean optionMeasurement;


    @Option(names = {"--average", "-A"},
            description = "Show average pollution for provided stations and parameters.")
    private boolean optionAverage;


    @Option(names = {"--fluctuation", "-f"},
            description = "Show highest fluctuating parameter for provided stations.")
    private boolean optionFluctuation;


    @Option(names = {"--worst-stations", "-w"},
            description = "Show stations with highest pollution.")
    private boolean optionWorstStations;


    @Option(names = {"--find-min-max-parameter", "-M"},
            description = "Find parameter with lowest and highest value.")
    private boolean optionFindMinMaxParameter;


    @Option(names = {"--find-min-max-value", "-x"},
            description = "Find lowest and highest value for provided "
                    + "parameters and stations.")
    private boolean optionFindMinMaxValue;


    @Option(names = {"--find-above-norm", "-n"},
            description = "Find measurements above norm for provided "
                    + "parameters and stations.")
    private boolean optionFindAboveNorm;


    @Option(names = {"--date", "-D"},
            paramLabel = "DATE",
            description = "Provides specific date for commands.")
    private LocalDateTime optionDate;


    @Option(names = {"--since", "-S"},
            paramLabel = "DATE_SINCE",
            description = "Provides specific beginning date for commands in format 'dd, h:MM'. "
                    + "If this argument is not provided, assume it as the lowest possible.")
    private LocalDateTime optionSince;


    @Option(names = {"--until", "-U"},
            paramLabel = "DATE_UNTIL",
            description = "Provides specific ending date for commands in format 'dd, h:MM'. "
                    + "If this argument is not provided, assume it as the highest possible.")
    private LocalDateTime optionUntil;


    @Option(names = {"--top", "-t"},
            paramLabel = "TOP",
            description = "Display only the first N values. If this argument is not provided, display first 5 values.")
    private int optionTop = 5;


    @Option(names = {"--graph", "-g"},
            description = "Show graph of pollution for specified hours.")
    private boolean optionGraph;


    @Option(names = {"--hour-since", "-i"},
            paramLabel = "HOUR_SINCE",
            description = "Provides beginning hour for graph command.")
    private int optionHourSince = 0;


    @Option(names = {"--hour-until", "-I"},
            paramLabel = "HOUR_UNTIL",
            description = "Provides ending hour for graph command.")
    private int optionHourUntil = 23;


    @Option(names = {"--threads", "-T"},
            description = "Number of threads used while fetching data. "
            + "If this argument is not provided, use one thread for every station.")
    private int optionNumberOfThreads = 0;


    @Option(names = {"--refresh", "-r"},
            description = "Force cache update.")
    private boolean optionRefreshCache;


    @Option(names = {"--refresh-disable", "-R"},
            description = "Don't refresh cache even when it's outdated.")
    private boolean optionRefreshCacheDisable;


    @Option(names = {"--cache-file", "-c"},
            description = "Path to cache file.")
    private File optionCacheFile = new File("cache.gz");


    @Option(names = {"--verbose", "-v"},
            description = "Show verbose output. Use -vv for highest verbosity mode.")
    private boolean[] optionVerbosity = new boolean[0];


    /**
     * This application entry point.
     * <p>
     * Initializes application and then runs it.
     *
     * @param args arguments passed from command line
     */
    public static void main(String[] args) {
        AnsiConsole.systemInstall();

        // Create new instance of this application
        App app = new App();

        CommandLine commandLine = new CommandLine(app);

        // Register default formatting for LocalDateTime
        // Example: 16, 21:00 -> will convert to 16th of current year and month, at 21:00:00
        commandLine.registerConverter(LocalDateTime.class,
                d -> {
                    DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder()
                            .appendPattern("d, H:mm")
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


    /**
     * Main program method.
     * <p>
     * Creates most important objects and invokes execution strategy.
     */
    @Override
    public void run() {
        Logger logger = Logger.getLogger(this);

        logger.debug("initializing components...");

        // Initialize important objects
        JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
        AirPollutionService airPollutionService = new AirPollutionService(JsonDecoder.getGson());
        ApiObjectCollector apiObjectCollector = new ApiObjectCollector(airPollutionService, jsonObjectFactory);

        logger.debug("initialization complete");


        Cache cache = null;
        CacheFile cacheFile = new CacheFile(optionCacheFile);

        // --refresh
        if (!optionRefreshCache) {
            cache = cacheFile.load(optionRefreshCacheDisable);

            if (cache == null) {
                optionRefreshCache = true;
            } else {
                cache.setApiObjectCollector(apiObjectCollector);
            }
        }

        if (optionRefreshCache) {
            logger.debug("refreshing cache...");

            cache = new Cache();
            cache.setApiObjectCollector(apiObjectCollector);

            cache.cacheStations(cache.getAllStations(), optionNumberOfThreads);

            logger.debug("saving up-to-date cache to file...");

            cacheFile.save(cache);
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

            logger.debug("matched %s stations", format(stations.size()));
        } else {
            logger.debug("no stations provided, assuming usage of all stations");

            stations = cache.getAllStations();

            if (stations == null || stations.size() < 1) {
                logger.fatal("unable to fetch any stations");
            }
        }


        List<Parameter> parameters;

        // If there are none parameters provided, assume usage of all
        if (optionParameters.size() < 1) {
            logger.debug("no parameters provided, assuming usage of all parameters");
            parameters = Arrays.asList(Parameter.values());
        } else {
            parameters = optionParameters;
        }


        LocalDateTime since, until;

        if (optionDate != null) {
            since = optionDate;
            until = optionDate;
        } else {
            logger.debug("specific date not specified, considering date ranges...");

            // If there are none date range provided, assume maximum possible range
            if (optionSince == null) {
                logger.debug("no since option provided, assuming minimum date");
                since = CommandUtils.getLowestDate(cache);
            } else {
                since = optionSince;
            }

            if (optionUntil == null) {
                logger.debug("no until option provided, assuming maximum date");
                until = CommandUtils.getHighestDate(cache);
            } else {
                until = optionUntil;
            }
        }

        // Prepare command line options
        Options options =
                new Options(
                        stations,
                        parameters,
                        since,
                        until,
                        LocalDateTime.now(),
                        optionTop,
                        optionHourSince,
                        optionHourUntil,
                        optionVerbosity.length > 0
                );

        // Prepare execution strategy
        OptionStrategy strategy =
                new OptionStrategy(
                        optionListStations,
                        optionAirIndex,
                        optionMeasurement,
                        optionAverage,
                        optionFluctuation,
                        optionFindMinMaxParameter,
                        optionFindMinMaxValue,
                        optionFindAboveNorm,
                        optionWorstStations,
                        optionGraph
                );

        strategy.invoke(cache, options);
    }
}
