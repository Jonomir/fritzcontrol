package dev.romahn.fritzcontrol.api.auth.session.challenge.impl;

import dev.romahn.fritzcontrol.api.auth.session.challenge.AuthenticationStrategy;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;

public class Md5AuthenticationStrategy implements AuthenticationStrategy {

    private final static String AUTHENTICATION_PATH = "/login_sid.lua";

    @Override
    public String getPath() {
        return AUTHENTICATION_PATH;
    }

    @Override
    public String createChallengeResponse(String challenge, String password) {
        String response = challenge + "-" + password;
        byte[] encoded = StringUtils.getBytesUtf16Le(response);
        return challenge + "-" + DigestUtils.md5Hex(encoded);
    }

}