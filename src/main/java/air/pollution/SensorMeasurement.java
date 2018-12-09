package air.pollution;

import java.util.Date;

class SensorMeasurement {
    Date date;
    float value;

    SensorMeasurement(Date date, float value) {
        this.date = date;
        this.value = value;
    }
}
