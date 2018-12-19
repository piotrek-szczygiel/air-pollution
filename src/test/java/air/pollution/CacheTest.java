package air.pollution;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CacheTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    ApiObjectCollector apiObjectCollector;

    private Cache cache;

    @BeforeClass
    public static void disableLogging() {
        Utils.disableStderr();
    }

    @Before
    public void setUp() {
        cache = new Cache();
        cache.setApiObjectCollector(apiObjectCollector);
    }

    @Test
    public void cacheStations_FullTest() {
        Station station1 = new Station();
        station1.setId(0);
        station1.setName("example1");

        Station station2 = new Station();
        station2.setId(1);
        station2.setName("example2");

        AirIndex airIndex1 = new AirIndex();
        airIndex1.setAirQuality("Dobry");

        AirIndex airIndex2 = new AirIndex();
        airIndex2.setAirQuality("Bardzo dobry");

        Sensor sensor1 = new Sensor();
        sensor1.setId(100);

        Sensor sensor2 = new Sensor();
        sensor2.setId(101);

        Sensor sensor3 = new Sensor();
        sensor3.setId(200);

        Sensor sensor4 = new Sensor();
        sensor4.setId(201);

        SensorMeasurement sensorMeasurement1 = new SensorMeasurement();
        sensorMeasurement1.setValue(0.0f);

        SensorMeasurement sensorMeasurement2 = new SensorMeasurement();
        sensorMeasurement2.setValue(1.0f);

        SensorMeasurement sensorMeasurement3 = new SensorMeasurement();
        sensorMeasurement3.setValue(2.0f);

        SensorMeasurement sensorMeasurement4 = new SensorMeasurement();
        sensorMeasurement4.setValue(3.0f);

        SensorMeasurement sensorMeasurement5 = new SensorMeasurement();
        sensorMeasurement5.setValue(4.0f);

        SensorMeasurement sensorMeasurement6 = new SensorMeasurement();
        sensorMeasurement6.setValue(5.0f);

        SensorMeasurement sensorMeasurement7 = new SensorMeasurement();
        sensorMeasurement7.setValue(6.0f);

        SensorMeasurement sensorMeasurement8 = new SensorMeasurement();
        sensorMeasurement8.setValue(7.0f);

        when(apiObjectCollector.getAllStations()).thenReturn(List.of(station1, station2));

        when(apiObjectCollector.getAirIndex(0)).thenReturn(airIndex1);
        when(apiObjectCollector.getAirIndex(1)).thenReturn(airIndex2);

        when(apiObjectCollector.getAllSensors(0)).thenReturn(List.of(sensor1, sensor2));
        when(apiObjectCollector.getAllSensors(1)).thenReturn(List.of(sensor3, sensor4));

        when(apiObjectCollector.getSensorMeasurements(100)).thenReturn(List.of(sensorMeasurement1, sensorMeasurement2));
        when(apiObjectCollector.getSensorMeasurements(101)).thenReturn(List.of(sensorMeasurement3, sensorMeasurement4));
        when(apiObjectCollector.getSensorMeasurements(200)).thenReturn(List.of(sensorMeasurement5, sensorMeasurement6));
        when(apiObjectCollector.getSensorMeasurements(201)).thenReturn(List.of(sensorMeasurement7, sensorMeasurement8));

        assertEquals(2, cache.getAllStations().size());

        cache.cacheStations(cache.getAllStations(), 1);

        assertEquals(airIndex1, cache.getAirIndex(0));
        assertEquals(airIndex2, cache.getAirIndex(1));

        assertEquals(2, cache.getAllSensors(0).size());
        assertEquals(2, cache.getAllSensors(1).size());

        assertTrue(cache.getAllSensors(0).containsAll(List.of(sensor1, sensor2)));
        assertTrue(cache.getAllSensors(1).containsAll(List.of(sensor3, sensor4)));

        assertEquals(2, cache.getSensorMeasurements(100).size());
        assertEquals(2, cache.getSensorMeasurements(101).size());
        assertEquals(2, cache.getSensorMeasurements(200).size());
        assertEquals(2, cache.getSensorMeasurements(201).size());

        assertTrue(cache.getSensorMeasurements(100).containsAll(List.of(sensorMeasurement1, sensorMeasurement2)));
        assertTrue(cache.getSensorMeasurements(101).containsAll(List.of(sensorMeasurement3, sensorMeasurement4)));
        assertTrue(cache.getSensorMeasurements(200).containsAll(List.of(sensorMeasurement5, sensorMeasurement6)));
        assertTrue(cache.getSensorMeasurements(201).containsAll(List.of(sensorMeasurement7, sensorMeasurement8)));

        verify(apiObjectCollector, times(1)).getAllStations();

        verify(apiObjectCollector, times(1)).getAirIndex(0);
        verify(apiObjectCollector, times(1)).getAirIndex(1);
        verify(apiObjectCollector, times(2)).getAirIndex(anyInt());

        verify(apiObjectCollector, times(1)).getAllSensors(0);
        verify(apiObjectCollector, times(1)).getAllSensors(1);
        verify(apiObjectCollector, times(2)).getAllSensors(anyInt());

        verify(apiObjectCollector, times(1)).getSensorMeasurements(100);
        verify(apiObjectCollector, times(1)).getSensorMeasurements(101);
        verify(apiObjectCollector, times(1)).getSensorMeasurements(200);
        verify(apiObjectCollector, times(1)).getSensorMeasurements(201);
        verify(apiObjectCollector, times(4)).getSensorMeasurements(anyInt());
    }

    @Test
    public void getAllStations_OneApiRequest_ForCacheRequest() {
        Station station = new Station();
        station.setId(0);
        station.setName("example");

        when(apiObjectCollector.getAllStations()).thenReturn(List.of(station));

        assertEquals(1, cache.getAllStations().size());
        assertEquals(1, cache.getAllStations().size());
        assertEquals(1, cache.getAllStations().size());

        verify(apiObjectCollector, times(1)).getAllStations();
    }

    @Test
    public void getAllStations_CorrectStations_ProvidedStations() {
        Station station1 = new Station();
        station1.setId(0);
        station1.setName("example1");

        Station station2 = new Station();
        station2.setId(1);
        station2.setName("example2");

        when(apiObjectCollector.getAllStations()).thenReturn(List.of(station1, station2));

        assertEquals(2, cache.getAllStations().size());
        assertTrue(cache.getAllStations().contains(station1));
        assertTrue(cache.getAllStations().contains(station2));

        verify(apiObjectCollector, times(1)).getAllStations();
    }

    @Test
    public void getAllSensors_OneApiRequest_ForCacheRequest() {
        when(apiObjectCollector.getAllSensors(anyInt())).thenReturn(List.of(new Sensor()));

        assertEquals(1, cache.getAllSensors(0).size());
        assertEquals(1, cache.getAllSensors(0).size());

        verify(apiObjectCollector, times(1)).getAllSensors(0);

        assertEquals(1, cache.getAllSensors(1).size());
        assertEquals(1, cache.getAllSensors(1).size());

        verify(apiObjectCollector, times(1)).getAllSensors(1);

        verify(apiObjectCollector, times(2)).getAllSensors(anyInt());
    }

    @Test
    public void getAllSensors_CorrectSensors_ProvidedSensors() {
        Sensor sensor1 = new Sensor();
        sensor1.setId(100);
        sensor1.setParameter("PM10");

        Sensor sensor2 = new Sensor();
        sensor2.setId(101);
        sensor2.setParameter("C6H6");

        when(apiObjectCollector.getAllSensors(0)).thenReturn(List.of(sensor1, sensor2));

        Sensor sensor3 = new Sensor();
        sensor3.setId(200);
        sensor3.setParameter("O3");

        Sensor sensor4 = new Sensor();
        sensor4.setId(201);
        sensor4.setParameter("CO");

        when(apiObjectCollector.getAllSensors(1)).thenReturn(List.of(sensor3, sensor4));

        assertEquals(2, cache.getAllSensors(0).size());
        assertEquals(2, cache.getAllSensors(1).size());

        assertTrue(cache.getAllSensors(0).contains(sensor1));
        assertTrue(cache.getAllSensors(0).contains(sensor2));

        assertTrue(cache.getAllSensors(1).contains(sensor3));
        assertTrue(cache.getAllSensors(1).contains(sensor4));

        verify(apiObjectCollector, times(1)).getAllSensors(0);
        verify(apiObjectCollector, times(1)).getAllSensors(1);

        verify(apiObjectCollector, times(2)).getAllSensors(anyInt());
    }

    @Test
    public void getSensorMeasurements_OneApiRequest_ForCacheRequest() {
        when(apiObjectCollector.getSensorMeasurements(anyInt())).thenReturn(List.of(new SensorMeasurement()));

        assertEquals(1, cache.getSensorMeasurements(0).size());
        assertEquals(1, cache.getSensorMeasurements(0).size());

        assertEquals(1, cache.getSensorMeasurements(1).size());
        assertEquals(1, cache.getSensorMeasurements(1).size());

        verify(apiObjectCollector, times(1)).getSensorMeasurements(0);
        verify(apiObjectCollector, times(1)).getSensorMeasurements(1);

        verify(apiObjectCollector, times(2)).getSensorMeasurements(anyInt());
    }

    @Test
    public void getSensorMeasurements_CorrectMeasurements_ProvidedMeasurements() {
        SensorMeasurement measurement1 = new SensorMeasurement();
        measurement1.setValue(1.0f);

        SensorMeasurement measurement2 = new SensorMeasurement();
        measurement2.setValue(2.0f);

        when(apiObjectCollector.getSensorMeasurements(0)).thenReturn(List.of(measurement1, measurement2));

        SensorMeasurement measurement3 = new SensorMeasurement();
        measurement3.setValue(3.0f);

        SensorMeasurement measurement4 = new SensorMeasurement();
        measurement4.setValue(4.0f);

        when(apiObjectCollector.getSensorMeasurements(1)).thenReturn(List.of(measurement3, measurement4));

        assertEquals(2, cache.getSensorMeasurements(0).size());
        assertEquals(2, cache.getSensorMeasurements(1).size());

        assertTrue(cache.getSensorMeasurements(0).contains(measurement1));
        assertTrue(cache.getSensorMeasurements(0).contains(measurement2));

        assertTrue(cache.getSensorMeasurements(1).contains(measurement3));
        assertTrue(cache.getSensorMeasurements(1).contains(measurement4));

        verify(apiObjectCollector, times(1)).getSensorMeasurements(0);
        verify(apiObjectCollector, times(1)).getSensorMeasurements(1);

        verify(apiObjectCollector, times(2)).getSensorMeasurements(anyInt());
    }

    @Test
    public void getAirIndex_OneApiRequest_ForCacheRequest() {
        AirIndex airIndex = new AirIndex();

        when(apiObjectCollector.getAirIndex(anyInt())).thenReturn(airIndex);

        assertNotNull(cache.getAirIndex(0));
        assertNotNull(cache.getAirIndex(0));

        verify(apiObjectCollector, times(1)).getAirIndex(anyInt());
    }

    @Test
    public void getAirIndex_CorrectAirIndexes_ProvidedAirIndexes() {
        AirIndex airIndex1 = new AirIndex();
        airIndex1.setAirQuality("Dobry");

        when(apiObjectCollector.getAirIndex(0)).thenReturn(airIndex1);

        AirIndex airIndex2 = new AirIndex();
        airIndex2.setAirQuality("Bardzo dobry");

        when(apiObjectCollector.getAirIndex(1)).thenReturn(airIndex2);

        assertEquals(airIndex1, cache.getAirIndex(0));
        assertEquals(airIndex2, cache.getAirIndex(1));

        verify(apiObjectCollector, times(1)).getAirIndex(0);
        verify(apiObjectCollector, times(1)).getAirIndex(1);
        verify(apiObjectCollector, times(2)).getAirIndex(anyInt());
    }
}
