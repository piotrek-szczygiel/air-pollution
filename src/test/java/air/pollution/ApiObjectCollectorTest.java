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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
    public static void disableLogging() {
        Logger.setGlobalLevel(ErrorLevel.DISABLE);
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
                    key = "PM10";
                    values = new JsonSensorMeasurements.Value[]{
                            new JsonSensorMeasurements.Value() {{
                                date = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
                                value = 1.0f;
                            }},
                            new JsonSensorMeasurements.Value() {{
                                date = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
                                value = 2.0f;
                            }}
                    };
                }},
                new JsonSensorMeasurements() {{
                    key = "PM10";
                    values = new JsonSensorMeasurements.Value[]{
                            new JsonSensorMeasurements.Value() {{
                                date = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
                                value = 10.0f;
                            }},
                            new JsonSensorMeasurements.Value() {{
                                date = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
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
                        Quality.EXCELLENT
                },
                {
                        new JsonAirIndex() {{
                            stIndexLevel = new IndexLevel() {{
                                indexLevelName = "Umiarkowany";
                            }};
                        }},
                        Quality.MODERATE
                },
                {
                        new JsonAirIndex() {{
                            stIndexLevel = new IndexLevel();
                        }},
                        Quality.UNKNOWN
                }
        };
    }

    @Before
    public void setUp() {
        api = new ApiObjectCollector(airPollutionService, jsonObjectFactory);
    }

    @Test
    public void getAllStations_SingleStation_SingleStationFromApi() {
        when(airPollutionService.fetchAllStations()).thenReturn(List.of(new JsonStation()));

        assertEquals(1, api.getAllStations().size());
    }

    // Commented because api now throws System.exit(1) on fetching error

//    @Test
//    public void getAllStations_Null_NoStationsFromApi() {
//        when(airPollutionService.fetchAllStations()).thenReturn(List.of());
//
//        assertNull(api.getAllStations());
//    }

//    @Test
//    public void getAllSensors_Null_NoSensorsFromApi() {
//        when(airPollutionService.fetchAllSensors(anyInt())).thenReturn(List.of());
//
//        assertNull(api.getAllSensors(0));
//    }

    @Test
    @UseDataProvider("dataProviderJsonSensors")
    public void getAllSensors_ThreeSensors_ProvidedSensors(JsonSensor[] jsonSensors) {
        when(airPollutionService.fetchAllSensors(anyInt())).thenReturn(List.of(jsonSensors));

        assertEquals(3, api.getAllSensors(0).size());
    }

    @Test
    public void getSensorData_Null_NoDataFromApi() {
        when(airPollutionService.fetchSensorMeasurements(anyInt())).thenReturn(new JsonSensorMeasurements());

        assertEquals(0, api.getSensorMeasurements(0).size());
    }

    @Test
    @UseDataProvider("dataProviderJsonSensorData")
    public void getSensorData_ThreeMeasurements_ProvidedData(JsonSensorMeasurements jsonSensorData) {

        when(airPollutionService.fetchSensorMeasurements(anyInt())).thenReturn(jsonSensorData);

        assertEquals(2, api.getSensorMeasurements(0).size());
    }

    @Test
    public void getAirIndex_Null_NoDataFromApi() {
        when(airPollutionService.fetchAirIndex(anyInt())).thenReturn(new JsonAirIndex());

        assertEquals(Quality.UNKNOWN, api.getAirIndex(0).getAirQuality());
    }

    @Test
    @UseDataProvider("dataProviderJsonAirIndex")
    public void getAirIndex_ExpectedAirIndex_ProvidedAirIndexes(JsonAirIndex jsonAirIndex, Quality airQuality) {
        when(airPollutionService.fetchAirIndex(anyInt())).thenReturn(jsonAirIndex);

        assertEquals(airQuality, api.getAirIndex(0).getAirQuality());
    }
}
