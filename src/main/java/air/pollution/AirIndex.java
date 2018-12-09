package air.pollution;

import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

class AirIndex {
    private int id;
    private Map<Parameter, String> values;

    AirIndex(JsonAirIndex jsonAirIndex) {
        id = jsonAirIndex.id;
        values = new HashMap<>();

        if (jsonAirIndex.pm10IndexLevel != null) {
            values.put(Parameter.PM10, jsonAirIndex.pm10IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.pm25IndexLevel != null) {
            values.put(Parameter.PM25, jsonAirIndex.pm25IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.o3IndexLevel != null) {
            values.put(Parameter.O3, jsonAirIndex.o3IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.no2IndexLevel != null) {
            values.put(Parameter.NO2, jsonAirIndex.no2IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.so2IndexLevel != null) {
            values.put(Parameter.SO2, jsonAirIndex.so2IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.c6h6IndexLevel != null) {
            values.put(Parameter.C6H6, jsonAirIndex.c6h6IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.coIndexLevel != null) {
            values.put(Parameter.CO, jsonAirIndex.coIndexLevel.indexLevelName);
        }
        if (jsonAirIndex.stIndexLevel != null) {
            values.put(Parameter.ST, jsonAirIndex.stIndexLevel.indexLevelName);
        }
    }

    int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Parameter parameter : Parameter.values()) {
            sb.append(ansi().fgCyan().a("  ").a(parameter).a(":\t")
                    .reset().a(getFormattedIndex(parameter)).a("\n"));
        }

        return sb.toString();
    }

    private String getFormattedIndex(Parameter parameter) {
        String index = values.getOrDefault(parameter, "-");
        String color;

        switch (index) {
            case "Bardzo dobry":
                color = ansi().fgGreen().toString();
                break;
            case "Dobry":
                color = ansi().fgBrightGreen().toString();
                break;
            case "Umiarkowany":
                color = ansi().fgBrightYellow().toString();
                break;
            case "Dostateczny":
                color = ansi().fgYellow().toString();
                break;
            case "Zły":
                color = ansi().fgBrightRed().toString();
                break;
            case "Bardzo zły":
                color = ansi().fgRed().toString();
                break;
            default:
                color = ansi().fgDefault().toString();
                break;
        }

        return color + index + ansi().reset().toString();
    }
}
