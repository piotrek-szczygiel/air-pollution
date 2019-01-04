package air.pollution;

import java.util.HashMap;
import java.util.Map;

/**
 * Information about overall air quality.
 */
class AirIndex {

    private Map<Parameter, Quality> values = new HashMap<>();
    private Quality airQuality = Quality.UNKNOWN;


    Quality getValue(Parameter parameter) {
        return values.getOrDefault(parameter, Quality.UNKNOWN);
    }

    void setValue(Parameter parameter, Quality quality) {
        values.put(parameter, quality);
    }


    Quality getAirQuality() {
        return airQuality;
    }

    void setAirQuality(Quality airQuality) {
        this.airQuality = airQuality;
    }
}
