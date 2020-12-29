package dev.romahn.fritzcontrol;

public class Configuration {
    private String fritzBoxUrl;
    private String username;
    private String password;

    public void setFritzBoxUrl(String fritzBoxUrl) {
        this.fritzBoxUrl = fritzBoxUrl;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
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
