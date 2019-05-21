package com.ventus.ibs.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ventus.ibs.entity.Battery;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/20/2019
 */
public class BatterySerializer implements JsonSerializer<Battery> {
    @Override
    public JsonElement serialize(Battery src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("level", src.getLevel());
        jsonObject.addProperty("capacity", src.getCapacity());

        return jsonObject;
    }
}
