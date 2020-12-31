package dev.romahn.fritzcontrol.client.api.auth.session.client;

import dev.romahn.fritzcontrol.client.api.auth.session.client.dto.SessionInfo;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface FritzBoxAuthenticationClient {
    @GET
    Call<SessionInfo> getSessionInfo(@Url String path);

    @POST
    @FormUrlEncoded
    Call<SessionInfo> login(@Url String path, @Field("username") String username, @Field("response") String response);

    @POST
    @FormUrlEncoded
    Call<SessionInfo> validateSession(@Url String path, @Field("sid") String sid);
}

