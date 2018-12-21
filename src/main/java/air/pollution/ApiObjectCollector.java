package air.pollution;

import java.util.List;
import java.util.stream.Collectors;

class ApiObjectCollector {
    private AirPollutionService airPollutionService;
    private JsonObjectFactory jsonObjectFactory;

    private Logger logger = Logger.getLogger(this);

    ApiObjectCollector(AirPollutionService airPollutionService, JsonObjectFactory jsonObjectFactory) {
        this.airPollutionService = airPollutionService;
        this.jsonObjectFactory = jsonObjectFactory;
    }

    List<Station> getAllStations() {
        List<JsonStation> jsonStations = airPollutionService.getAllStations();

        if (jsonStations == null) {
            logger.fatal("unable to fetch stations");
            return null;
        }

        List<Station> stations = jsonStations
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());

        if (stations.size() < 1) {
            logger.fatal("no stations fetched");
            return null;
        }

        logger.debug(Format.size(stations.size()) + "~ stations fetched");

        return stations;
    }

    List<Sensor> getAllSensors(int stationId) {
        List<JsonSensor> jsonSensors = airPollutionService.getAllSensors(stationId);

        if (jsonSensors == null) {
            logger.fatal("unable to fetch sensors for station with id " + Format.stationId(stationId));
            return null;
        }

        if (jsonSensors.size() < 1) {
            logger.fatal("unable to fetch sensors for station with id " + Format.stationId(stationId));
            return null;
        }

        return jsonSensors
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());
    }

    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        JsonSensorMeasurements jsonSensorMeasurements = airPollutionService.getSensorMeasurements(sensorId);

        if (jsonSensorMeasurements == null) {
            logger.fatal("unable to fetch sensor measurements for sensor with id " + Format.sensorId(sensorId));
            return null;
        }

        List<SensorMeasurement> sensorMeasurements = jsonObjectFactory.fromJson(jsonSensorMeasurements);

        logger.debug("fetched " + Format.size(sensorMeasurements.size()) + "~ measurements");

        return sensorMeasurements;
    }

    AirIndex getAirIndex(int stationId) {
        JsonAirIndex jsonAirIndex = airPollutionService.getAirIndex(stationId);

        if (jsonAirIndex == null) {
            logger.fatal("unable to fetch air index for station with id " + Format.stationId(stationId));
            return null;
        }

        return jsonObjectFactory.fromJson(jsonAirIndex);
    }
}
