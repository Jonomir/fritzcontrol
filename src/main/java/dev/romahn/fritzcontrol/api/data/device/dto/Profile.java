package dev.romahn.fritzcontrol.api.data.device.dto;

public class Profile {

    private final String id;
    private final String name;

    public Profile(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
