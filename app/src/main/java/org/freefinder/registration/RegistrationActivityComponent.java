package org.freefinder.registration;

import org.freefinder.dependencies.FreefinderApplicationComponent;
import org.freefinder.dependencies.HttpComponent;

import dagger.Component;

@RegistrationScope
@Component(
    modules = {
        RegistrationActivityModule.class
    },
    dependencies = {
            HttpComponent.class
    }
)
public interface RegistrationActivityComponent {
    void injectRegistrationActivity(RegistrationActivity registrationActivity);
}
