package air.pollution;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class IntegrationTest {
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
    public void integrationTest() {
        final String expectedResult = "" +
                "example1\n" +
                "\n" +
                "\u001B[33mexample1 \u001B[34m1\u001B[m\n" +
                "\u001B[36mOverall air quality: \u001B[92mGood\u001B[m\n" +
                "----------------------------------\u001B[36m\n" +
                "PM10:\t\u001B[32mExcellent\u001B[m\u001B[36m\n" +
                "PM25:\t\u001B[92mGood\u001B[m\u001B[36m\n" +
                "O3:\t\u001B[39m-\u001B[m\u001B[36m\n" +
                "NO2:\t\u001B[39m-\u001B[m\u001B[36m\n" +
                "SO2:\t\u001B[39m-\u001B[m\u001B[36m\n" +
                "C6H6:\t\u001B[39m-\u001B[m\u001B[36m\n" +
                "CO:\t\u001B[39m-\u001B[m\n" +
                "\n" +
                "Showing \u001B[94m2\u001B[m measurements of \u001B[35mPM10\u001B[m for \u001B[33mexample1 " +
                "\u001B[34m1\u001B[m\n" +
                "\u001B[32m 10,00 µg/m3\u001B[m\t\u001B[90m00:00,  01 stycznia\u001B[m\n" +
                "\u001B[32m 12,00 µg/m3\u001B[m\t\u001B[90m01:00,  01 stycznia\u001B[m\n" +
                "\n" +
                "Showing \u001B[94m2\u001B[m measurements of \u001B[35mPM25\u001B[m for \u001B[33mexample1 " +
                "\u001B[34m1\u001B[m\n" +
                "\u001B[32m  5,00 µg/m3\u001B[m\t\u001B[90m00:00,  01 stycznia\u001B[m\n" +
                "\u001B[32m  7,00 µg/m3\u001B[m\t\u001B[90m01:00,  01 stycznia\u001B[m\n" +
                "\n" +
                "Average pollution for \u001B[94m1\u001B[m station\n" +
                "\u001B[35mPM10\u001B[m:\t\u001B[32m11,00 µg/m3\u001B[m\n" +
                "\u001B[35mPM25\u001B[m:\t\u001B[32m6,00 µg/m3\u001B[m\n" +
                "\u001B[35mO3\u001B[m:\t-\n" +
                "\u001B[35mNO2\u001B[m:\t-\n" +
                "\u001B[35mSO2\u001B[m:\t-\n" +
                "\u001B[35mC6H6\u001B[m:\t-\n" +
                "\u001B[35mCO\u001B[m:\t-\n" +
                "\n" +
                "Highest fluctuating parameter for \u001B[94m1\u001B[m station is \u001B[35mPM10\u001B[m with" +
                " " +
                "fluctuation ratio of \u001B[94m2,00\u001B[m.\n" +
                "\u001B[35mPM10\u001B[m oscillated between \u001B[32m10,00 µg/m3\u001B[m and \u001B[32m12,00 " +
                "µg/m3\u001B[m.\n" +
                "\n" +
                "Parameter with lowest value is \u001B[35mPM25\u001B[m measured in \u001B[33mexample1 " +
                "\u001B[34m1\u001B[m: \u001B[32m5,00 µg/m3\u001B[m\n" +
                "Parameter with highest value is \u001B[35mPM10\u001B[m measured in \u001B[33mexample1 " +
                "\u001B[34m1\u001B[m: \u001B[32m12,00 µg/m3\u001B[m\n" +
                "\n" +
                "Lowest measurement for \u001B[35mPM10\u001B[m parameter is \u001B[32m10,00 µg/m3\u001B[m in " +
                "\u001B[33mexample1 \u001B[34m1\u001B[m at \u001B[36m01 stycznia, 00:00:00\u001B[m\n" +
                "Highest measurement for \u001B[35mPM10\u001B[m parameter is \u001B[32m12,00 µg/m3\u001B[m in" +
                " " +
                "\u001B[33mexample1 \u001B[34m1\u001B[m at \u001B[36m01 stycznia, 01:00:00\u001B[m\n" +
                "\n" +
                "Lowest measurement for \u001B[35mPM25\u001B[m parameter is \u001B[32m5,00 µg/m3\u001B[m in " +
                "\u001B[33mexample1 \u001B[34m1\u001B[m at \u001B[36m01 stycznia, 00:00:00\u001B[m\n" +
                "Highest measurement for \u001B[35mPM25\u001B[m parameter is \u001B[32m7,00 µg/m3\u001B[m in " +
                "\u001B[33mexample1 \u001B[34m1\u001B[m at \u001B[36m01 stycznia, 01:00:00\u001B[m\n" +
                "\n" +
                "Top \u001B[94m1\u001B[m most polluted station for \u001B[35mPM10\u001B[m parameter\n" +
                "\u001B[32m 12,00 µg/m3\u001B[m \u001B[36m01 stycznia, 01:00:00\u001B[m: \u001B[33mexample1 " +
                "\u001B[34m1\u001B[m\n" +
                "\n" +
                "Top \u001B[94m1\u001B[m most polluted station for \u001B[35mPM25\u001B[m parameter\n" +
                "\u001B[32m  7,00 µg/m3\u001B[m \u001B[36m01 stycznia, 01:00:00\u001B[m: \u001B[33mexample1 " +
                "\u001B[34m1\u001B[m\n" +
                "\n" +
                "Graphing \u001B[35mPM10\u001B[m hourly pollution for \u001B[94m1\u001B[m station\n" +
                "\n" +
                "                                          6                                  12 [µg/m3]\n" +
                "      ┌───────────────────────────────────┼───────────────────────────────────┤\n" +
                "00:00 ┼ \u001B[32m██████████████████████████████████████████████████████████ \u001B[32m10,00" +
                " " +
                "\u001B[90m(example1)\u001B[m\n" +
                "01:00 ┼ \u001B[32m██████████████████████████████████████████████████████████████████████ " +
                "\u001B[32m12,00 \u001B[90m(example1)\u001B[m\n" +
                "      ┴\n" +
                "\n" +
                "Graphing \u001B[35mPM25\u001B[m hourly pollution for \u001B[94m1\u001B[m station\n" +
                "\n" +
                "                                          3                                  7 [µg/m3]\n" +
                "      ┌───────────────────────────────────┼───────────────────────────────────┤\n" +
                "00:00 ┼ \u001B[32m██████████████████████████████████████████████████ \u001B[32m5,00 " +
                "\u001B[90m" +
                "(example1)\u001B[m\n" +
                "01:00 ┼ \u001B[32m██████████████████████████████████████████████████████████████████████ " +
                "\u001B[32m7,00 \u001B[90m(example1)\u001B[m\n" +
                "      ┴\n";

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
        String actualResult = outContent.toString().replace("\r\n", "\n");

        assertEquals(expectedResult, actualResult);

//        try (PrintWriter out = new PrintWriter("integrationTestOutput.txt")) {
//            out.print(actualResult);
//        }
    }

    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}
