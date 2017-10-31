package org.freefinder.model.serializers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import org.freefinder.model.KeyValue;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;

import io.realm.Realm;
import io.realm.RealmList;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by rade on 27.10.17..
 */

public class KeyValueRealmListSerializerUnitTest {
    private Gson gson;
    private Type keyValueRealmListType;
    private RealmList<KeyValue> keyValues;

    @Before
    public void setup() {
        keyValueRealmListType = new TypeToken<RealmList<KeyValue>>() {}.getType();
        gson = new GsonBuilder().registerTypeAdapter(
                keyValueRealmListType,
                new KeyValueSerializer())
        .registerTypeAdapter(keyValueRealmListType, new TypeAdapter<RealmList<KeyValue>>() {
            @Override
            public void write(JsonWriter out, RealmList<KeyValue> value) throws IOException {

            }

            @Override
            public RealmList<KeyValue> read(JsonReader in) throws IOException {
                RealmList<KeyValue> keyValues = new RealmList<>();

                in.beginObject();

                while(in.hasNext()) {
                    KeyValue keyValue = new KeyValue();

                    keyValue.setKey(in.nextString());
                    keyValue.setValue(in.nextString());

                    keyValues.add(keyValue);
                }

                in.endObject();

                return keyValues;
            }
        }).create();

        keyValues = new RealmList<>();

        for(int i = 1; i < 5;i++) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey("key" + i);
            keyValue.setValue("value" + i);

            keyValues.add(keyValue);
        }
    }

    @Test
    public void jsonSerialization_isCorrect() throws Exception {
        final String expected = "{\"key1\":\"value1\","
                               + "\"key2\":\"value2\","
                               + "\"key3\":\"value3\","
                               + "\"key4\":\"value4\"}";

        String keyValuesJsonString = gson.toJson(keyValues, keyValueRealmListType);

        assertEquals(keyValuesJsonString, expected);
    }
}
