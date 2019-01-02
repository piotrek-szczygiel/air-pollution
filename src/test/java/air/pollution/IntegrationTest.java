package air.pollution;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class IntegrationTest {
    private final static String integrationTestResultPath =
            "src/test/resources/integration_test_expected_result.txt";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    Cache cache;

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @Before
    public void setUpCache() {
        Station s1 = new Station();
        s1.setId(1);
        s1.setName("Example station, 1");

        AirIndex s1_airIndex1 = new AirIndex();
        s1_airIndex1.setAirQuality(Quality.GOOD);
        s1_airIndex1.setValue(Parameter.PM10, Quality.EXCELLENT);
        s1_airIndex1.setValue(Parameter.PM25, Quality.GOOD);

        when(cache.getAirIndex(s1.getId())).thenReturn(s1_airIndex1);

        Sensor s1_sensor1 = new Sensor();
        s1_sensor1.setId(100);
        s1_sensor1.setParameter(Parameter.PM10);

        Sensor s1_sensor2 = new Sensor();
        s1_sensor2.setId(101);
        s1_sensor2.setParameter(Parameter.PM25);

        SensorMeasurement s1_sensor1_measurement1 = new SensorMeasurement();
        s1_sensor1_measurement1.setValue(10.0f);
        s1_sensor1_measurement1.setParameter(Parameter.PM10);
        s1_sensor1_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s1_sensor1_measurement2 = new SensorMeasurement();
        s1_sensor1_measurement2.setValue(12.0f);
        s1_sensor1_measurement2.setParameter(Parameter.PM10);
        s1_sensor1_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s1_sensor1.getId()))
                .thenReturn(List.of(s1_sensor1_measurement1, s1_sensor1_measurement2));

        SensorMeasurement s1_sensor2_measurement1 = new SensorMeasurement();
        s1_sensor2_measurement1.setValue(5.0f);
        s1_sensor2_measurement1.setParameter(Parameter.PM25);
        s1_sensor2_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s1_sensor2_measurement2 = new SensorMeasurement();
        s1_sensor2_measurement2.setValue(7.0f);
        s1_sensor2_measurement2.setParameter(Parameter.PM25);
        s1_sensor2_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s1_sensor2.getId()))
                .thenReturn(List.of(s1_sensor2_measurement1, s1_sensor2_measurement2));

        when(cache.getAllSensors(s1.getId())).thenReturn(List.of(s1_sensor1, s1_sensor2));

        Station s2 = new Station();
        s2.setId(2);
        s2.setName("Example station, 2");

        AirIndex s2_airIndex1 = new AirIndex();
        s2_airIndex1.setAirQuality(Quality.MODERATE);
        s2_airIndex1.setValue(Parameter.PM10, Quality.MODERATE);
        s2_airIndex1.setValue(Parameter.PM25, Quality.FAIR);
        s2_airIndex1.setValue(Parameter.SO2, Quality.EXCELLENT);
        s2_airIndex1.setValue(Parameter.C6H6, Quality.EXCELLENT);

        when(cache.getAirIndex(s2.getId())).thenReturn(s2_airIndex1);

        Sensor s2_sensor1 = new Sensor();
        s2_sensor1.setId(200);
        s2_sensor1.setParameter(Parameter.PM10);

        Sensor s2_sensor2 = new Sensor();
        s2_sensor2.setId(201);
        s2_sensor2.setParameter(Parameter.PM25);

        Sensor s2_sensor3 = new Sensor();
        s2_sensor3.setId(202);
        s2_sensor3.setParameter(Parameter.SO2);

        Sensor s2_sensor4 = new Sensor();
        s2_sensor4.setId(203);
        s2_sensor4.setParameter(Parameter.C6H6);

        SensorMeasurement s2_sensor1_measurement1 = new SensorMeasurement();
        s2_sensor1_measurement1.setValue(70.12f);
        s2_sensor1_measurement1.setParameter(Parameter.PM10);
        s2_sensor1_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s2_sensor1_measurement2 = new SensorMeasurement();
        s2_sensor1_measurement2.setValue(67.5f);
        s2_sensor1_measurement2.setParameter(Parameter.PM10);
        s2_sensor1_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s2_sensor1.getId()))
                .thenReturn(List.of(s2_sensor1_measurement1, s2_sensor1_measurement2));

        SensorMeasurement s2_sensor2_measurement1 = new SensorMeasurement();
        s2_sensor2_measurement1.setValue(66.77f);
        s2_sensor2_measurement1.setParameter(Parameter.PM25);
        s2_sensor2_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s2_sensor2_measurement2 = new SensorMeasurement();
        s2_sensor2_measurement2.setValue(56.1f);
        s2_sensor2_measurement2.setParameter(Parameter.PM25);
        s2_sensor2_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s2_sensor2.getId()))
                .thenReturn(List.of(s2_sensor2_measurement1, s2_sensor2_measurement2));

        SensorMeasurement s2_sensor3_measurement1 = new SensorMeasurement();
        s2_sensor3_measurement1.setValue(3.13f);
        s2_sensor3_measurement1.setParameter(Parameter.SO2);
        s2_sensor3_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s2_sensor3_measurement2 = new SensorMeasurement();
        s2_sensor3_measurement2.setValue(6.98f);
        s2_sensor3_measurement2.setParameter(Parameter.SO2);
        s2_sensor3_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s2_sensor3.getId()))
                .thenReturn(List.of(s2_sensor3_measurement1, s2_sensor3_measurement2));

        SensorMeasurement s2_sensor4_measurement1 = new SensorMeasurement();
        s2_sensor4_measurement1.setValue(3.2f);
        s2_sensor4_measurement1.setParameter(Parameter.C6H6);
        s2_sensor4_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s2_sensor4_measurement2 = new SensorMeasurement();
        s2_sensor4_measurement2.setValue(4.73f);
        s2_sensor4_measurement2.setParameter(Parameter.C6H6);
        s2_sensor4_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        SensorMeasurement s2_sensor4_measurement3 = new SensorMeasurement();
        s2_sensor4_measurement3.setValue(4.28f);
        s2_sensor4_measurement3.setParameter(Parameter.C6H6);
        s2_sensor4_measurement3.setDate(LocalDateTime.of(2019, 1, 1, 2, 0));

        when(cache.getSensorMeasurements(s2_sensor4.getId()))
                .thenReturn(List.of(s2_sensor4_measurement1,
                        s2_sensor4_measurement2, s2_sensor4_measurement3));

        when(cache.getAllSensors(s2.getId()))
                .thenReturn(List.of(s2_sensor1, s2_sensor2, s2_sensor3, s2_sensor4));

        Station s3 = new Station();
        s3.setId(3);
        s3.setName("Example station, 3");

        AirIndex s3_airIndex1 = new AirIndex();
        s3_airIndex1.setAirQuality(Quality.MODERATE);
        s3_airIndex1.setValue(Parameter.PM10, Quality.BAD);
        s3_airIndex1.setValue(Parameter.O3, Quality.EXCELLENT);
        s3_airIndex1.setValue(Parameter.NO2, Quality.FAIR);
        s3_airIndex1.setValue(Parameter.CO, Quality.GOOD);

        when(cache.getAirIndex(s3.getId())).thenReturn(s3_airIndex1);

        Sensor s3_sensor1 = new Sensor();
        s3_sensor1.setId(300);
        s3_sensor1.setParameter(Parameter.PM10);

        Sensor s3_sensor2 = new Sensor();
        s3_sensor2.setId(301);
        s3_sensor2.setParameter(Parameter.O3);

        Sensor s3_sensor3 = new Sensor();
        s3_sensor3.setId(302);
        s3_sensor3.setParameter(Parameter.NO2);

        Sensor s3_sensor4 = new Sensor();
        s3_sensor4.setId(303);
        s3_sensor4.setParameter(Parameter.CO);

        SensorMeasurement s3_sensor1_measurement1 = new SensorMeasurement();
        s3_sensor1_measurement1.setValue(160.42f);
        s3_sensor1_measurement1.setParameter(Parameter.PM10);
        s3_sensor1_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s3_sensor1_measurement2 = new SensorMeasurement();
        s3_sensor1_measurement2.setValue(155.75f);
        s3_sensor1_measurement2.setParameter(Parameter.PM10);
        s3_sensor1_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s3_sensor1.getId()))
                .thenReturn(List.of(s3_sensor1_measurement1, s3_sensor1_measurement2));

        SensorMeasurement s3_sensor2_measurement1 = new SensorMeasurement();
        s3_sensor2_measurement1.setValue(70.77f);
        s3_sensor2_measurement1.setParameter(Parameter.O3);
        s3_sensor2_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s3_sensor2_measurement2 = new SensorMeasurement();
        s3_sensor2_measurement2.setValue(69.32f);
        s3_sensor2_measurement2.setParameter(Parameter.O3);
        s3_sensor2_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s3_sensor2.getId()))
                .thenReturn(List.of(s3_sensor2_measurement1, s3_sensor2_measurement2));

        SensorMeasurement s3_sensor3_measurement1 = new SensorMeasurement();
        s3_sensor3_measurement1.setValue(172.13f);
        s3_sensor3_measurement1.setParameter(Parameter.NO2);
        s3_sensor3_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s3_sensor3_measurement2 = new SensorMeasurement();
        s3_sensor3_measurement2.setValue(175.98f);
        s3_sensor3_measurement2.setParameter(Parameter.NO2);
        s3_sensor3_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        when(cache.getSensorMeasurements(s3_sensor3.getId()))
                .thenReturn(List.of(s3_sensor3_measurement1, s3_sensor3_measurement2));

        SensorMeasurement s3_sensor4_measurement1 = new SensorMeasurement();
        s3_sensor4_measurement1.setValue(3000.2f);
        s3_sensor4_measurement1.setParameter(Parameter.CO);
        s3_sensor4_measurement1.setDate(LocalDateTime.of(2019, 1, 1, 0, 0));

        SensorMeasurement s3_sensor4_measurement2 = new SensorMeasurement();
        s3_sensor4_measurement2.setValue(3146.73f);
        s3_sensor4_measurement2.setParameter(Parameter.CO);
        s3_sensor4_measurement2.setDate(LocalDateTime.of(2019, 1, 1, 1, 0));

        SensorMeasurement s3_sensor4_measurement3 = new SensorMeasurement();
        s3_sensor4_measurement3.setValue(3128.28f);
        s3_sensor4_measurement3.setParameter(Parameter.CO);
        s3_sensor4_measurement3.setDate(LocalDateTime.of(2019, 1, 1, 2, 0));

        when(cache.getSensorMeasurements(s3_sensor4.getId()))
                .thenReturn(List.of(s3_sensor4_measurement1,
                        s3_sensor4_measurement2, s3_sensor4_measurement3));

        when(cache.getAllSensors(s3.getId()))
                .thenReturn(List.of(s3_sensor1, s3_sensor2, s3_sensor3, s3_sensor4));

        when(cache.getAllStations()).thenReturn(List.of(s1, s2, s3));
    }

    @Test
    public void integrationTest() throws IOException {
        Options options =
                new Options(
                        cache.getAllStations(),
                        Arrays.asList(Parameter.values()),
                        CommandUtils.getLowestDate(cache),
                        CommandUtils.getHighestDate(cache),
                        0,
                        0,
                        23
                );

        OptionStrategy strategy =
                new OptionStrategy(true, true, true, true, true, true, true, true, true);

        strategy.invoke(cache, options);

        String actualResult = outContent.toString()
                .replace("\r\n", "\n");

        String expectedResult =
                new String(Files.readAllBytes(Paths.get(integrationTestResultPath)),
                        StandardCharsets.UTF_8);

        assertEquals(expectedResult, actualResult);
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}
