package dev.romahn.fritzcontrol;

public class Configuration {
    private String fritzBoxUrl;
    private String username;
    private String password;

    public Configuration(String fritzBoxUrl, String username, String password) {
        this.fritzBoxUrl = fritzBoxUrl;
        this.username = username;
        this.password = password;
    }

    public String getFritzBoxUrl() {
        return fritzBoxUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
