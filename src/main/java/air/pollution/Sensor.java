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
        switch (formula) {
            case "PM10":
                parameter = Parameter.PM10;
                break;
            case "PM2.5":
                parameter = Parameter.PM25;
                break;
            case "O3":
                parameter = Parameter.O3;
                break;
            case "NO2":
                parameter = Parameter.NO2;
                break;
            case "SO2":
                parameter = Parameter.SO2;
                break;
            case "C6H6":
                parameter = Parameter.C6H6;
                break;
            case "CO":
                parameter = Parameter.CO;
                break;
        }
    }
}
