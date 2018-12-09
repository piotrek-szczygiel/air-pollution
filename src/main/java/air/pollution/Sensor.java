package air.pollution;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

class Sensor {
    private int id;
    private Parameter parameter;

    private List<SensorMeasurement> measurements;

    Sensor() {
        measurements = new ArrayList<>();
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    Parameter getParameter() {
        return parameter;
    }

    void setParameter(String formula) {
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
        }
    }

    void setMeasurements(List<SensorMeasurement> measurements) {
        this.measurements = measurements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (SensorMeasurement measurement : measurements) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm\td MMMM");
            String strDate = dateFormat.format(measurement.date);
            sb.append("    ").append(getFormatted(measurement.value)).append("\t\t").append(strDate).append("\n");
        }

        return sb.toString();
    }

    private String getFormatted(float value) {
        String c1 = ansi().fgGreen().toString();
        String c2 = ansi().fgBrightGreen().toString();
        String c3 = ansi().fgBrightYellow().toString();
        String c4 = ansi().fgYellow().toString();
        String c5 = ansi().fgBrightRed().toString();
        String c6 = ansi().fgRed().toString();

        String[] colors = {c1, c2, c3, c4, c5, c6};

        String color = ansi().fgDefault().toString();

        String unit = " \u00b5g";

        int[] thresholds = null;

        if (value > 0) {
            switch (parameter) {
                case PM10:
                    thresholds = new int[]{21, 61, 101, 141, 201};
                    break;
                case PM25:
                    thresholds = new int[]{13, 37, 61, 85, 121};
                    break;
                case O3:
                    thresholds = new int[]{71, 121, 151, 181, 241};
                    break;
                case NO2:
                    thresholds = new int[]{41, 101, 151, 201, 401};
                    break;
                case SO2:
                    thresholds = new int[]{51, 101, 201, 351, 501};
                    break;
                case C6H6:
                    thresholds = new int[]{6, 11, 16, 21, 51};
                    break;
                case CO:
                    value /= 1000.f;
                    unit = " mg";
                    thresholds = new int[]{3, 7, 11, 15, 21};
                    break;
            }

            boolean found = false;
            for (int i = 0; i < 5; i++) {
                if (value < thresholds[i]) {
                    color = colors[i];
                    found = true;
                    break;
                }
            }

            if (!found) {
                color = colors[5];
            }
        }

        return color + String.format("%.2f", value) + unit + ansi().reset().toString();
    }
}
