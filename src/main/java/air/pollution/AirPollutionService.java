package air.pollution;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class AirPollutionService {
    private static final String apiAddress = "http://api.gios.gov.pl/pjp-api/rest/";
    private static final String apiGetAirIndex = apiAddress + "aqindex/getIndex/";
    private static final String apiGetAllSensors = apiAddress + "station/sensors/";
    private static final String apiGetAllStations = apiAddress + "station/findAll";
    private static final String apiGetSensorMeasurements = apiAddress + "data/getData/";

    private Gson gson;
    private HttpGet get;

    AirPollutionService(Gson gson) {
        this.gson = gson;

        get = new HttpGet();
    }

    List<JsonStation> getAllStations() throws IOException {
        return Arrays.asList(gson.fromJson(get.from(apiGetAllStations), JsonStation[].class));
    }

    List<JsonSensor> getAllSensors(int stationId) throws IOException {
        return Arrays.asList(gson.fromJson(get.from(apiGetAllSensors + stationId), JsonSensor[].class));
    }

    JsonSensorMeasurements getSensorMeasurements(int sensorId) throws IOException {
        return gson.fromJson(get.from(apiGetSensorMeasurements + sensorId), JsonSensorMeasurements.class);
    }

    JsonAirIndex getAirIndex(int stationId) throws IOException {
        return gson.fromJson(get.from(apiGetAirIndex + stationId), JsonAirIndex.class);
    }
}
