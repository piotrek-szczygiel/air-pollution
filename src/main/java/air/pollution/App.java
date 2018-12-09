package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

@Command(name = "air-pollution",
        mixinStandardHelpOptions = true,
        version = "Air Pollution Information v1.0 by Piotr Szczygie\u0142 2018",
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
        JsonObjectFactory factory = JsonObjectFactory.getInstance();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.gios.gov.pl/pjp-api/rest/")
                .addConverterFactory(GsonConverterFactory.create(JsonDecoder.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        AirPollutionService service = retrofit.create(AirPollutionService.class);
        List<JsonStation> jsonStations = service.getAllStations().blockingFirst();

        List<Station> stations = jsonStations
                .stream()
                .map(factory::fromJson)
                .collect(Collectors.toList());


        if (listStations) {
            System.out.println(ansi().fgBrightYellow().a(stations.size()).
                    fgDefault().a(" stations available:"));
            for (Station station : stations) {
                System.out.println(station.getName());
            }

            return;
        }

        if (stationName != null) {
            List<Station> found = stations
                    .stream()
                    .filter(station -> station.getName().equals(stationName))
                    .collect(Collectors.toList());

            if (found.size() < 1) {
                System.out.println(ansi().fgBrightRed()
                        .a("error: unable to find station: '").a(stationName).a("'").reset());

                System.exit(1);
            }

            Station station = found.get(0);

            if (parameter == null) {
                // Show Air Index
                System.out.println(ansi().a("found station, id: ")
                        .fgBrightGreen().a(station.getId()).reset()
                        .a("\nretrieving air index info about ")
                        .fgBrightYellow().a(stationName).reset().a("...\n"));

                AirIndex airIndex = factory.fromJson(service.getAirIndex(station.getId()).blockingFirst());
                System.out.print(airIndex);

                return;
            }

            Sensor sensor = null;

            List<JsonSensor> jsonSensors = service.getSensors(station.getId()).blockingFirst();
            for (JsonSensor jsonSensor : jsonSensors) {
                Sensor findSensor = factory.fromJson(jsonSensor);

                if (findSensor.getParameter() == parameter) {
                    sensor = findSensor;
                    break;
                }
            }

            if (sensor == null) {
                System.out.println(ansi().fgBrightRed().a("error: ").fgBrightYellow().a(stationName).fgBrightRed()
                        .a(" doesn't have ").fgCyan().a(parameter).fgBrightRed().a(" sensor.").reset());
                System.exit(1);
            }

            System.out.println(ansi().a("retrieving sensor data for ").fgCyan().a(parameter).reset()
                    .a(" in ").fgBrightYellow().a(stationName).reset().a("...\n"));

            JsonSensorData jsonSensorData = service.getSensorData(sensor.getId()).blockingFirst();
            sensor.setMeasurements(factory.fromJson(jsonSensorData));
            System.out.print(sensor);

            return;
        }

        CommandLine.usage(this, System.out);
    }
}
