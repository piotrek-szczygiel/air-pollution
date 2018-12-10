package air.pollution;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.List;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

class ApiObjectCollector {
    private AirPollutionService service;
    private JsonObjectFactory jsonObjectFactory;
    private Logger logger;

    private ApiObjectCollector() {
        jsonObjectFactory = JsonObjectFactory.getInstance();

        service = new Retrofit.Builder()
                .baseUrl("http://api.gios.gov.pl/pjp-api/rest/")
                .addConverterFactory(GsonConverterFactory.create(JsonDecoder.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(AirPollutionService.class);

        logger = new Logger(this);
    }

    static ApiObjectCollector getInstance() {
        return ApiObjectCollectorHolder.INSTANCE;
    }

    Station getStation(String stationName) {
        List<Station> stations = getAllStations();

        if (stations == null) {
            return null;
        }

        List<Station> found = stations
                .stream()
                .filter(station -> station.getName().equals(stationName))
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

        List<JsonStation> jsonStations = service.getAllStations().blockingFirst();

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

    private List<Sensor> getAllSensors(int stationId) {
        logger.log(ErrorLevel.DEBUG, ansi()
                .a("fetching all sensors for station with id ")
                .fgBrightGreen()
                .a(stationId)
                .reset()
                .a("..."));

        List<JsonSensor> jsonSensors = service.getSensors(stationId).blockingFirst();

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

        JsonSensorData jsonSensorData = service.getSensorData(sensorId).blockingFirst();

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
        JsonAirIndex jsonAirIndex = service.getAirIndex(stationId).blockingFirst();

        if (jsonAirIndex == null) {
            logger.log(ErrorLevel.ERROR, ansi()
                    .a("unable to fetch air index for station with id ")
                    .fgBrightGreen()
                    .a(stationId));

            return null;
        }

        return jsonObjectFactory.fromJson(jsonAirIndex);
    }

    private static class ApiObjectCollectorHolder {
        private static final ApiObjectCollector INSTANCE = new ApiObjectCollector();
    }
}
