package air.pollution;

import java.time.LocalDateTime;

class JsonAirIndex {
    int id;

    IndexLevel stIndexLevel;
    LocalDateTime stCalcDate;
    LocalDateTime stSourceDataDate;

    IndexLevel so2IndexLevel;
    LocalDateTime so2CalcDate;
    LocalDateTime so2SourceDataDate;

    IndexLevel no2IndexLevel;
    LocalDateTime no2CalcDate;
    LocalDateTime no2SourceDataDate;

    IndexLevel coIndexLevel;
    LocalDateTime coCalcDate;
    LocalDateTime coSourceDataDate;

    IndexLevel pm10IndexLevel;
    LocalDateTime pm10CalcDate;
    LocalDateTime pm10SourceDataDate;

    IndexLevel pm25IndexLevel;
    LocalDateTime pm25CalcDate;
    LocalDateTime pm25SourceDataDate;

    IndexLevel o3IndexLevel;
    LocalDateTime o3CalcDate;
    LocalDateTime o3SourceDataDate;

    IndexLevel c6h6IndexLevel;
    LocalDateTime c6h6CalcDate;
    LocalDateTime c6h6SourceDataDate;

    class IndexLevel {
        int id;
        String indexLevelName;
    }
}
