package air.pollution;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class ApiObjectCollectorTest {
    private final JsonObjectFactory jsonObjectFactory = new JsonObjectFactory();

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    AirPollutionService airPollutionService;

    private ApiObjectCollector api;

    @BeforeClass
    public static void setupClass() {
        // disable logging in tests
        Logger.setLevel(ErrorLevel.DISABLE);
    }

    @DataProvider
    public static Object[][] dataProviderJsonStations() {
        JsonStation[] stations1 = {
                new JsonStation() {{
                    id = 1;
                    stationName = "station 1";
                }},
                new JsonStation() {{
                    id = 2;
                    stationName = "station 2";
                }},
                new JsonStation() {{
                    id = 3;
                    stationName = "station 3";
                }}
        };

        JsonStation[] stations2 = {
                new JsonStation() {{
                    id = 143;
                    stationName = "Cracow, First Street";
                }},
                new JsonStation() {{
                    id = 246;
                    stationName = "Cracow, Second Street";
                }},
                new JsonStation() {{
                    id = 333;
                    stationName = "Cracow, Third Street";
                }}
        };

        JsonStation[] stations3 = {
                new JsonStation() {{
                    id = 10;
                    stationName = "duplicate name";
                }},
                new JsonStation() {{
                    id = 10;
                    stationName = "duplicate name";
                }}
        };

        return new Object[][]{
                {stations1, "station 3"},
                {stations2, "Cracow, Third Street"},
                {stations3, "duplicate name"}
        };
    }

    @DataProvider
    public static Object[] dataProviderJsonSensors() {
        JsonSensor[] sensors1 = {
                new JsonSensor() {{
                    id = 1;
                    param = new JsonSensor.Param() {{
                        paramFormula = "PM10";
                    }};
                }},
                new JsonSensor() {{
                    id = 2;
                    param = new JsonSensor.Param() {{
                        paramFormula = "SO2";
                    }};
                }},
                new JsonSensor() {{
                    id = 3;
                    param = new JsonSensor.Param() {{
                        paramFormula = "NO2";
                    }};
                }}
        };

        JsonSensor[] sensors2 = {
                new JsonSensor() {{
                    id = 123;
                    param = new JsonSensor.Param() {{
                        paramFormula = "O3";
                    }};
                }},
                new JsonSensor() {{
                    id = 321;
                    param = new JsonSensor.Param() {{
                        paramFormula = "C6H6";
                    }};
                }},
                new JsonSensor() {{
                    id = 314;
                    param = new JsonSensor.Param() {{
                        paramFormula = "PM10";
                    }};
                }}
        };

        return new Object[]{
                sensors1,
                sensors2
        };
    }

    @DataProvider
    public static Object[] dataProviderJsonSensorData() {
        return new Object[]{
                new JsonSensorMeasurements() {{
                    values = new JsonSensorMeasurements.Value[]{
                            new JsonSensorMeasurements.Value() {{
                                date = new Date(0);
                                value = 1.0f;
                            }},
                            new JsonSensorMeasurements.Value() {{
                                date = new Date(1);
                                value = 2.0f;
                            }}
                    };
                }},
                new JsonSensorMeasurements() {{
                    values = new JsonSensorMeasurements.Value[]{
                            new JsonSensorMeasurements.Value() {{
                                date = new Date(10);
                                value = 10.0f;
                            }},
                            new JsonSensorMeasurements.Value() {{
                                date = new Date(20);
                                value = 20.0f;
                            }}
                    };
                }}
        };
    }

    @DataProvider
    public static Object[][] dataProviderJsonAirIndex() {
        return new Object[][]{
                {
                        new JsonAirIndex() {{
                            stIndexLevel = new IndexLevel() {{
                                indexLevelName = "Bardzo dobry";
                            }};
                        }},
                        "Bardzo dobry"
                },
                {
                        new JsonAirIndex() {{
                            stIndexLevel = new IndexLevel() {{
                                indexLevelName = "Umiarkowany";
                            }};
                        }},
                        "Umiarkowany"
                },
                {
                        new JsonAirIndex() {{
                            stIndexLevel = new IndexLevel();
                        }},
                        "-"
                }
        };
    }

    @Before
    public void initialize() {
        api = new ApiObjectCollector(airPollutionService, jsonObjectFactory);
    }

    @Test
    public void getAllStations_SingleStation_SingleStationFromApi() throws IOException {
        when(airPollutionService.getAllStations()).thenReturn(Collections.singletonList(new JsonStation()));

        assertEquals(1, api.getAllStations().size());
    }

    @Test
    public void getAllStations_Null_NoStationsFromApi() throws IOException {
        when(airPollutionService.getAllStations()).thenReturn(Collections.emptyList());

        assertNull(api.getAllStations());
    }

    @Test
    public void getAllSensors_Null_NoSensorsFromApi() throws IOException {
        when(airPollutionService.getAllSensors(anyInt())).thenReturn(Collections.emptyList());

        assertNull(api.getAllSensors(0));
    }

    @Test
    @UseDataProvider("dataProviderJsonSensors")
    public void getAllSensors_ThreeSensors_ProvidedSensors(JsonSensor[] jsonSensors) throws IOException {
        when(airPollutionService.getAllSensors(anyInt())).thenReturn(Arrays.asList(jsonSensors));

        assertEquals(3, api.getAllSensors(0).size());
    }

    @Test
    public void getSensorData_Null_NoDataFromApi() throws IOException {
        when(airPollutionService.getSensorMeasurements(anyInt())).thenReturn(new JsonSensorMeasurements());

        assertEquals(0, api.getSensorMeasurements(0).size());
    }

    @Test
    @UseDataProvider("dataProviderJsonSensorData")
    public void getSensorData_ThreeMeasurements_ProvidedData(JsonSensorMeasurements jsonSensorData)
            throws IOException {

        when(airPollutionService.getSensorMeasurements(anyInt())).thenReturn(jsonSensorData);

        assertEquals(2, api.getSensorMeasurements(0).size());
    }

    @Test
    public void getAirIndex_Null_NoDataFromApi() throws IOException {
        when(airPollutionService.getAirIndex(anyInt())).thenReturn(new JsonAirIndex());

        assertEquals("-", api.getAirIndex(0).getAirQuality());
    }

    @Test
    @UseDataProvider("dataProviderJsonAirIndex")
    public void getAirIndex_ExpectedAirIndex_ProvidedAirIndexes(JsonAirIndex jsonAirIndex, String airQuality)
            throws IOException {

        when(airPollutionService.getAirIndex(anyInt())).thenReturn(jsonAirIndex);

        assertEquals(airQuality, api.getAirIndex(0).getAirQuality());
    }
}
