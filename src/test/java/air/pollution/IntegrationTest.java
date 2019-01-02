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
        s1.setName("example1");

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

        when(cache.getAllSensors(s1.getId())).thenReturn(List.of(s1_sensor1, s1_sensor2));

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

        Station s2 = new Station();
        s2.setId(2);
        s2.setName("example2");

        Station s3 = new Station();
        s3.setId(3);
        s3.setName("example3");

        when(cache.getAllStations()).thenReturn(List.of(s1));
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

//        try (PrintWriter out = new PrintWriter(integrationTestResultPath)) {
//            out.print(actualResult);
//        }
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}
