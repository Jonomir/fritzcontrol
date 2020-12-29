package dev.romahn.fritzcontrol.api.auth.challenge;

public interface AuthenticationStrategy {

    String getAuthenticationPath();

    String createChallengeResponse(String challenge, String password) throws Exception;
}
