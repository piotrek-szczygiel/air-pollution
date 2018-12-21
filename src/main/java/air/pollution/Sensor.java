package air.pollution;

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

    void setParameter(String formula) {
        parameter = Parameter.fromString(formula);
    }
}
