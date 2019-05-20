package com.mirrordust.telecomlocate.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mirrordust.telecomlocate.entity.LatLng;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/20/2019
 */
public class LatLngSerializer implements JsonSerializer<LatLng> {
    @Override
    public JsonElement serialize(LatLng src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("longitude", src.getLongitude());
        jsonObject.addProperty("latitude", src.getLatitude());
        jsonObject.addProperty("altitude", src.getAltitude());
        jsonObject.addProperty("accuracy", src.getAccuracy());
        jsonObject.addProperty("speed", src.getSpeed());

        return jsonObject;
    }
}
