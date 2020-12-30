package dev.romahn.fritzcontrol.api;

public class Device {

    private String id;
    private String name;
    private Profile currentProfile;

    public Device(String id, String name, Profile currentProfile) {
        this.id = id;
        this.name = name;
        this.currentProfile = currentProfile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentProfile=" + currentProfile +
                '}';
    }
}
