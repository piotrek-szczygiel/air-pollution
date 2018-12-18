package air.pollution;

import java.time.LocalDateTime;

class SensorMeasurement {
    Parameter parameter;
    LocalDateTime date;
    float value;

    SensorMeasurement(Parameter parameter, LocalDateTime date, float value) {
        this.parameter = parameter;
        this.date = date;
        this.value = value;
    }
}
