package air.pollution;

/**
 * Holds information about sensor.
 */
class Sensor {

    private int id;
    private Parameter parameter;

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }


    Parameter getParameter() {
        return parameter;
    }

    void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }
}
