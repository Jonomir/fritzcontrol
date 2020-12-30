package dev.romahn.fritzcontrol.api.device.dto;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.List;

public class Device {

    private final String id;
    private final String name;
    private Profile currentProfile;
    private List<Profile> profiles;

    public Device(String id, String name, Profile currentProfile, List<Profile> profiles) {
        this.id = id;
        this.name = name;
        this.currentProfile = currentProfile;
        this.profiles = profiles;
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

    public List<Profile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public void setCurrentProfile(Profile currentProfile) {
        if (!profiles.contains(currentProfile)) {
            throw new InvalidParameterException("Profile must be part of this devices profiles");
        }

        this.currentProfile = currentProfile;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", profile=" + currentProfile +
                ", profiles=" + profiles +
                '}';
    }
}
