package air.pollution;

import java.util.Date;

class JsonSensorMeasurements {
    String key;
    Value[] values;

    class Value {
        Date date;
        float value;
    }
}
