package air.pollution;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.stream.Collectors;

@Command(name = "air-pollution",
        mixinStandardHelpOptions = true,
        version = "Air Pollution Information v1.0 by Piotr Szczygieł 2018",
        header = "Display information about air pollution in Poland.",
        description = "Displays information about air pollution usign JSON API provided by the Polish government.\n\n"
                + "Available commands\n"
                + "\tair-index: show curret air index for specified station",
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class App implements Runnable {
    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        CommandLine.run(new App(), args);
        AnsiConsole.systemUninstall();
    }

    @Override
    public void run() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.gios.gov.pl/pjp-api/rest/")
                .addConverterFactory(GsonConverterFactory.create(JsonDecoder.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        AirPollutionService service = retrofit.create(AirPollutionService.class);
        List<JsonStation> jsonStations = service.getAllStations().blockingFirst();

        List<Station> stations = jsonStations
                .stream()
                .map(Station::new)
                .collect(Collectors.toList());

        System.out.println(stations.size() + " stations available");

/*        List<Observable<List<JsonSensor>>> sensorRequests = new ArrayList<>();
        for (var station : stations) {
            sensorRequests.add(service.getSensors(station.id));
        }

        List<List<JsonSensor>> allSensors = Observable
                .zip(sensorRequests, objects -> {
                    List<List<JsonSensor>> sensorsList = new ArrayList<>();

                    for (Object o : objects) {
                        List<JsonSensor> sensors = (List<JsonSensor>) o;
                        sensorsList.add(sensors);
                    }

                    return sensorsList;
                })
                .blockingFirst();

        List<Observable<JsonSensorData>> dataRequests = new ArrayList<>();
        int counter = 0;
        for (var sensors : allSensors) {
            for (var sensor : sensors) {
                counter++;
                dataRequests.add(service.getSensorData(sensor.id));
            }
        }

        System.out.println("collected " + counter + " sensors");

        List<JsonSensorData> allSensorData = Observable
                .zip(dataRequests, objects -> {
                    List<JsonSensorData> dataList = new ArrayList<>();

                    for (Object o : objects) {
                        JsonSensorData data = (JsonSensorData) o;
                        dataList.add(data);
                    }

                    return dataList;
                }).blockingFirst();

        System.out.println("collected " + allSensorData.size() + " sensor data");*/
    }
}
