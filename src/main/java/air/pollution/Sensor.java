package air.pollution;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

class Sensor {
    private int id;
    private Parameter parameter;

    private List<Value> data;

    Sensor(JsonSensor jsonSensor) {
        id = jsonSensor.id;
        setParameter(jsonSensor.param.paramFormula);

        data = new ArrayList<>();
    }

    int getId() {
        return id;
    }

    Parameter getParameter() {
        return parameter;
    }

    void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    private void setParameter(String formula) {
        switch (formula) {
            case "PM10":
                parameter = Parameter.PM10;
                break;
            case "PM2.5":
                parameter = Parameter.PM25;
                break;
            case "O3":
                parameter = Parameter.O3;
                break;
            case "NO2":
                parameter = Parameter.NO2;
                break;
            case "SO2":
                parameter = Parameter.SO2;
                break;
            case "C6H6":
                parameter = Parameter.C6H6;
                break;
            case "CO":
                parameter = Parameter.CO;
                break;
            case "ST":
                parameter = Parameter.ST;
                break;
        }
    }

    void setSensorData(JsonSensorData jsonSensorData) {
        data.clear();

        boolean skip = true;
        for (var value : jsonSensorData.values) {
            // Skip first value, always 0.0 for some reason
            if (skip) {
                skip = false;
                continue;
            }

            data.add(new Value(value.date, value.value));
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();


        int counter = 0;
        final int show = 5;
        sb.append("displaying last 5 measurements:\n");
        for (Value value : data) {
            sb.append("    ").append(getFormatted(value.value)).append("\t").append(value.date).append("\n");
            if (++counter >= show) {
                break;
            }
        }

        return sb.toString();
    }

    private String getFormatted(float value) {
        String good = ansi().fgGreen().toString();
        String mediocre = ansi().fgYellow().toString();
        String bad = ansi().fgRed().toString();

        String color;

        String ug = ansi().fgBrightDefault().a("\u00b5g").reset().toString();

        String unit = "";

        switch (parameter) {
            case PM10:
                unit = ug;
                if (value < 0) {
                    color = "";
                } else if (value > 141) {
                    color = bad;
                } else if (value > 101) {
                    color = mediocre;
                } else {
                    color = good;
                }
                break;
            default:
                color = "";
                break;
        }

        return color + String.format("%.2f", value) + unit;
    }

    class Value {
        Date date;
        float value;

        Value(Date date, float value) {
            this.date = date;
            this.value = value;
        }
    }
}
