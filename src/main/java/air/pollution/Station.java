package air.pollution;

class Station {
    private int id;
    private String name;

    Station(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Station(JsonStation station) {
        id = station.id;
        name = station.stationName;
    }

    int getId() {
        return id;
    }

    String getName() {
        return name;
    }
}
