package air.pollution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class containing customized Gson object.
 * <p>
 * Because API sometimes provides date in yyyy-MM-dd HH:mm:ss format,
 * and sometimes in unix time format, we have to differentiate between them.
 */
class JsonDecoder {
    private static SimpleDateFormat normalDateFormat
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Gson gson = new GsonBuilder().registerTypeAdapter(
            Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                String dateStr = json.getAsString();

                // If yyyy-MM-dd HH:mm:ss format
                if (dateStr.contains("-")) {
                    try {
                        return normalDateFormat.parse(dateStr);
                    } catch (ParseException ex) {
                        throw new JsonParseException(ex);
                    }
                }

                // If unix time format
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }).create();

    static Gson getGson() {
        return gson;
    }
}
