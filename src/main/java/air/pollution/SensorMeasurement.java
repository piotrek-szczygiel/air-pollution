package air.pollution;

import java.time.LocalDateTime;

class SensorMeasurement {
    private Parameter parameter;
    private LocalDateTime date;
    private float value;

    Parameter getParameter() {
        return parameter;
    }

    void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    LocalDateTime getDate() {
        return date;
    }

    void setDate(LocalDateTime date) {
        this.date = date;
    }

    float getValue() {
        return value;
    }

    void setValue(float value) {
        this.value = value;
    }
}
