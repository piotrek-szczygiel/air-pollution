package air.pollution;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

class ShowAirIndex {
    static void show(String stationName) {
        if (stationName == null || stationName.equals("")) {
            System.out.println("error: you have to specify station name (--station)");
            return;
        }

        DataCollector dataCollector = new DataCollector();

        Station[] stations;
        try {
            stations = dataCollector.collectAllStations();
        } catch (IOException | JsonSyntaxException ex) {
            System.out.println("error while loading stations: " + ex);
            return;
        }

        Station station = null;
        for (Station stationIterator : stations) {
            if (stationIterator.stationName.equals(stationName)) {
                station = stationIterator;
            }
        }

        if (station != null) {
            System.out.printf("Displaying Air Index for %s (id: %s)%n", station.stationName, station.id);
            AirIndex airIndex;
            try {
                airIndex = dataCollector.collectAirIndexData(station.id);
            } catch (IOException | JsonSyntaxException ex) {
                System.out.println("error while loading air index: " + ex);
                return;
            }

            if (airIndex.stIndexLevel != null) {
                System.out.println("\tStale:\t" + airIndex.stIndexLevel.indexLevelName);
            }
            if (airIndex.so2IndexLevel != null) {
                System.out.println("\tSO2:\t" + airIndex.so2IndexLevel.indexLevelName);
            }
            if (airIndex.no2IndexLevel != null) {
                System.out.println("\tNO2:\t" + airIndex.no2IndexLevel.indexLevelName);
            }
            if (airIndex.coIndexLevel != null) {
                System.out.println("\tCO:\t" + airIndex.coIndexLevel.indexLevelName);
            }
            if (airIndex.pm10IndexLevel != null) {
                System.out.println("\tPM10:\t" + airIndex.pm10IndexLevel.indexLevelName);
            }
            if (airIndex.pm25IndexLevel != null) {
                System.out.println("\tPM2.5:\t" + airIndex.pm25IndexLevel.indexLevelName);
            }
            if (airIndex.o3IndexLevel != null) {
                System.out.println("\tO3:\t" + airIndex.o3IndexLevel.indexLevelName);
            }
            if (airIndex.c6h6IndexLevel != null) {
                System.out.println("\tC6H6:\t" + airIndex.c6h6IndexLevel.indexLevelName);
            }
        } else {
            System.out.println("error: unable to find specified station: " + stationName);
        }
    }
}
