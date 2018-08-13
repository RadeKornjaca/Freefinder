package org.freefinder.login;

import com.google.gson.Gson;

import org.freefinder.model.User;
import org.freefinder.shared.SharedPreferencesHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.UserActionsListener,
                                       LoginContract.LoginStatus {

    private static final String ACCESS_TOKEN = "access_token";

    private final LoginContract.View loginActivity;
    private final LoginApi loginApi;

    public LoginPresenter(LoginContract.View loginActivity, LoginApi loginApi) {
        this.loginActivity = loginActivity;
        this.loginApi = loginApi;
    }

    @Override
    public void submitLogin(String email, String password) {
        final User user = new User(email, password, null);  // no need for password confirmation, won't be serialized anyway
        Call call = loginApi.login(user);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // prefer JSONObject over Gson here, because it only needs one property from response
                if(response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        final String authorizationToken = jsonResponse.getString(ACCESS_TOKEN);
                        LoginPresenter.this.onSuccess(authorizationToken);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    LoginPresenter.this.onFailure();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                LoginPresenter.this.onFailure();
            }

        });
    }

    @Override
    public void onSuccess(String authorizationToken) {
        SharedPreferencesHelper.setAuthorizationToken((LoginActivity)loginActivity, authorizationToken);
        loginActivity.successfulLoginRedirection();
        loginActivity.showProgress(false);
    }

    @Override
    public void onFailure() {
        loginActivity.showLoginError();
        loginActivity.showProgress(false);
    }

    public void onServerCommunicationFailure() {
        loginActivity.displayServerErrorMessage();
    }
}
