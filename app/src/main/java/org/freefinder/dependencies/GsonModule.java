package org.freefinder.dependencies;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.freefinder.model.KeyValue;
import org.freefinder.model.serializers.KeyValueSerializer;

import java.lang.reflect.Type;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.RealmList;

/**
 * Created by rade on 21.11.17..
 */

@Module
public class GsonModule {

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
}
