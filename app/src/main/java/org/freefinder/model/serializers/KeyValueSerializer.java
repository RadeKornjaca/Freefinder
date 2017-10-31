package org.freefinder.model.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import org.freefinder.model.KeyValue;

import java.lang.reflect.Type;
import java.util.Map;

import io.realm.RealmList;

/**
 * Created by rade on 27.10.17..
 */

public class KeyValueSerializer implements JsonSerializer<RealmList<KeyValue>>,
                                           JsonDeserializer<RealmList<KeyValue>> {
    @Override
    public JsonElement serialize(RealmList<KeyValue> src,
                                 Type typeOfSrc,
                                 JsonSerializationContext context) {

        JsonObject keyValueJson = new JsonObject();

        for(KeyValue keyValue : src) {
            keyValueJson.addProperty(keyValue.getKey(), keyValue.getValue());
        }

        return keyValueJson;
    }

    @Override
    public RealmList<KeyValue> deserialize(JsonElement json,
                                           Type typeOfT,
                                           JsonDeserializationContext context) throws JsonParseException {

        Type type = new TypeToken<Map<String, String>>() {}.getType();
        Map<String, String> data = new Gson().fromJson(json, type);

        RealmList<KeyValue> keyValues = new RealmList<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            KeyValue keyValue = new KeyValue();
            keyValue.setKey(entry.getKey());
            keyValue.setValue(entry.getValue());

            keyValues.add(keyValue);
        }

        return keyValues;
    }
}
