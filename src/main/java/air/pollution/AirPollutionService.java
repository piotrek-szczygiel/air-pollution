package air.pollution;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

interface AirPollutionService {
    @GET("station/findAll")
    Observable<List<JsonStation>> getAllStations();

    @GET("station/sensors/{stationId}")
    Observable<List<JsonSensor>> getSensors(@Path("stationId") int stationId);

    @GET("data/getData/{sensorId}")
    Observable<JsonSensorData> getSensorData(@Path("sensorId") int sensorId);

    @GET("aqindex/getIndex/{stationId}")
    Observable<JsonAirIndex> getAirIndex(@Path("stationId") int stationId);
}
