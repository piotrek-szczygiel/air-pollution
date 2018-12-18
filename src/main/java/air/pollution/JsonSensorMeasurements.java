package air.pollution;

import java.time.LocalDateTime;

class JsonSensorMeasurements {
    String key;
    Value[] values;

    class Value {
        LocalDateTime date;
        float value;
    }
}
