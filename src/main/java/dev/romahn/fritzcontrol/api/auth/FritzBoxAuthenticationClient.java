package dev.romahn.fritzcontrol.api.auth;

import dev.romahn.fritzcontrol.api.auth.api.SessionInfo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface FritzBoxAuthenticationClient {
    @GET("{path}")
    Call<SessionInfo> getSessionInfo(@Path("path") String path);

    @POST("{path}")
    @FormUrlEncoded
    Call<SessionInfo> login(@Path("path") String path, @Field("username") String username, @Field("response") String response);

    @POST("{path}")
    @FormUrlEncoded
    Call<SessionInfo> validateSession(@Path("path") String path, @Field("sid") String sid);
}

