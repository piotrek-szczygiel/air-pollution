package air.pollution;

import java.util.List;
import java.util.stream.Collectors;

import static air.pollution.Format.format;

/**
 * Collects objects from air pollution service and transforms them into usable objects.
 *
 * @see AirPollutionService
 */
class ApiObjectCollector {

    // How many times do we retry API request before giving up
    private static final int RETRY_COUNT = 5;

    private AirPollutionService airPollutionService;
    private JsonObjectFactory jsonObjectFactory;

    private Logger logger = Logger.getLogger(this);

    ApiObjectCollector(AirPollutionService airPollutionService, JsonObjectFactory jsonObjectFactory) {
        this.airPollutionService = airPollutionService;
        this.jsonObjectFactory = jsonObjectFactory;
    }

    /**
     * Returns list of all stations or null if none exists.
     *
     * @return list of stations
     * @see Station
     */
    List<Station> getAllStations() {
        List<JsonStation> jsonStations = null;

        for (int i = 0; jsonStations == null && i < RETRY_COUNT; i++) {
            jsonStations = airPollutionService.fetchAllStations();
        }

        if (jsonStations == null || jsonStations.size() < 1) {
            logger.fatal("unable to fetch any station");
            return null;
        }

        List<Station> stations = jsonStations
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());

        logger.debug("%s stations fetched", format(stations.size()));

        return stations;
    }

    /**
     * Returns list of sensors for specified station or null if none exists.
     *
     * @param stationId id of station
     * @return list of sensors
     * @see Sensor
     */
    List<Sensor> getAllSensors(int stationId) {
        List<JsonSensor> jsonSensors = null;

        for (int i = 0; jsonSensors == null && i < RETRY_COUNT; i++) {
            jsonSensors = airPollutionService.fetchAllSensors(stationId);
        }

        if (jsonSensors == null) {
            logger.fatal("unable to fetch sensors for station with id %s", format(stationId));
            return null;
        }

        return jsonSensors
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());
    }

    /**
     * Returns list of measurements for specified sensor or null if none exists.
     *
     * @param sensorId if of sensor
     * @return list of measurements
     * @see SensorMeasurement
     */
    List<SensorMeasurement> getSensorMeasurements(int sensorId) {
        JsonSensorMeasurements jsonSensorMeasurements = null;

        for (int i = 0; jsonSensorMeasurements == null && i < RETRY_COUNT; i++) {
            jsonSensorMeasurements = airPollutionService.fetchSensorMeasurements(sensorId);
        }

        if (jsonSensorMeasurements == null) {
            logger.fatal("unable to fetch sensor measurements for sensor with id %s", format(sensorId));
            return null;
        }

        List<SensorMeasurement> sensorMeasurements = jsonObjectFactory.fromJson(jsonSensorMeasurements);

        logger.debug("fetched %s measurements", format(sensorMeasurements.size()));

        return sensorMeasurements;
    }

    /**
     * Returns air index for specified station or null if it doesn't exist.
     *
     * @param stationId id of station
     * @return air index
     * @see AirIndex
     */
    AirIndex getAirIndex(int stationId) {
        JsonAirIndex jsonAirIndex = null;

        for (int i = 0; jsonAirIndex == null && i < RETRY_COUNT; i++) {
            jsonAirIndex = airPollutionService.fetchAirIndex(stationId);
        }

        if (jsonAirIndex == null) {
            logger.fatal("unable to fetch air index for station with id %s", format(stationId));
            return null;
        }

        return jsonObjectFactory.fromJson(jsonAirIndex);
    }
}
