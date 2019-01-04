package air.pollution;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import static air.pollution.Format.format;

/**
 * Collects JSON objects from API.
 */
class AirPollutionService {
    private static final String apiAddress = "http://api.gios.gov.pl/pjp-api/rest/";

    private static final String apiFetchAirIndex = apiAddress + "aqindex/getIndex/";
    private static final String apiFetchAllSensors = apiAddress + "station/sensors/";
    private static final String apiFetchAllStations = apiAddress + "station/findAll";
    private static final String apiFetchSensorMeasurements = apiAddress + "data/getData/";

    private Gson gson;
    private HttpGet get;

    private Logger logger = Logger.getLogger(this);

    AirPollutionService(Gson gson) {
        this.gson = gson;

        get = new HttpGet();
    }

    /**
     * Returns list of JSON stations or null if none exists.
     *
     * @return list of stations
     * @see JsonStation
     */
    List<JsonStation> fetchAllStations() {
        List<JsonStation> jsonStations;

        try {
            jsonStations = Arrays.asList(gson.fromJson(get.from(apiFetchAllStations), JsonStation[].class));
        } catch (Exception e) {
            logger.warn("unable to fetch all stations: %s", e.toString());

            return null;
        }

        return jsonStations;
    }

    /**
     * Returns list of JSON sensors for specified station or null if none exists.
     *
     * @param stationId id of station
     * @return list of sensors
     * @see JsonSensor
     */
    List<JsonSensor> fetchAllSensors(int stationId) {
        List<JsonSensor> jsonSensors;

        try {
            jsonSensors = Arrays.asList(gson.fromJson(get.from(apiFetchAllSensors + stationId), JsonSensor[].class));
        } catch (Exception e) {
            logger.warn("unable to fetch all sensors for station with id %s: %s",
                    format(stationId),
                    e.toString());

            return null;
        }

        return jsonSensors;
    }

    /**
     * Returns sensor measurements for specified sensor or null if none exists.
     *
     * @param sensorId id of sensor
     * @return sensor measurements
     * @see JsonSensorMeasurements
     */
    JsonSensorMeasurements fetchSensorMeasurements(int sensorId) {
        JsonSensorMeasurements jsonSensorMeasurements;

        try {
            jsonSensorMeasurements = gson.fromJson(get.from(apiFetchSensorMeasurements + sensorId),
                    JsonSensorMeasurements.class);
        } catch (Exception e) {
            logger.warn("unable to fetch all sensor measurements for sensor with id %s: %s",
                    format(sensorId),
                    e.toString());

            return null;
        }

        return jsonSensorMeasurements;
    }

    /**
     * Returns air index for specified station or null if it doesn't exist.
     *
     * @param stationId id of station
     * @return air index
     * @see JsonAirIndex
     */
    JsonAirIndex fetchAirIndex(int stationId) {
        JsonAirIndex jsonAirIndex;

        try {
            jsonAirIndex = gson.fromJson(get.from(apiFetchAirIndex + stationId), JsonAirIndex.class);
        } catch (Exception e) {
            logger.warn("unable to fetch air index for station with id %s: %s",
                    format(stationId),
                    e.toString());

            return null;
        }

        return jsonAirIndex;
    }
}
