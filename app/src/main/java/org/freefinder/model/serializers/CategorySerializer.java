package org.freefinder.model.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.freefinder.model.AdditionalField;
import org.freefinder.model.Category;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by rade on 25.10.17..
 */

public class CategorySerializer implements JsonSerializer<Category> {
    @Override
    public JsonElement serialize(Category src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject categoryJson = new JsonObject();
        JsonObject parametersJson = new JsonObject();

        JsonArray additionalFieldsJson = new JsonArray();
        for(AdditionalField additionalField : src.getAdditionalFields()) {
            additionalFieldsJson.add(context.serialize(additionalField));
        }

        parametersJson.addProperty("name", src.getName());
        parametersJson.addProperty("parent_category_id", src.getParentCategory().getId());
        parametersJson.add("additional_fields_attributes", additionalFieldsJson);

        categoryJson.add("category", parametersJson);

        return categoryJson;
    }
}
