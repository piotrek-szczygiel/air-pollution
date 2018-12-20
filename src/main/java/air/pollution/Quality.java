package air.pollution;

enum Quality {
    EXCELLENT("Excellent"),
    GOOD("Good"),
    MODERATE("Moderate"),
    FAIR("Fair"),
    POOR("Poor"),
    BAD("Bad"),
    UNKNOWN("-");

    private String qualityName;

    Quality(String qualityName) {
        this.qualityName = qualityName;
    }

    static Quality fromIndexLevel(JsonAirIndex.IndexLevel indexLevel) {
        if (indexLevel == null || indexLevel.indexLevelName == null) {
            return UNKNOWN;
        }

        return fromString(indexLevel.indexLevelName);
    }

    static Quality fromString(String indexLevelName) {
        indexLevelName = Utils.normalizeString(indexLevelName);

        switch (indexLevelName) {
            case "bardzo dobry":
                return EXCELLENT;
            case "dobry":
                return GOOD;
            case "umiarkowany":
                return MODERATE;
            case "dostateczny":
                return FAIR;
            case "zly":
                return POOR;
            case "bardzo zly":
                return BAD;
            default:
                return UNKNOWN;
        }
    }

    @Override
    public String toString() {
        return qualityName;
    }
}
