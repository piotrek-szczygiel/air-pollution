package air.pollution;

import com.google.gson.JsonSyntaxException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.util.Date;

@Command(name = "air-pollution",
        mixinStandardHelpOptions = true,
        version = "Air Pollution Information v1.0 by Piotr Szczygieł 2018",
        header = "Display information about air pollution in Poland.",
        description = "Displays information about air pollution usign JSON API provided by the government.",
        headerHeading = "@|bold,underline Usage:|@%n%n",
        synopsisHeading = "%n",
        descriptionHeading = "%n@|bold,underline Description:|@%n%n",
        parameterListHeading = "%n@|bold,underline Parameters:|@%n",
        optionListHeading = "%n@|bold,underline Options:|@%n"
)
public class App implements Runnable {
    @Option(names = {"--station"}, paramLabel = "STATION", description = "station")
    private String paramStationName;

    @Option(names = {"--date"}, paramLabel = "DATE", description = "date")
    private Date paramDate;

    @Option(names = {"--parameter"}, paramLabel = "PARAMETER", description = "parameter")
    private String paramAirParameter;

    public static void main(String[] args) {
        CommandLine.run(new App(), args);
    }

    @Override
    public void run() {
        DataCollector dataCollector = new DataCollector();

        Station[] stations;
        try {
            stations = dataCollector.collectAllStations();
        } catch (IOException | JsonSyntaxException ex) {
            System.out.println("error while loading stations: " + ex);
            return;
        }

        Station station = null;
        if (paramStationName != null) {
            for (Station stationIterator : stations) {
                if (stationIterator.stationName.equals(paramStationName)) {
                    station = stationIterator;
                }
            }
        }

        if (station != null) {
            System.out.println(station.id + ": " + station.stationName + ", " + station.city.commune.communeName);

            AirIndex airIndex;
            try {
                airIndex = dataCollector.collectAirIndexData(station.id);
            } catch (IOException | JsonSyntaxException ex) {
                System.out.println("error while loading air index: " + ex);
                return;
            }

            System.out.println("stale: " + airIndex.stIndexLevel.indexLevelName);

            Sensor[] sensors;
            try {
                sensors = dataCollector.collectAllSensors(station.id);
            } catch (IOException | JsonSyntaxException ex) {
                System.out.println("error while loading sensors: " + ex);
                return;
            }

            for (Sensor sensorIterator : sensors) {
                System.out.println(sensorIterator.id + ": " + sensorIterator.param.paramCode);
                SensorData sensorData;
                try {
                    sensorData = dataCollector.collectSensorData(sensorIterator.id);
                } catch (IOException | JsonSyntaxException ex) {
                    System.out.println("error while loading sensor data: " + ex);
                    return;
                }

                System.out.println("\t" + sensorData.key + ": ");
                for (SensorData.Value value : sensorData.values) {
                    System.out.println("\t\t" + value.date + ": " + value.value);
                }
            }
        }
    }
}
