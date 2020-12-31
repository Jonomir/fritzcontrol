package dev.romahn.fritzcontrol.client.api.auth.session;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.auth.session.client.FritzBoxAuthenticationClient;
import dev.romahn.fritzcontrol.client.api.auth.session.client.dto.SessionInfo;
import dev.romahn.fritzcontrol.client.util.CallUtil;
import retrofit2.Retrofit;
import retrofit2.converter.jaxb.JaxbConverterFactory;

public class SessionManager {

    private final FritzControl fritzControl;
    private final FritzBoxAuthenticationClient authenticationClient;

    private String currentSid;

    public SessionManager(FritzControl fritzControl) {
        this.fritzControl = fritzControl;
        this.authenticationClient = new Retrofit.Builder()
                .baseUrl(fritzControl.getUrl())
                .addConverterFactory(JaxbConverterFactory.create())
                .build()
                .create(FritzBoxAuthenticationClient.class);
    }

    public String getSessionId() throws Exception {

        if (sidIsValid(currentSid)) {
            SessionInfo sessionInfo = CallUtil.executeAndCheck(authenticationClient.validateSession(fritzControl.getAuthenticationStrategy().getPath(), currentSid));

            if (!sidIsValid(sessionInfo.getSid())) {
                fetchNewSid(sessionInfo);
            }
        } else {
            SessionInfo sessionInfo = CallUtil.executeAndCheck(authenticationClient.getSessionInfo(fritzControl.getAuthenticationStrategy().getPath()));
            fetchNewSid(sessionInfo);
        }
        return currentSid;
    }


    private void fetchNewSid(SessionInfo sessionInfo) throws Exception {
        String challengeAnswer = fritzControl.getAuthenticationStrategy().createChallengeResponse(sessionInfo.getChallenge(), fritzControl.getPassword());

        SessionInfo newSessionInfo = CallUtil.executeAndCheck(
                authenticationClient.login(fritzControl.getAuthenticationStrategy().getPath(), fritzControl.getUsername(), challengeAnswer));
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
