package air.pollution;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class DataCollector {
    private static SimpleDateFormat sdfNormal = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Gson gson;

    DataCollector() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
            String dateStr = json.getAsString();
            if (dateStr.contains("-")) {
                try {
                    return sdfNormal.parse(dateStr);
                } catch (ParseException ex) {
                    throw new JsonParseException(ex);
                }
            }

            return new Date(json.getAsJsonPrimitive().getAsLong());
        });

        gson = builder.create();
    }

    Station[] collectAllStations() throws IOException, JsonSyntaxException {
        URL url = new URL("http://api.gios.gov.pl/pjp-api/rest/station/findAll");
        String result = getRawData(url);
        return gson.fromJson(result, Station[].class);
    }

    private String getRawData(URL url) throws IOException, JsonSyntaxException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    Sensor[] collectAllSensors(int stationId) throws IOException, JsonSyntaxException {
        URL url = new URL("http://api.gios.gov.pl/pjp-api/rest/station/sensors/" + stationId);
        String result = getRawData(url);
        return gson.fromJson(result, Sensor[].class);
    }

    SensorData collectSensorData(int sensorId) throws IOException, JsonSyntaxException {
        URL url = new URL("http://api.gios.gov.pl/pjp-api/rest/data/getData/" + sensorId);
        String result = getRawData(url);
        return gson.fromJson(result, SensorData.class);
    }

    AirIndex collectAirIndexData(int stationId) throws IOException, JsonSyntaxException {
        URL url = new URL("http://api.gios.gov.pl/pjp-api/rest/aqindex/getIndex/" + stationId);
        String result = getRawData(url);
        return gson.fromJson(result, AirIndex.class);
    }
}
