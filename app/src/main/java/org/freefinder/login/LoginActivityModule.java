package org.freefinder.login;

import org.freefinder.registration.RegistrationScope;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

@Module
public class LoginActivityModule {
    private final LoginActivity loginActivity;

    public LoginActivityModule(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @LoginScope
    @Provides
    LoginContract.UserActionsListener provideLoginPresenter(LoginApi loginApi) {
        return new LoginPresenter(loginActivity, loginApi);
    }

    @LoginScope
    @Provides
    LoginApi provideLoginApi(Retrofit retrofit) {
        return retrofit.create(LoginApi.class);
    }
}
