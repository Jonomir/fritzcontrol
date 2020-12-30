package dev.romahn.fritzcontrol.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.util.Map;

public interface FritzBoxClient {
    @POST("/data.lua")
    @FormUrlEncoded
    Call<ResponseBody> getData(@Field("page") String page);

    @POST("/data.lua")
    @FormUrlEncoded
    Call<ResponseBody> sendData(@FieldMap Map<String, String> devices);
}
