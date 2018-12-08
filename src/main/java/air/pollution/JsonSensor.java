package air.pollution;

class JsonSensor {
    int id;
    int stationId;

    Param param;

    class Param {
        String paramName;
        String paramFormula;
        String paramCode;
        int idParam;
    }
}
