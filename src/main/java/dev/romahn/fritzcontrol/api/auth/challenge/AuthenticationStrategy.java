package dev.romahn.fritzcontrol.api.auth.challenge;

public interface AuthenticationStrategy {

    String getPath();

    String createChallengeResponse(String challenge, String password) throws Exception;
}
