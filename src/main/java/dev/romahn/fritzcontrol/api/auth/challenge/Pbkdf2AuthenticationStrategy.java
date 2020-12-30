package dev.romahn.fritzcontrol.api.auth.challenge;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;


/**
 * Shamelessly stolen from https://avm.de/fileadmin/user_upload/Global/Service/Schnittstellen/AVM_Technical_Note_-_Session_ID_deutsch_Dez2020.pdf
 */
public class Pbkdf2AuthenticationStrategy implements AuthenticationStrategy {

    private final static String AUTHENTICATION_PATH = "/login_sid.lua?version=2";

    @Override
    public String getPath() {
        return AUTHENTICATION_PATH;
    }

    @Override
    public String createChallengeResponse(String challenge, String password) throws GeneralSecurityException {
        String[] challenge_parts = challenge.split("\\$");
        int iter1 = Integer.parseInt(challenge_parts[1]);
        byte[] salt1 = fromHex(challenge_parts[2]);
        int iter2 = Integer.parseInt(challenge_parts[3]);
        byte[] salt2 = fromHex(challenge_parts[4]);
        byte[] hash1 = pbkdf2HmacSha256(password.getBytes(StandardCharsets.UTF_8), salt1, iter1);
        byte[] hash2 = pbkdf2HmacSha256(hash1, salt2, iter2);
        return challenge_parts[4] + "$" + toHex(hash2);
    }

    /**
     * Hex string to bytes
     */
    private byte[] fromHex(String hexString) {
        int len = hexString.length() / 2;
        byte[] ret = new byte[len];
        for (int i = 0; i < len; i++) {
            ret[i] = (byte) Short.parseShort(hexString.substring(i * 2, i *
                    2 + 2), 16);
        }
        return ret;
    }

    /**
     * byte array to hex string
     */
    private String toHex(byte[] bytes) {
        StringBuilder s = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            s.append(String.format("%02x", b));
        }
        return s.toString();
    }

    /**
     * Create a pbkdf2 HMAC by applying the Hmac iter times as specified.
     */
    private byte[] pbkdf2HmacSha256(final byte[] password, final byte[] salt, int iters) throws GeneralSecurityException {
        String alg = "HmacSHA256";
        Mac sha256mac = Mac.getInstance(alg);
        sha256mac.init(new SecretKeySpec(password, alg));
        byte[] ret = new byte[sha256mac.getMacLength()];
        byte[] tmp = new byte[salt.length + 4];
        System.arraycopy(salt, 0, tmp, 0, salt.length);
        tmp[salt.length + 3] = 1;
        for (int i = 0; i < iters; i++) {
            tmp = sha256mac.doFinal(tmp);
            for (int k = 0; k < ret.length; k++) {
                ret[k] ^= tmp[k];
            }
        }
        return ret;
    }

}