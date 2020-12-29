package dev.romahn.fritzcontrol.api.auth;

import dev.romahn.fritzcontrol.Configuration;
import dev.romahn.fritzcontrol.api.auth.api.SessionInfo;
import dev.romahn.fritzcontrol.api.auth.challenge.AuthenticationStrategy;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

import java.util.Objects;

public class AuthenticationManager {

    private final Configuration configuration;
    private final AuthenticationStrategy authenticationStrategy;
    private final FritzBoxAuthenticationClient authenticationClient;

    private String currentSid;

    public AuthenticationManager(Configuration configuration, AuthenticationStrategy authenticationStrategy) {
        this.configuration = configuration;
        this.authenticationStrategy = authenticationStrategy;
        this.authenticationClient = new Retrofit.Builder()
                .baseUrl(configuration.getFritzBoxUrl())
                .addConverterFactory(JaxbConverterFactory.create())
                .build()
                .create(FritzBoxAuthenticationClient.class);
    }

    public String getSessionId() throws Exception {

        if (sidIsValid(currentSid)) {
            Response<SessionInfo> response = authenticationClient.validateSession(authenticationStrategy.getAuthenticationPath(), currentSid).execute();
            checkResponse(response);

            SessionInfo sessionInfo = response.body();
            if (!sidIsValid(sessionInfo.getSid())) {
                fetchNewSid(sessionInfo);
            }
        } else {
            Response<SessionInfo> response = authenticationClient.getSessionInfo(authenticationStrategy.getAuthenticationPath()).execute();
            checkResponse(response);
            fetchNewSid(response.body());
        }
        return currentSid;
    }


    private void fetchNewSid(SessionInfo sessionInfo) throws Exception {
        String challengeAnswer = authenticationStrategy.createChallengeResponse(sessionInfo.getChallenge(), configuration.getPassword());

        Response<SessionInfo> response = authenticationClient.login(authenticationStrategy.getAuthenticationPath(), configuration.getUsername(), challengeAnswer).execute();
        checkResponse(response);

        String newSid = response.body().getSid();

        if (sidIsValid(newSid)) {
            currentSid = newSid;
        } else {
            throw new Exception("Could not get valid session ID");
        }
    }

    private void checkResponse(Response<SessionInfo> response) {
        Objects.requireNonNull(response, "Response was null");
        if (!response.isSuccessful()) {
            throw new HttpException(response);
        }
        SessionInfo sessionInfo = response.body();
        Objects.requireNonNull(sessionInfo, "Response SessionInfo was null");
        Objects.requireNonNull(sessionInfo.getSid(), "Response SessionInfo SessionID was null");
        Objects.requireNonNull(sessionInfo.getChallenge(), "Response SessionInfo Challenge was null");
    }


    private boolean sidIsValid(final String sid) {
        if (sid == null || sid.isBlank()) {
            return false;
        }
        try {
            if (Integer.parseInt(sid) == 0) {
                return false;
            }
        } catch (NumberFormatException e) {
            //NOP
        }
        return true;
    }
}
