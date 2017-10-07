package org.freefinder.model.deserializers;

import android.os.Parcelable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.freefinder.model.Category;
import org.freefinder.model.Place;

import java.lang.reflect.Type;

/**
 * Created by rade on 3.10.17..
 */

public class ParcelableDeserializer implements JsonDeserializer<Parcelable> {
    @Override
    public Parcelable deserialize(JsonElement json,
                                  Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
        try {
            return context.deserialize(json, Category.class);
        }
        catch (JsonParseException e) {
            return context.deserialize(json, Place.class);
        }
    }
}
