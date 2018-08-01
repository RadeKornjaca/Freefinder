package org.freefinder.dependencies;


import javax.inject.Singleton;

import dagger.Component;
import retrofit2.Retrofit;

@Singleton
@Component(modules = { HttpModule.class })
public interface HttpComponent {
    Retrofit getRetrofit();
}
