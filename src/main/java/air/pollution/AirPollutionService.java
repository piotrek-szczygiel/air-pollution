package air.pollution;

import com.google.gson.Gson;

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

    private Logger logger = Logger.getLogger(this);

    AirPollutionService(Gson gson) {
        this.gson = gson;

        get = new HttpGet();
    }

    List<JsonStation> getAllStations() {
        try {
            return Arrays.asList(gson.fromJson(get.from(apiGetAllStations), JsonStation[].class));
        } catch (Exception e) {
            logger.error("error while fetching all stations: " + e);
            return null;
        }
    }

    List<JsonSensor> getAllSensors(int stationId) {
        try {
            return Arrays.asList(gson.fromJson(get.from(apiGetAllSensors + stationId), JsonSensor[].class));
        } catch (Exception e) {
            logger.error("error while fetching all sensors: " + e);
            return null;
        }
    }

    JsonSensorMeasurements getSensorMeasurements(int sensorId) {
        try {
            return gson.fromJson(get.from(apiGetSensorMeasurements + sensorId), JsonSensorMeasurements.class);
        } catch (Exception e) {
            logger.error("error while fetching all sensor measurements: " + e);
            return null;
        }
    }

    JsonAirIndex getAirIndex(int stationId) {
        try {
            return gson.fromJson(get.from(apiGetAirIndex + stationId), JsonAirIndex.class);
        } catch (Exception e) {
            logger.error("error while fetching air index: " + e);
            return null;
        }
    }
}
