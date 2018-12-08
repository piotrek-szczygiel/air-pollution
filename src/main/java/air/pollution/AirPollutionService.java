package air.pollution;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

interface AirPollutionService {
    @GET("station/findAll")
    Call<List<JSONStation>> getAllStations();

    @GET("station/sensors/{stationId}")
    Call<List<JSONSensor>> getSensors(@Path("stationId") int stationId);

    @GET("data/getData/{sensorId}")
    Call<JSONSensorData> getSensorData(@Path("sensorId") int sensorId);

    @GET("aqindex/getIndex/{stationId}")
    Call<JSONAirIndex> getAirIndex(@Path("stationId") int stationId);
}
