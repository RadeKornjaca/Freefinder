package org.freefinder.registration;

import dagger.Module;
import dagger.Provides;

import retrofit2.Retrofit;

@Module
public class RegistrationActivityModule {

    private final RegistrationActivity registrationActivity;

    public RegistrationActivityModule(RegistrationActivity registrationActivity) {
        this.registrationActivity = registrationActivity;
    }

    @RegistrationScope
    @Provides
    RegistrationContract.UserActionsListener provideRegistrationPresenter(RegistrationApi registrationApi) {
        return new RegistrationPresenter(registrationActivity, registrationApi);
    }
//
//    @RegistrationScope
//    @Provides
//    RegistrationModel provideRegistrationModel(RegistrationPresenter registrationPresenter,
//                                               RegistrationApi registrationApi) {
//        return new RegistrationModel(registrationApi, registrationPresenter);
//    }

    @RegistrationScope
    @Provides
    RegistrationApi provideRegistrationApi(Retrofit retrofit) {
        return retrofit.create(RegistrationApi.class);
    }
}
