package org.freefinder.dependencies;

import android.support.test.espresso.IdlingRegistry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jakewharton.espresso.OkHttp3IdlingResource;

import org.freefinder.BuildConfig;
import org.freefinder.model.KeyValue;
import org.freefinder.model.serializers.KeyValueSerializer;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmList;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class HttpModule {

    private String baseUrl;

    public HttpModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Provides
    @Singleton
    public Gson createGson() {
        Type keyValueRealmListType = new TypeToken<RealmList<KeyValue>>() {}.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        gsonBuilder.registerTypeAdapter(keyValueRealmListType,
                new KeyValueSerializer());

        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addNetworkInterceptor(httpLoggingInterceptor);

        OkHttpClient okHttpClient = okHttpClientBuilder.build();

        if(BuildConfig.DEBUG) {
            IdlingRegistry.getInstance().register(OkHttp3IdlingResource.create("okhttp", okHttpClient));
        }
        return okHttpClient;
    }

    @Provides
    @Singleton
    Retrofit createRetrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                           .addConverterFactory(GsonConverterFactory.create(gson))
                           .client(okHttpClient)
                           .baseUrl(baseUrl)
                           .build();
    }
}
