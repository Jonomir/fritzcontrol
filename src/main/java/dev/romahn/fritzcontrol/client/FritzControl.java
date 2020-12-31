package dev.romahn.fritzcontrol.client;

import dev.romahn.fritzcontrol.client.api.FritzBoxClient;
import dev.romahn.fritzcontrol.client.api.auth.AuthenticationInterceptor;
import dev.romahn.fritzcontrol.client.api.auth.session.challenge.AuthenticationStrategy;
import dev.romahn.fritzcontrol.client.api.auth.session.challenge.impl.Md5AuthenticationStrategy;
import dev.romahn.fritzcontrol.client.api.auth.session.challenge.impl.Pbkdf2AuthenticationStrategy;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

import java.util.Objects;

public class FritzControl {

    private String url;
    private String username;
    private String password;

    private AuthenticationStrategy authenticationStrategy;
    private FritzBoxClient client;

    private FritzControl() {
        //private, use builder
    }

    public static class Builder {

        private String url = "http://fritz.box";
        private String username;
        private String password;
        private AuthenticationStrategy authenticationStrategy = new Md5AuthenticationStrategy();

        public FritzControl build() {

            Objects.requireNonNull(url, "url must not be null");
            Objects.requireNonNull(username, "username must not be null");
            Objects.requireNonNull(password, "password must not be null");
            Objects.requireNonNull(authenticationStrategy, "authentication strategy must not be null");


            FritzControl ret = new FritzControl();
            ret.url = url;
            ret.username = username;
            ret.password = password;
            ret.authenticationStrategy = authenticationStrategy;

            OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new AuthenticationInterceptor(ret)).build();
            ret.client = new Retrofit.Builder().client(okHttpClient).baseUrl(url).build().create(FritzBoxClient.class);

            return ret;
        }


        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder authenticationStrategy(String authenticationStrategy) {

            switch (authenticationStrategy) {
                case "MD5":
                    this.authenticationStrategy = new Md5AuthenticationStrategy();
                    break;
                case "PBKDF2":
                    this.authenticationStrategy = new Pbkdf2AuthenticationStrategy();
                    break;
                default:
                    throw new IllegalArgumentException("Invalid Authentication Strategy: " + authenticationStrategy);
            }
            return this;
        }

    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public AuthenticationStrategy getAuthenticationStrategy() {
        return authenticationStrategy;
    }

    public FritzBoxClient getClient() {
        return client;
    }

}
