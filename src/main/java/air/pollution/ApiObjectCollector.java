package air.pollution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class ApiObjectCollector {
    private AirPollutionService airPollutionService;
    private JsonObjectFactory jsonObjectFactory;
    private Logger logger;

    ApiObjectCollector(AirPollutionService airPollutionService, JsonObjectFactory jsonObjectFactory) {
        this.airPollutionService = airPollutionService;
        this.jsonObjectFactory = jsonObjectFactory;

        logger = new Logger(this);
    }

    List<Station> getAllStations() {
        logger.debug("fetching all stations...");

        List<JsonStation> jsonStations = new ArrayList<>();

        try {
            jsonStations = airPollutionService.getAllStations();
        } catch (IOException e) {
            logger.error(e);
        }

        List<Station> stations = jsonStations
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());

        if (stations.size() < 1) {
            logger.error("no stations fetched");
            return null;
        }

        logger.debug(Format.size(stations.size()) + " stations fetched");

        return stations;
    }

    List<Sensor> getAllSensors(int stationId) {
        logger.debug("fetching all sensors for station with id " + Format.stationId(stationId) + "...");

        List<JsonSensor> jsonSensors = new ArrayList<>();
        try {
            jsonSensors = airPollutionService.getAllSensors(stationId);
        } catch (IOException e) {
            logger.error(e);
        }

        if (jsonSensors.size() < 1) {
            logger.error("unable to fetch sensors for station with id " + Format.stationId(stationId));
            return null;
        }

        return jsonSensors
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());
    }

    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        logger.debug("fetching sensor data for sensor with id " + Format.sensorId(sensorId) + "...");

        JsonSensorMeasurements jsonSensorMeasurements = null;
        try {
            jsonSensorMeasurements = airPollutionService.getSensorMeasurements(sensorId);
        } catch (IOException e) {
            logger.error(e);
        }

        if (jsonSensorMeasurements == null) {
            logger.error("unable to fetch sensor data");
            return null;
        }

        List<SensorMeasurement> sensorMeasurements = jsonObjectFactory.fromJson(jsonSensorMeasurements);

        logger.debug("fetched " + Format.size(sensorMeasurements.size()) + " measurements");

        return sensorMeasurements;
    }

    AirIndex getAirIndex(int stationId) {
        JsonAirIndex jsonAirIndex = null;

        try {
            jsonAirIndex = airPollutionService.getAirIndex(stationId);
        } catch (IOException e) {
            logger.error(e);
        }

        if (jsonAirIndex == null) {
            logger.error("unable to fetch air index for station with id " + Format.stationId(stationId));
            return null;
        }

        return jsonObjectFactory.fromJson(jsonAirIndex);
    }
}
