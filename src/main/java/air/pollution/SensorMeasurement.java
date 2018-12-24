package air.pollution;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

class SensorMeasurement implements Comparable<SensorMeasurement> {
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

    @Override
    public int compareTo(@NotNull SensorMeasurement other) {
        return Float.compare(value, other.getValue());
    }

    float getValue() {
        return value;
    }

    void setValue(float value) {
        this.value = value;
    }
}
