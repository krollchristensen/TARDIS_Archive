package dk.zealand.model;

public class Doctor {

    private int id;
    private String name;
    private String actor;

    public Doctor(int id, String name, String actor) {
        this.id = id;
        this.name = name;
        this.actor = actor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getActor() {
        return actor;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    @Override
    public String toString() {
        return id + " - " + name + " (" + actor + ")";
    }
}