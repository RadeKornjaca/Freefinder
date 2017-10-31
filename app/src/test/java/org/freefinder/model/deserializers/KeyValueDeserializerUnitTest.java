package org.freefinder.model.deserializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.freefinder.model.KeyValue;
import org.freefinder.model.serializers.KeyValueSerializer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.realm.RealmList;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * Created by rade on 27.10.17..
 */

public class KeyValueDeserializerUnitTest {

    private Gson       gson;
    private JsonObject keyValuesJson;
    private Type       keyValueRealmListType;

    @Before
    public void setup() {
        keyValueRealmListType = new TypeToken<RealmList<KeyValue>>() {}.getType();
        gson = new GsonBuilder().registerTypeAdapter(keyValueRealmListType,
                                                     new KeyValueSerializer())
                   .create();

        keyValuesJson = new JsonObject();

        for(int i = 1;i < 5;i++) {
            keyValuesJson.addProperty("key" + i, "value" + i);
        }
    }

    @Test
    public void jsonDeserialization_isCorrect() throws Exception {
        RealmList<KeyValue> expectedKeyValues = new RealmList<>();

        for(int i = 1; i < 5;i++) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("key" + i);
            keyValue.setValue("value" + i);

            expectedKeyValues.add(keyValue);
        }

        RealmList<KeyValue> keyValues = gson.fromJson(keyValuesJson,
                                                      keyValueRealmListType);

        ArrayList<KeyValue> expectedKeyValueArrayList = new ArrayList<>();
        expectedKeyValueArrayList.addAll(keyValues);
        ArrayList<KeyValue> keyValueArrayList = new ArrayList<>();
        keyValueArrayList.addAll(keyValues);

        assertThat(keyValueArrayList, is(equalTo(expectedKeyValueArrayList)));
    }
}
