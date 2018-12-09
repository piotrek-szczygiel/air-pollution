package air.pollution;

import java.util.ArrayList;
import java.util.List;

class JsonObjectFactory {
    private JsonObjectFactory() {

    }

    static JsonObjectFactory getInstance() {
        return JsonObjectFactoryHolder.INSTANCE;
    }

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

    List<SensorMeasurement> fromJson(JsonSensorData jsonSensorData) {
        List<SensorMeasurement> measurements = new ArrayList<>();

        for (var value : jsonSensorData.values) {
            // add only non zero values
            if (value.value != 0.0f) {
                measurements.add(new SensorMeasurement(value.date, value.value));
            }
        }

        return measurements;
    }

    private static class JsonObjectFactoryHolder {
        private static final JsonObjectFactory INSTANCE = new JsonObjectFactory();
    }
}
