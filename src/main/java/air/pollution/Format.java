package air.pollution;

import com.google.common.base.Stopwatch;
import org.fusesource.jansi.Ansi;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.fusesource.jansi.Ansi.ansi;

/**
 * Utilities for colorfully formatting various objects.
 */
class Format {

    // Thresholds table holds values from API website
    // They describe pollution levels and are used to determine
    // measurements colors

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


    // Date formatters

    private static final DateTimeFormatter TIMESTAMP_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM, HH:mm:ss");
    static final DateTimeFormatter MEASUREMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm,  dd MMMM");


    /**
     * Colorfully format given station like: name id.
     *
     * @param station station
     * @return string result
     */
    static String format(Station station) {
        return ansi().fgYellow().a(station.getName()).a(" ").fgBlue().a(station.getId()).reset().toString();
    }


    /**
     * Colorfully format given sensor like: id parameter.
     *
     * @param sensor sensor
     * @return string result
     */
    static String format(Sensor sensor) {
        return ansi().fgCyan().a(sensor.getId()).a(" ").fgMagenta().a(sensor.getParameter()).reset().toString();
    }


    /**
     * Colorfully format given parameter.
     *
     * @param parameter parameter
     * @return string result
     */
    static String format(Parameter parameter) {
        return ansi().fgMagenta().a(parameter.toString()).reset().toString();
    }


    /**
     * Colorfully format given integer.
     *
     * @param integer integer
     * @return string result
     */
    static String format(int integer) {
        return format((long) integer);
    }


    /**
     * Colorfully format give long.
     *
     * @param longInteger long integer
     * @return string result
     */
    static String format(long longInteger) {
        return ansi().fgBrightBlue().a(longInteger).reset().toString();
    }


    /**
     * Colorfully format provided floating number with 2 decimal places.
     *
     * @param floating floating number
     * @return string result
     */
    static String format(float floating) {
        return ansi().fgBrightBlue().a(String.format("%.2f", floating)).reset().toString();
    }


    /**
     * Colorfully format stopwatch result.
     *
     * @param stopwatch stopwatch
     * @return string result
     */
    static String format(Stopwatch stopwatch) {
        return ansi().fgBrightRed().a(stopwatch).reset().toString();
    }


    /**
     * Colorfully format filename.
     *
     * @param file file
     * @return string result
     */
    static String format(File file) {
        return ansi().fgBrightMagenta().a(file.getPath()).reset().toString();
    }


    /**
     * Colorfully format single character.
     *
     * @param character character
     * @return string result
     */
    static String format(char character) {
        return ansi().fgBrightCyan().a(character).reset().toString();
    }


    /**
     * Colorfully format measurement.
     * <p>
     * If rightAlign is set, then measurement will be aligned to 6th column (format %6s).
     *
     * @param value      measurement value
     * @param parameter  measurement parameter
     * @param rightAlign right alignment
     * @return string result
     */
    static String format(float value, Parameter parameter, boolean rightAlign) {
        String unit = " \u00b5g/m3";
        String color = getMeasurementColor(value, parameter);


        if (value >= 1000.f) {
            value /= 1000.f;
            unit = " mg/m3";
        }

        return color
                + String.format(rightAlign ? "%6s" : "%s", String.format("%.2f", value))
                + unit
                + ansi().reset();
    }


    /**
     * Get appropriate color of the measurement depending on the pollution severity.
     *
     * @param value     measurement value
     * @param parameter measurement parameter
     * @return string result
     */
    static String getMeasurementColor(float value, Parameter parameter) {
        Ansi color = ansi().fgDefault();

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

        return color.toString();
    }


    /**
     * Colorfully format date.
     *
     * @param date date
     * @return string result
     */
    static String format(LocalDateTime date) {
        return ansi().fgCyan().a(date.format(TIMESTAMP_DATE_FORMATTER)).reset().toString();
    }


    /**
     * Colorfully format provided air index.
     * <p>
     * Display every present parameter quality with color dependent on pollution severity.
     *
     * @param airIndex air index
     * @return string result
     */
    static String format(AirIndex airIndex) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                .append(ansi().fgCyan())
                .append("Overall air quality: ")
                .append(format(airIndex.getAirQuality()))
                .append("\n----------------------------------");

        for (Parameter parameter : Parameter.values()) {
            Quality quality = airIndex.getValue(parameter);

            stringBuilder
                    .append(ansi().fgCyan())
                    .append("\n")
                    .append(parameter)
                    .append(":\t")
                    .append(format(quality));
        }

        return stringBuilder.toString();
    }


    /**
     * Colorfully format quality.
     *
     * @param quality quality
     * @return string result
     */
    static String format(Quality quality) {
        Ansi color;

        switch (quality) {
            case EXCELLENT:
                color = COLOR_THRESHOLDS[0];
                break;
            case GOOD:
                color = COLOR_THRESHOLDS[1];
                break;
            case MODERATE:
                color = COLOR_THRESHOLDS[2];
                break;
            case FAIR:
                color = COLOR_THRESHOLDS[3];
                break;
            case POOR:
                color = COLOR_THRESHOLDS[4];
                break;
            case BAD:
                color = COLOR_THRESHOLDS[5];
                break;
            default:
                color = ansi().fgDefault();
                break;
        }

        return color.toString() + quality + ansi().reset().toString();
    }
}
