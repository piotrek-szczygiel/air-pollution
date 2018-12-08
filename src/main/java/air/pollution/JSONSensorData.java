package air.pollution;

import java.util.Date;

class JSONSensorData {
    String key;
    Value[] values;

    class Value {
        Date date;
        float value;
    }
}
