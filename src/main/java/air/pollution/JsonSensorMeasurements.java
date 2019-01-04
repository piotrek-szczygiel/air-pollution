package air.pollution;

import java.time.LocalDateTime;

/**
 * JSON representation of fetched measurements for GSON parser.
 *
 * @see SensorMeasurement
 */
class JsonSensorMeasurements {
    String key;
    Value[] values;

    class Value {
        LocalDateTime date;
        float value;
    }
}
