package air.pollution;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

class JsonDecoder {
    private static SimpleDateFormat normalDateFormat
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static Gson gson = new GsonBuilder().registerTypeAdapter(
            Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                String dateStr = json.getAsString();
                if (dateStr.contains("-")) {
                    try {
                        return normalDateFormat.parse(dateStr);
                    } catch (ParseException ex) {
                        throw new JsonParseException(ex);
                    }
                }

                return new Date(json.getAsJsonPrimitive().getAsLong());
            }).create();

    static Gson getGson() {
        return gson;
    }
}
