package air.pollution;

import java.util.List;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

class ApiObjectCollector {
    private AirPollutionService airPollutionService;
    private JsonObjectFactory jsonObjectFactory;
    private Logger logger;

    ApiObjectCollector(AirPollutionService airPollutionService, JsonObjectFactory jsonObjectFactory) {
        this.airPollutionService = airPollutionService;
        this.jsonObjectFactory = jsonObjectFactory;

        logger = new Logger(this);
    }

    Station getStation(String stationName) {
        List<JsonStation> jsonStations = airPollutionService.getAllStations().blockingFirst();

        List<Station> found = jsonStations
                .stream()
                .filter(jsonStation -> jsonStation.stationName.equals(stationName))
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());

        if (found.size() < 1) {
            logger.log(ErrorLevel.ERROR, ansi()
                    .a("unable to find station: ")
                    .fgBrightYellow()
                    .a(stationName));

            return null;
        }

        if (found.size() > 1) {
            logger.log(ErrorLevel.WARN, "found more than one station");
        }


        Station station = found.get(0);

        logger.log(ErrorLevel.DEBUG, ansi()
                .a("found station ")
                .fgBrightYellow()
                .a(station.getName())
                .reset()
                .a(", with id ")
                .fgBrightGreen()
                .a(station.getId()));

        return station;
    }

    List<Station> getAllStations() {
        logger.log(ErrorLevel.DEBUG, "fetching all stations...");

        List<JsonStation> jsonStations = airPollutionService.getAllStations().blockingFirst();

        List<Station> stations = jsonStations
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());

        if (stations.size() < 1) {
            logger.log(ErrorLevel.ERROR, "no stations fetched");
            return null;
        }

        logger.log(ErrorLevel.DEBUG, ansi()
                .fgBrightRed()
                .a(stations.size())
                .reset()
                .a(" stations fetched"));

        return stations;
    }

    Sensor getSensor(int stationId, Parameter parameter) {
        List<Sensor> sensors = getAllSensors(stationId);

        if (sensors == null) {
            return null;
        }

        for (Sensor sensor : sensors) {
            if (sensor.getParameter() == parameter) {
                logger.log(ErrorLevel.DEBUG, ansi()
                        .a("found ")
                        .fgBrightMagenta()
                        .a(parameter)
                        .reset()
                        .a(" sensor, with id ")
                        .fgCyan()
                        .a(sensor.getId()));

                return sensor;
            }
        }

        logger.log(ErrorLevel.ERROR, "unable to find specified sensor: " + parameter);
        return null;
    }

    List<Sensor> getAllSensors(int stationId) {
        logger.log(ErrorLevel.DEBUG, ansi()
                .a("fetching all sensors for station with id ")
                .fgBrightGreen()
                .a(stationId)
                .reset()
                .a("..."));

        List<JsonSensor> jsonSensors = airPollutionService.getSensors(stationId).blockingFirst();

        if (jsonSensors == null || jsonSensors.size() < 1) {
            logger.log(ErrorLevel.ERROR, "unable to fetch sensors");
            return null;
        }

        return jsonSensors
                .stream()
                .map(jsonObjectFactory::fromJson)
                .collect(Collectors.toList());
    }

    List<SensorMeasurement> getSensorData(int sensorId) {
        logger.log(ErrorLevel.DEBUG, ansi()
                .a("fetching sensor data for sensor with id ")
                .fgCyan()
                .a(sensorId)
                .reset()
                .a("..."));

        JsonSensorData jsonSensorData = airPollutionService.getSensorData(sensorId).blockingFirst();

        if (jsonSensorData == null) {
            logger.log(ErrorLevel.ERROR, "unable to fetch sensor data");
            return null;
        }

        List<SensorMeasurement> sensorData = jsonObjectFactory.fromJson(jsonSensorData);

        logger.log(ErrorLevel.DEBUG, ansi()
                .a("fetched ")
                .fgBrightRed()
                .a(sensorData.size())
                .reset()
                .a(" measurements"));

        return sensorData;
    }

    AirIndex getAirIndex(int stationId) {
        JsonAirIndex jsonAirIndex = airPollutionService.getAirIndex(stationId).blockingFirst();

        if (jsonAirIndex == null) {
            logger.log(ErrorLevel.ERROR, ansi()
                    .a("unable to fetch air index for station with id ")
                    .fgBrightGreen()
                    .a(stationId));

            return null;
        }

        return jsonObjectFactory.fromJson(jsonAirIndex);
    }
}
