package org.freefinder;


import android.app.Activity;
import android.app.Application;

import org.freefinder.dependencies.DaggerHttpComponent;
import org.freefinder.dependencies.HttpComponent;
import org.freefinder.dependencies.HttpModule;


public class FreefinderApplication extends Application {

    private HttpComponent httpComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        httpComponent = DaggerHttpComponent.builder()
                            .httpModule(new HttpModule(BuildConfig.API_URL))
                            .build();
    }

    public static FreefinderApplication get(Activity activity) {
        return (FreefinderApplication) activity.getApplication();
    }

    public HttpComponent getHttpComponent() {
        return httpComponent;
    }
}
