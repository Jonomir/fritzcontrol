 package dev.romahn.fritzcontrol.client.util;

import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Response;

import java.io.IOException;
import java.util.Objects;

public class CallUtil {

    public static <T> T executeAndCheck(Call<T> call) {
        Objects.requireNonNull(call, "Call can not be null");

        try {
            Response<T> response = call.execute();
            Objects.requireNonNull(response, "Response from " + call.request().url().toString() + " was null");
            if (!response.isSuccessful()) {
                throw new HttpException(response);
            }
            T body = response.body();

            Objects.requireNonNull(body, "Response body was null");
            return body;
        } catch (IOException e) {
            throw new RuntimeException("Error executing call to" + call.request().url().toString(), e);
        }
    }
}
