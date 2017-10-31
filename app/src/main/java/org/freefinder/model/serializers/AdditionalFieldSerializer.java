package org.freefinder.model.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.freefinder.model.AdditionalField;

import java.lang.reflect.Type;

/**
 * Created by rade on 25.10.17..
 */

public class AdditionalFieldSerializer implements JsonSerializer<AdditionalField> {
    @Override
    public JsonElement serialize(AdditionalField src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject additionalFieldJson = new JsonObject();

        additionalFieldJson.addProperty("name", src.getName());
        additionalFieldJson.addProperty("field_type", src.getFieldType());

        return additionalFieldJson;
    }
}
