package air.pollution;

import org.fusesource.jansi.Ansi;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.fusesource.jansi.Ansi.ansi;

class Format {
    private static final int[] PM10_THRESHOLDS = new int[]{21, 61, 101, 141, 201};
    private static final int[] PM25_THRESHOLDS = new int[]{13, 37, 61, 85, 121};
    private static final int[] O3_THRESHOLDS = new int[]{71, 121, 151, 181, 241};
    private static final int[] NO2_THRESHOLDS = new int[]{41, 101, 151, 201, 401};
    private static final int[] SO2_THRESHOLDS = new int[]{51, 101, 201, 351, 501};
    private static final int[] C6H6_THRESHOLDS = new int[]{6, 11, 16, 21, 51};
    private static final int[] CO_THRESHOLDS = new int[]{3000, 7000, 11000, 15000, 21000};

    private static final Ansi[] COLOR_THRESHOLDS = {
            ansi().fgGreen(),
            ansi().fgBrightGreen(),
            ansi().fgBrightYellow(),
            ansi().fgYellow(),
            ansi().fgBrightRed(),
            ansi().fgRed()
    };

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd MMMM");

    static String stationName(String stationName) {
        return ansi().fgYellow().a(stationName).reset().toString();
    }

    static String stationId(int stationId) {
        return ansi().fgGreen().a(stationId).reset().toString();
    }

    static String sensorId(int sensorId) {
        return ansi().fgCyan().a(sensorId).reset().toString();
    }

    static String parameter(Parameter parameter) {
        return ansi().fgMagenta().a(parameter).reset().toString();
    }

    static String size(Object size) {
        return ansi().fgRed().a(size).reset().toString();
    }

    static String format(AirIndex airIndex) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(ansi().fgCyan())
                .append("Air quality:\t")
                .append(getColoredIndex(airIndex.getAirQuality()))
                .append("\n----------------------------");

        for (Parameter parameter : Parameter.values()) {
            String index = airIndex.getValue(parameter);

            stringBuilder
                    .append(ansi().fgCyan())
                    .append("\n")
                    .append(parameter)
                    .append(":\t\t")
                    .append(getColoredIndex(index));
        }

        return stringBuilder.toString();
    }

    private static String getColoredIndex(String index) {
        Ansi color;

        switch (index) {
            case "Bardzo dobry":
                color = COLOR_THRESHOLDS[0];
                break;
            case "Dobry":
                color = COLOR_THRESHOLDS[1];
                break;
            case "Umiarkowany":
                color = COLOR_THRESHOLDS[2];
                break;
            case "Dostateczny":
                color = COLOR_THRESHOLDS[3];
                break;
            case "Z\u0142y":
                color = COLOR_THRESHOLDS[4];
                break;
            case "Bardzo z\u0142y":
                color = COLOR_THRESHOLDS[5];
                break;
            default:
                color = ansi().fgDefault();
                break;
        }

        return color.toString() + index + ansi().reset().toString();
    }

    static String format(List<SensorMeasurement> measurements, int top) {
        StringBuilder stringBuilder = new StringBuilder();

        int counter = 0;

        boolean addNewline = false;
        for (SensorMeasurement measurement : measurements) {
            if (top != 0) {
                counter++;
            }

            if (counter > top) {
                break;
            }

            if (addNewline) {
                stringBuilder.append("\n");
            } else {
                addNewline = true;
            }

            stringBuilder.append(format(measurement));
        }

        return stringBuilder.toString();
    }

    static String format(SensorMeasurement measurement) {
        return getFormattedMeasurementValue(measurement.getParameter(), measurement.getValue())
                + "\t"
                + format(measurement.getDate());
    }

    static private String getFormattedMeasurementValue(Parameter parameter, float value) {
        Ansi color = ansi().fgDefault();

        String unit = " ug/m3";

        int[] thresholds = null;

        if (value > 0) {
            switch (parameter) {
                case PM10:
                    thresholds = PM10_THRESHOLDS;
                    break;
                case PM25:
                    thresholds = PM25_THRESHOLDS;
                    break;
                case O3:
                    thresholds = O3_THRESHOLDS;
                    break;
                case NO2:
                    thresholds = NO2_THRESHOLDS;
                    break;
                case SO2:
                    thresholds = SO2_THRESHOLDS;
                    break;
                case C6H6:
                    thresholds = C6H6_THRESHOLDS;
                    break;
                case CO:
                    thresholds = CO_THRESHOLDS;
                    break;
            }

            boolean found = false;
            for (int i = 0; i < 5; i++) {
                if (value < thresholds[i]) {
                    color = COLOR_THRESHOLDS[i];
                    found = true;
                    break;
                }
            }

            if (!found) {
                color = COLOR_THRESHOLDS[5];
            }
        }

        if (value >= 1000.f) {
            value /= 1000.f;
            unit = " mg/m3";
        }

        return color.toString() + String.format("%6s", String.format("%.2f", value)) + unit + ansi().reset().toString();
    }

    private static String format(LocalDateTime date) {
        return ansi().fgBrightBlack().a(date.format(DATE_FORMATTER)).reset().toString();
    }
}
