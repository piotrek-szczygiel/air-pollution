package air.pollution;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/*
    1. W jaki sposób wypisywać ten indeks
    2. Dla podanego dnia wartość parametru ma być uśrednionia, czy może podane wszystkie wyniki
    3. Rozumiem, że trzeba podać miasto?
    4. j.w
    5. j.w
    7. Wymaga to pobrania całej bazy, co zajmuje bardzo dużo czasu
    8. Dla konkretnego miasta? Zamiast numeru tygodnia nie może być po prostu YYYY-MM-DD?
       Z danego dnia parametr jest średnią wartością?
*/

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
    @Option(names = {"--command"}, required = true, paramLabel = "COMMAND", description = "command for this program")
    private String command;

    @Option(names = {"--station"}, paramLabel = "STATION", description = "station name")
    private String stationName;

    public static void main(String[] args) {
        CommandLine.run(new App(), args);
    }

    @Override
    public void run() {
        switch (command) {
            case "air-index":
                ShowAirIndex.show(stationName);
                break;
            default:
                System.out.println("error: unknown command received");
                System.exit(1);
                break;
        }
//        DataCollector dataCollector = new DataCollector();
//
//        Station[] stations;
//        try {
//            stations = dataCollector.collectAllStations();
//        } catch (IOException | JsonSyntaxException ex) {
//            System.out.println("error while loading stations: " + ex);
//            return;
//        }
//
//        Station station = null;
//        if (paramStationName != null) {
//            for (Station stationIterator : stations) {
//                if (stationIterator.stationName.equals(paramStationName)) {
//                    station = stationIterator;
//                }
//            }
//        }
//
//        if (station != null) {
//            System.out.println(station.id + ": " + station.stationName + ", " + station.city.commune.communeName);
//            AirIndex airIndex;
//            try {
//                airIndex = dataCollector.collectAirIndexData(station.id);
//            } catch (IOException | JsonSyntaxException ex) {
//                System.out.println("error while loading air index: " + ex);
//                return;
//            }
//
//            if (airIndex.stIndexLevel != null) {
//                System.out.println("Stale:\t" + airIndex.stIndexLevel.indexLevelName);
//            }
//            if (airIndex.so2IndexLevel != null) {
//                System.out.println("SO2:\t" + airIndex.so2IndexLevel.indexLevelName);
//            }
//            if (airIndex.no2IndexLevel != null) {
//                System.out.println("NO2:\t" + airIndex.no2IndexLevel.indexLevelName);
//            }
//            if (airIndex.coIndexLevel != null) {
//                System.out.println("CO:\t" + airIndex.coIndexLevel.indexLevelName);
//            }
//            if (airIndex.pm10IndexLevel != null) {
//                System.out.println("PM10:\t" + airIndex.pm10IndexLevel.indexLevelName);
//            }
//            if (airIndex.pm25IndexLevel != null) {
//                System.out.println("PM2.5:\t" + airIndex.pm25IndexLevel.indexLevelName);
//            }
//            if (airIndex.o3IndexLevel != null) {
//                System.out.println("O3:\t" + airIndex.o3IndexLevel.indexLevelName);
//            }
//            if (airIndex.c6h6IndexLevel != null) {
//                System.out.println("C6H6:\t" + airIndex.c6h6IndexLevel.indexLevelName);
//            }
//
//            Sensor[] sensors;
//            try {
//                sensors = dataCollector.collectAllSensors(station.id);
//            } catch (IOException | JsonSyntaxException ex) {
//                System.out.println("error while loading sensors: " + ex);
//                return;
//            }
//
//            for (Sensor sensor : sensors) {
//                System.out.println(sensor.id + ": " + sensor.param.paramCode);
//
//                SensorData sensorData;
//                try {
//                    sensorData = dataCollector.collectSensorData(sensor.id);
//                } catch (IOException | JsonSyntaxException ex) {
//                    System.out.println("error while loading sensor data: " + ex);
//                    return;
//                }
//
//                for (SensorData.Value value : sensorData.values) {
//                    System.out.println("\t" + value.date + ": " + value.value);
//                }
//            }
//        }
    }
}
