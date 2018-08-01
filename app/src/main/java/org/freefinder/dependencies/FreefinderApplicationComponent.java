package org.freefinder.dependencies;

import android.app.Application;

import org.freefinder.FreefinderApplication;
import org.freefinder.registration.RegistrationActivityComponent;
import org.freefinder.registration.RegistrationActivityModule;

import javax.inject.Singleton;

import dagger.Component;

//@Singleton
@Component(modules = {
        FreefinderApplicationModule.class
    }
)
public interface FreefinderApplicationComponent {

}
