package dev.romahn.fritzcontrol.api;

public class Device {

    private String id;
    private String name;
    private String profile;

    public Device(String id, String name, String profile) {
        this.id = id;
        this.name = name;
        this.profile = profile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }
}
