package com.ventus.ibs.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ventus.ibs.entity.Barometric;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/20/2019
 */
public class BarometricSerializer implements JsonSerializer<Barometric> {
    @Override
    public JsonElement serialize(Barometric src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pressure", src.getPressure());

        return jsonObject;
    }
}
