package org.freefinder.registration;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegistrationModel implements RegistrationContract.Model {

    private RegistrationApi registrationApi;
    private RegistrationContract.RegistrationStatus presenter;

    @Inject
    public RegistrationModel(RegistrationApi registrationApi, RegistrationContract.RegistrationStatus presenter) {
        this.registrationApi = registrationApi;
        this.presenter = presenter;
    }

    @Override
    public void register(String email, String password, String repeatedPassword) {
//        Call call = registrationApi.register(email, password, repeatedPassword);
//
//        call.enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) {
//                presenter.onSuccess();
//            }
//
//            @Override
//            public void onFailure(Call call, Throwable t) {
//                presenter.onFailure();
//            }
//        });
    }
}
