package dev.romahn.fritzcontrol.api.auth;

import dev.romahn.fritzcontrol.Configuration;
import dev.romahn.fritzcontrol.api.auth.challenge.AuthenticationStrategy;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class AuthenticationInterceptor implements Interceptor {

    private final static String METHOD = "POST";

    private final AuthenticationController authenticationController;

    public AuthenticationInterceptor(final Configuration configuration, final AuthenticationStrategy authenticationStrategy) {
        this.authenticationController = new AuthenticationController(configuration, authenticationStrategy);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (METHOD.equals(request.method())) {

            String sessionId;
            try {
                sessionId = authenticationController.getSessionId();
            } catch (Exception e) {
                throw new IOException("Can not inject session id for authentication", e);
            }

            RequestBody body = request.body();
            if (body instanceof FormBody) {
                FormBody.Builder formBuilder = new FormBody.Builder();
                formBuilder.add("sid", sessionId);

                FormBody formBody = (FormBody) body;
                for (int i = 0; i < formBody.size(); i++) {
                    formBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }

                request = request.newBuilder().method(METHOD, formBuilder.build()).build();
            }
        }

        return chain.proceed(request);
    }
}
