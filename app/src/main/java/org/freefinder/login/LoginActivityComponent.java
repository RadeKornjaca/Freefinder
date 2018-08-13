package org.freefinder.login;

import org.freefinder.dependencies.HttpComponent;

import dagger.Component;

@LoginScope
@Component(
    modules = {
        LoginActivityModule.class
    },
    dependencies = {
        HttpComponent.class
    }
)
public interface LoginActivityComponent {
    void injectLoginActivity(LoginActivity loginActivity);
}
