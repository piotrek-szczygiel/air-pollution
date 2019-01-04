package air.pollution;

/**
 * JSON representation of fetched sensor for GSON parser.
 *
 * @see Sensor
 */
class JsonSensor {
    int id;
    Param param;

    class Param {
        String paramFormula;
    }
}
