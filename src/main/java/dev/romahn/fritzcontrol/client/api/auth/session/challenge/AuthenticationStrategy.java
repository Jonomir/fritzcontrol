package dev.romahn.fritzcontrol.client.api.auth.session.challenge;

public interface AuthenticationStrategy {

    String getPath();

    String createChallengeResponse(String challenge, String password) throws Exception;
}
