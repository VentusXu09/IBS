package com.mirrordust.telecomlocate.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mirrordust.telecomlocate.entity.Geomagnetism;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/20/2019
 */
public class GeomagnetismSerializer implements JsonSerializer<Geomagnetism> {
    @Override
    public JsonElement serialize(Geomagnetism src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", src.getX());
        jsonObject.addProperty("Y", src.getY());
        jsonObject.addProperty("Z", src.getZ());
        jsonObject.addProperty("alpha", src.getAlpha());
        jsonObject.addProperty("beta", src.getBeta());
        jsonObject.addProperty("magneticIntensity", src.getMagneticIntensity());

        return jsonObject;
    }
}
