package air.pollution;


class Station {
    int id;
    String stationName;
    float gegrLat;
    float gegrLon;
    City city;
    String addressStreet;

    class City {
        int id;
        String name;
        Commune commune;

        class Commune {
            String communeName;
            String districtName;
            String provinceName;
        }
    }
}
