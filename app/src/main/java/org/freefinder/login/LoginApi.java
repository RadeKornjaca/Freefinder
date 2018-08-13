package org.freefinder.login;

import org.freefinder.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApi {

    @POST("device_sign_in")
    Call<ResponseBody> login(@Body User user);
}
