package air.pollution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import me.tongfei.progressbar.ProgressBar;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        SimpleDateFormat sdfNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Gson gson = new GsonBuilder().registerTypeAdapter(
                Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                    String dateStr = json.getAsString();
                    if (dateStr.contains("-")) {
                        try {
                            return sdfNormal.parse(dateStr);
                        } catch (ParseException ex) {
                            throw new JsonParseException(ex);
                        }
                    }

                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.gios.gov.pl/pjp-api/rest/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        try {
            AirPollutionService service = retrofit.create(AirPollutionService.class);

            List<JSONStation> stations = service.getAllStations().execute().body();

            if (stations == null) {
                throw new IllegalArgumentException("no stations received");
            }

            System.out.println("found " + stations.size() + " stations");

            for (JSONStation station : ProgressBar.wrap(stations, "loading data...")) {
                List<JSONSensor> sensors = service.getSensors(station.id).execute().body();

                if (sensors == null) {
                    throw new IllegalArgumentException("no sensors received");
                }

                for (JSONSensor sensor : sensors) {
                    JSONSensorData sensorData = service.getSensorData(sensor.id).execute().body();

                    if (sensorData == null) {
                        throw new IllegalArgumentException("no sensor data received");
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("error: " + ex);
        } finally {
            System.out.println("finished");
        }
    }
}
