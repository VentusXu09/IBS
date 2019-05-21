package com.ventus.ibs.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ventus.ibs.entity.Signal;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/20/2019
 */
public class SignalSerialzer implements JsonSerializer<Signal> {
    @Override
    public JsonElement serialize(Signal src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("cellid", src.getCellid());
        jsonObject.addProperty("dbm", src.getDbm());
//        jsonObject.addProperty("isGsm", src.isGsm());
        jsonObject.addProperty("signalNoiseRatio", src.getSignalToNoiseRatio());
        jsonObject.addProperty("evdoEcio", src.getEvdoEcio());
        jsonObject.addProperty("level", src.getLevel());

        return jsonObject;
    }
}
