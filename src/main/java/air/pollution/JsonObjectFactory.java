package air.pollution;

import java.util.ArrayList;
import java.util.List;

class JsonObjectFactory {
    AirIndex fromJson(JsonAirIndex jsonAirIndex) {
        AirIndex airIndex = new AirIndex();

        airIndex.setValue(Parameter.PM10, Quality.fromIndexLevel(jsonAirIndex.pm10IndexLevel));
        airIndex.setValue(Parameter.PM25, Quality.fromIndexLevel(jsonAirIndex.pm25IndexLevel));
        airIndex.setValue(Parameter.O3, Quality.fromIndexLevel(jsonAirIndex.o3IndexLevel));
        airIndex.setValue(Parameter.NO2, Quality.fromIndexLevel(jsonAirIndex.no2IndexLevel));
        airIndex.setValue(Parameter.SO2, Quality.fromIndexLevel(jsonAirIndex.so2IndexLevel));
        airIndex.setValue(Parameter.C6H6, Quality.fromIndexLevel(jsonAirIndex.c6h6IndexLevel));
        airIndex.setValue(Parameter.CO, Quality.fromIndexLevel(jsonAirIndex.coIndexLevel));

        airIndex.setAirQuality(Quality.fromIndexLevel(jsonAirIndex.stIndexLevel));

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

        Parameter parameter = Parameter.fromString(jsonSensor.param.paramFormula);
        sensor.setParameter(parameter);

        return sensor;
    }

    List<SensorMeasurement> fromJson(JsonSensorMeasurements jsonSensorMeasurements) {
        List<SensorMeasurement> measurements = new ArrayList<>();

        if (jsonSensorMeasurements.values == null) {
            return measurements;
        }

        for (JsonSensorMeasurements.Value value : jsonSensorMeasurements.values) {

            // Add only non zero values
            if (value.value != 0.0f) {
                SensorMeasurement measurement = new SensorMeasurement();

                measurement.setParameter(Parameter.fromString(jsonSensorMeasurements.key));
                measurement.setDate(value.date);
                measurement.setValue(value.value);

                measurements.add(measurement);
            }
        }

        return measurements;
    }
}
