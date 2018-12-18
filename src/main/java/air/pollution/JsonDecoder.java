package air.pollution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Class containing customized Gson object.
 * <p>
 * Because API sometimes provides date in yyyy-MM-dd HH:mm:ss format,
 * and sometimes in unix time format, we have to differentiate between them.
 */
class JsonDecoder {
    private static Gson gson = new GsonBuilder().registerTypeAdapter(
            LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                String dateStr = json.getAsString();

                // If yyyy-MM-dd HH:mm:ss format
                if (dateStr.contains("-")) {
                    return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }

                // If unix time format
                Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }).create();

    static Gson getGson() {
        return gson;
    }
}
