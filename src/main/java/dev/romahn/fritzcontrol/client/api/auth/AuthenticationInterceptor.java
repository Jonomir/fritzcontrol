package dev.romahn.fritzcontrol.client.api.auth;

import dev.romahn.fritzcontrol.client.FritzControl;
import dev.romahn.fritzcontrol.client.api.auth.session.SessionManager;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class AuthenticationInterceptor implements Interceptor {

    private final static String METHOD = "POST";

    private final SessionManager sessionManager;

    public AuthenticationInterceptor(FritzControl fritzControl) {
        this.sessionManager = new SessionManager(fritzControl);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        if (METHOD.equals(request.method())) {

            String sessionId;
            try {
                sessionId = sessionManager.getSessionId();
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
