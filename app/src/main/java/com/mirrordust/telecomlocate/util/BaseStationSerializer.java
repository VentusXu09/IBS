package com.mirrordust.telecomlocate.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mirrordust.telecomlocate.entity.BaseStation;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/08/2019
 */
public class BaseStationSerializer implements JsonSerializer<BaseStation> {
    @Override
    public JsonElement serialize(BaseStation src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mcc", src.getMcc());
        jsonObject.addProperty("mnc", src.getMnc());
        jsonObject.addProperty("cid", src.getCid());
        jsonObject.addProperty("lac", src.getLac());
        jsonObject.addProperty("type", src.getType());
        jsonObject.addProperty("Dbm", src.getDbm());

        return jsonObject;
    }
}
