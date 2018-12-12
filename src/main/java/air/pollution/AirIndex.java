package air.pollution;

import java.util.HashMap;
import java.util.Map;

class AirIndex {
    private Map<Parameter, String> values;
    private String airQuality;

    AirIndex() {
        values = new HashMap<>();
        airQuality = "-";
    }

    String getValue(Parameter parameter) {
        return values.getOrDefault(parameter, "-");
    }

    void setValue(Parameter parameter, String value) {
        if (value != null) {
            values.put(parameter, value);
        }
    }

    String getAirQuality() {
        return airQuality;
    }

    void setAirQuality(String airQuality) {
        if (airQuality != null) {
            this.airQuality = airQuality;
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Air quality: ").append(airQuality);

        for (Map.Entry<Parameter, String> entry : values.entrySet()) {
            stringBuilder
                    .append("\n")
                    .append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue());
        }

        return stringBuilder.toString();
    }
}
