package org.freefinder.registration;

import org.freefinder.model.User;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by rade on 13.12.17..
 */

public class RegistrationPresenter implements RegistrationContract.UserActionsListener,
                                              RegistrationContract.RegistrationStatus {

    private RegistrationContract.View registrationActivity;
//    private RegistrationContract.Model registrationModel;
    private RegistrationApi registrationApi;

    public RegistrationPresenter(RegistrationContract.View registrationActivity, RegistrationApi registrationApi) {
        this.registrationActivity = registrationActivity;
        this.registrationApi = registrationApi;
    }

    @Override
    public void submitRegistration(String email, String password, String repeatedPassword) {
        User user = new User(email, password, repeatedPassword);
        Call call = registrationApi.register(user);

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                RegistrationPresenter.this.onSuccess();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                RegistrationPresenter.this.onFailure();
            }
        });
    }

    @Override
    public void onSuccess() {
        registrationActivity.successfulRegistrationRedirection();
        registrationActivity.showProgress(false);
    }

    @Override
    public void onFailure() {
        registrationActivity.showRegistrationError();
        registrationActivity.showProgress(false);
    }
}
