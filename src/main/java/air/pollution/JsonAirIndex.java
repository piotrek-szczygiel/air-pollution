package air.pollution;

/**
 * JSON representation of fetched air index for GSON parser.
 *
 * @see AirIndex
 */
class JsonAirIndex {
    IndexLevel stIndexLevel;
    IndexLevel so2IndexLevel;
    IndexLevel no2IndexLevel;
    IndexLevel coIndexLevel;
    IndexLevel pm10IndexLevel;
    IndexLevel pm25IndexLevel;
    IndexLevel o3IndexLevel;
    IndexLevel c6h6IndexLevel;

    class IndexLevel {
        String indexLevelName;
    }
}
