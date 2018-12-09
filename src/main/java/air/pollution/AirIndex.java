package air.pollution;

import java.util.HashMap;
import java.util.Map;

import static org.fusesource.jansi.Ansi.ansi;

class AirIndex {
    private Map<Parameter, String> values;
    private String airQuality;

    AirIndex() {
        values = new HashMap<>();
        airQuality = "-";
    }

    void setValue(Parameter parameter, String value) {
        values.put(parameter, value);
    }

    void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ansi().fgCyan().a("  ").a("Air quality: ").a(getFormattedIndex(airQuality))
                .a("\n-----------------------------\n"));
        for (Parameter parameter : Parameter.values()) {
            String index = values.getOrDefault(parameter, "-");
            sb.append(ansi().fgCyan().a("  ").a(parameter).a(":\t")
                    .a(getFormattedIndex(index)).a("\n"));
        }

        return sb.toString();
    }

    private String getFormattedIndex(String index) {
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
