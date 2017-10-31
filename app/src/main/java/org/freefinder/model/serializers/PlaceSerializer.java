package org.freefinder.model.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.freefinder.model.Place;

import java.lang.reflect.Type;

/**
 * Created by rade on 26.10.17..
 */

public class PlaceSerializer implements JsonSerializer<Place> {
    @Override
    public JsonElement serialize(Place src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject placeJson = new JsonObject();
        placeJson.add("place", context.serialize(src));

        return placeJson;
    }
}
