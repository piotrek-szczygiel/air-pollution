package air.pollution;

class Station {
    private int id;
    private String name;

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }

    String getIdColored() {
        return Format.stationId(id);
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getNameColored() {
        return Format.stationName(name);
    }
}
