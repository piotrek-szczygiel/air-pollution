package air.pollution;

class Station {
    private int id;
    private String name;

    int getId() {
        return id;
    }

    String getIdColored() {
        return Format.stationId(id);
    }

    void setId(int id) {
        this.id = id;
    }

    String getName() {
        return name;
    }

    String getNameColored() {
        return Format.stationName(name);
    }

    void setName(String name) {
        this.name = name;
    }
}
