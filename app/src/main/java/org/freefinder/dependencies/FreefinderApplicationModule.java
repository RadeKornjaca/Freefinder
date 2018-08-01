package org.freefinder.dependencies;

import android.app.Application;

import org.freefinder.FreefinderApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FreefinderApplicationModule {
    private final FreefinderApplication freefinderApplication;

    public FreefinderApplicationModule(FreefinderApplication freefinderApplication) {
        this.freefinderApplication = freefinderApplication;
    }

    @Singleton
    @Provides
    public FreefinderApplication providesApplication() {
        return freefinderApplication;
    }
}
