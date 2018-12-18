package air.pollution;

class Sensor {
    private int id;
    private Parameter parameter;

    int getId() {
        return id;
    }

    String getIdColored() {
        return Format.sensorId(id);
    }

    void setId(int id) {
        this.id = id;
    }

    Parameter getParameter() {
        return parameter;
    }

    String getParameterColored() {
        return Format.parameter(parameter);
    }

    void setParameter(String formula) {
        parameter = Parameter.fromString(formula);
    }
}
