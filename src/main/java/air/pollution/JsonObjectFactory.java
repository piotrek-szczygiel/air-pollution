package air.pollution;

import java.util.ArrayList;
import java.util.List;

class JsonObjectFactory {
    AirIndex fromJson(JsonAirIndex jsonAirIndex) {
        AirIndex airIndex = new AirIndex();

        if (jsonAirIndex.pm10IndexLevel != null) {
            airIndex.setValue(Parameter.PM10, jsonAirIndex.pm10IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.pm25IndexLevel != null) {
            airIndex.setValue(Parameter.PM25, jsonAirIndex.pm25IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.o3IndexLevel != null) {
            airIndex.setValue(Parameter.O3, jsonAirIndex.o3IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.no2IndexLevel != null) {
            airIndex.setValue(Parameter.NO2, jsonAirIndex.no2IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.so2IndexLevel != null) {
            airIndex.setValue(Parameter.SO2, jsonAirIndex.so2IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.c6h6IndexLevel != null) {
            airIndex.setValue(Parameter.C6H6, jsonAirIndex.c6h6IndexLevel.indexLevelName);
        }
        if (jsonAirIndex.coIndexLevel != null) {
            airIndex.setValue(Parameter.CO, jsonAirIndex.coIndexLevel.indexLevelName);
        }
        if (jsonAirIndex.stIndexLevel != null) {
            airIndex.setAirQuality(jsonAirIndex.stIndexLevel.indexLevelName);
        }

        return airIndex;
    }

    Station fromJson(JsonStation jsonStation) {
        Station station = new Station();

        station.setId(jsonStation.id);
        station.setName(jsonStation.stationName);

        return station;
    }

    Sensor fromJson(JsonSensor jsonSensor) {
        Sensor sensor = new Sensor();

        sensor.setId(jsonSensor.id);
        sensor.setParameter(jsonSensor.param.paramFormula);

        return sensor;
    }

    List<SensorMeasurement> fromJson(JsonSensorMeasurements jsonSensorMeasurements) {
        List<SensorMeasurement> measurements = new ArrayList<>();

        if (jsonSensorMeasurements.values == null) {
            return measurements;
        }

        for (JsonSensorMeasurements.Value value : jsonSensorMeasurements.values) {
            // add only non zero values
            if (value.value != 0.0f) {
                measurements.add(new SensorMeasurement(
                        Parameter.fromString(jsonSensorMeasurements.key), value.date, value.value));
            }
        }

        return measurements;
    }
}
