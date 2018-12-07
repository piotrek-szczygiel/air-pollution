package air.pollution;

import java.util.Date;

class AirIndex {
    int id;

    IndexLevel stIndexLevel;
    Date stCalcDate;
    Date stSourceDataDate;

    IndexLevel s02IndexLevel;
    Date s02CalcDate;
    Date s02SourceDataDate;

    IndexLevel no2IndexLevel;
    Date no2CalcDate;
    Date no2SourceDataDate;

    IndexLevel coIndexLevel;
    Date coCalcDate;
    Date coSourceDataDate;

    IndexLevel pm10IndexLevel;
    Date pm10CalcDate;
    Date pm10SourceDataDate;

    IndexLevel pm25IndexLevel;
    Date pm25CalcDate;
    Date pm25SourceDataDate;

    IndexLevel o3IndexLevel;
    Date o3CalcDate;
    Date o3SourceDataDate;

    IndexLevel c6h6IndexLevel;
    Date c6h6CalcDate;
    Date c6h6SourceDataDate;

    class IndexLevel {
        int id;
        String indexLevelName;
    }
}
