package dev.romahn.fritzcontrol.api.auth;

import dev.romahn.fritzcontrol.Configuration;
import dev.romahn.fritzcontrol.api.CallUtil;
import dev.romahn.fritzcontrol.api.auth.api.SessionInfo;
import dev.romahn.fritzcontrol.api.auth.challenge.AuthenticationStrategy;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

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
            SessionInfo sessionInfo = CallUtil.executeAndCheck(authenticationClient.validateSession(authenticationStrategy.getPath(), currentSid));

            if (!sidIsValid(sessionInfo.getSid())) {
                fetchNewSid(sessionInfo);
            }
        } else {
            SessionInfo sessionInfo = CallUtil.executeAndCheck(authenticationClient.getSessionInfo(authenticationStrategy.getPath()));
            fetchNewSid(sessionInfo);
        }
        return currentSid;
    }


    private void fetchNewSid(SessionInfo sessionInfo) throws Exception {
        String challengeAnswer = authenticationStrategy.createChallengeResponse(sessionInfo.getChallenge(), configuration.getPassword());

        SessionInfo newSessionInfo = CallUtil.executeAndCheck(authenticationClient.login(authenticationStrategy.getPath(), configuration.getUsername(), challengeAnswer));
        String newSid = newSessionInfo.getSid();

        if (sidIsValid(newSid)) {
            currentSid = newSid;
        } else {
            throw new Exception("Could not get valid session ID");
        }
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
