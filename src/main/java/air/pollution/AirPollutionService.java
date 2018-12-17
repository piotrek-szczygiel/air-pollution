package air.pollution;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class AirPollutionService {
    private Gson gson;
    private HttpGet get;

    AirPollutionService(Gson gson) {
        this.gson = gson;

        get = new HttpGet();
    }

    List<JsonStation> getAllStations() throws IOException {
        return Arrays.asList(gson.fromJson(
                get.from("http://api.gios.gov.pl/pjp-api/rest/station/findAll"),
                JsonStation[].class));
    }

    List<JsonSensor> getAllSensors(int stationId) throws IOException {
        return Arrays.asList(gson.fromJson(get.from(
                "http://api.gios.gov.pl/pjp-api/rest/station/sensors/" + stationId),
                JsonSensor[].class));
    }

    JsonSensorMeasurements getSensorMeasurements(int sensorId) throws IOException {
        return gson.fromJson(get.from(
                "http://api.gios.gov.pl/pjp-api/rest/data/getData/" + sensorId),
                JsonSensorMeasurements.class);
    }

    JsonAirIndex getAirIndex(int stationId) throws IOException {
        return gson.fromJson(get.from(
                "http://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + stationId),
                JsonAirIndex.class);
    }
}
