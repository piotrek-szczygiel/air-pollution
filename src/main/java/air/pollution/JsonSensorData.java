package air.pollution;

import java.util.Date;

class JsonSensorData {
    String key;
    Value[] values;

    class Value {
        Date date;
        float value;
    }
}
