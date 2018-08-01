package org.freefinder.registration;

import org.freefinder.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RegistrationApi {

    @POST("users")
    Call<ResponseBody> register(@Body User user);
}
