package dev.romahn.fritzcontrol.api.auth.session.challenge;

public interface AuthenticationStrategy {

    String getPath();

    String createChallengeResponse(String challenge, String password) throws Exception;
}
