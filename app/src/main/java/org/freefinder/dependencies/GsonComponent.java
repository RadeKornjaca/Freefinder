package org.freefinder.dependencies;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by rade on 22.11.17..
 */
@Singleton
@Component(modules = { GsonModule.class })
public interface GsonComponent {
    Gson provideGson();
}
