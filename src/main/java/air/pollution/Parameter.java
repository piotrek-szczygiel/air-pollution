package air.pollution;

/**
 * Parameters that we are measuring.
 */
enum Parameter {

    PM10,
    PM25,
    O3,
    NO2,
    SO2,
    C6H6,
    CO;


    /**
     * Create parameter object from given formula.
     *
     * @param formula formula
     * @return parameter
     */
    static Parameter fromString(String formula) {
        Parameter parameter = null;

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

        return parameter;
    }
}
