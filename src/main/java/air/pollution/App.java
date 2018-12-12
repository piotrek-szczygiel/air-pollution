package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;

@Command(name = "air-pollution",
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
    @Option(names = {"--list", "-l"}, description = "List available stations.")
    private boolean listStations;

    @Option(names = {"--station", "-s"}, paramLabel = "STATION", description = "station name")
    private String stationName;

    @Option(names = {"--parameter", "-p"}, paramLabel = "PARAMETER", description = "valid values: " +
            "${COMPLETION-CANDIDATES}")
    private Parameter parameter;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        CommandLine.run(new App(), args);
        AnsiConsole.systemUninstall();
    }

    @Override
    public void run() {
        Logger logger = new Logger(this);


        AirPollutionService airPollutionService = new Retrofit.Builder()
                .baseUrl("http://api.gios.gov.pl/pjp-api/rest/")
                .addConverterFactory(GsonConverterFactory.create(JsonDecoder.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(AirPollutionService.class);

        JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();
        ApiObjectCollector collector = new ApiObjectCollector(airPollutionService, jsonObjectFactory);

        if (listStations) {
            List<Station> stations = collector.getAllStations();

            if (stations == null) {
                return;
            }

            for (Station station : stations) {
                System.out.println(station.getName());
            }

            return;
        }

        if (stationName != null) {
            Station station = collector.getStation(stationName);

            if (station == null) {
                return;
            }

            if (parameter == null) {
                AirIndex airIndex = collector.getAirIndex(station.getId());
                System.out.println(PrettyFormat.format(airIndex));

                return;
            }

            Sensor sensor = collector.getSensor(station.getId(), parameter);

            if (sensor == null) {
                return;
            }

            sensor.setMeasurements(collector.getSensorData(sensor.getId()));

            System.out.println(PrettyFormat.format(sensor));
            return;
        }

        CommandLine.usage(this, System.out);
    }
}
