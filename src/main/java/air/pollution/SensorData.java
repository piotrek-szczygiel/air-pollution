package air.pollution;

import java.util.Date;

class SensorData {
    String key;
    Value[] values;

    class Value {
        Date date;
        float value;
    }
}
