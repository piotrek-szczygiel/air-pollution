package air.pollution;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.reactivex.Observable;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.*;
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
                new JsonSensorData() {{
                    values = new JsonSensorData.Value[]{
                            new JsonSensorData.Value() {{
                                date = new Date(0);
                                value = 1.0f;
                            }},
                            new JsonSensorData.Value() {{
                                date = new Date(1);
                                value = 2.0f;
                            }}
                    };
                }},
                new JsonSensorData() {{
                    values = new JsonSensorData.Value[]{
                            new JsonSensorData.Value() {{
                                date = new Date(10);
                                value = 10.0f;
                            }},
                            new JsonSensorData.Value() {{
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
    public void getAllStations_SingleStation_SingleStationFromApi() {
        when(airPollutionService.getAllStations()).thenReturn(Observable.just(
                Collections.singletonList(new JsonStation())
        ));

        assertEquals(1, api.getAllStations().size());
    }

    @Test
    public void getAllStations_Null_NoStationsFromApi() {
        when(airPollutionService.getAllStations()).thenReturn(Observable.just(Collections.emptyList()));

        assertNull(api.getAllStations());
    }

    @Test
    @UseDataProvider("dataProviderJsonStations")
    public void getStation_ExpectedStation_ProvidedStations(JsonStation[] jsonStations, String stationName) {
        when(airPollutionService.getAllStations()).thenReturn(Observable.just(Arrays.asList(jsonStations)));

        assertEquals(stationName, api.getStation(stationName).getName());
    }

    @Test
    @UseDataProvider("dataProviderJsonStations")
    public void getStation_Null_NonExistentStation(JsonStation[] jsonStations, String stationName) {
        when(airPollutionService.getAllStations()).thenReturn(Observable.just(Arrays.asList(jsonStations)));

        assertNull(api.getStation("non-existent-station-name"));
    }

    @Test
    public void getAllSensors_Null_NoSensorsFromApi() {
        when(airPollutionService.getSensors(anyInt())).thenReturn(Observable.just(Collections.emptyList()));

        assertNull(api.getAllSensors(0));
    }

    @Test
    @UseDataProvider("dataProviderJsonSensors")
    public void getAllSensors_ThreeSensors_ProvidedSensors(JsonSensor[] jsonSensors) {
        when(airPollutionService.getSensors(anyInt())).thenReturn(Observable.just(Arrays.asList(jsonSensors)));

        assertEquals(3, api.getAllSensors(0).size());
    }

    @Test
    public void getSensor_Null_NoSensorsFromApi() {
        when(airPollutionService.getSensors(anyInt())).thenReturn(Observable.just(Collections.emptyList()));

        assertNull(api.getSensor(0, Parameter.PM10));
    }

    @Test
    @UseDataProvider("dataProviderJsonSensors")
    public void getSensor_Null_NonExistedSensor(JsonSensor[] jsonSensors) {
        when(airPollutionService.getSensors(anyInt())).thenReturn(Observable.just(Arrays.asList(jsonSensors)));

        assertNull(api.getSensor(0, Parameter.CO));
    }

    @Test
    @UseDataProvider("dataProviderJsonSensors")
    public void getSensor_PM10Sensor_ProvidedSensors(JsonSensor[] jsonSensors) {
        when(airPollutionService.getSensors(anyInt())).thenReturn(Observable.just(Arrays.asList(jsonSensors)));

        assertNotNull(api.getSensor(0, Parameter.PM10));
    }

    @Test
    public void getSensorData_Null_NoDataFromApi() {
        when(airPollutionService.getSensorData(anyInt())).thenReturn(Observable.just(new JsonSensorData()));

        assertEquals(0, api.getSensorData(0).size());
    }

    @Test
    @UseDataProvider("dataProviderJsonSensorData")
    public void getSensorData_ThreeMeasurements_ProvidedData(JsonSensorData jsonSensorData) {
        when(airPollutionService.getSensorData(anyInt())).thenReturn(Observable.just(jsonSensorData));

        assertEquals(2, api.getSensorData(0).size());
    }

    @Test
    public void getAirIndex_Null_NoDataFromApi() {
        when(airPollutionService.getAirIndex(anyInt())).thenReturn(Observable.just(new JsonAirIndex()));

        assertEquals("-", api.getAirIndex(0).getAirQuality());
    }

    @Test
    @UseDataProvider("dataProviderJsonAirIndex")
    public void getAirIndex_ExpectedAirIndex_ProvidedAirIndexes(JsonAirIndex jsonAirIndex, String airQuality) {
        when(airPollutionService.getAirIndex(anyInt())).thenReturn(Observable.just(jsonAirIndex));

        assertEquals(airQuality, api.getAirIndex(0).getAirQuality());
    }
}
