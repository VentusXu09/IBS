package com.ventus.ibs.model.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ventus.ibs.entity.Wifi;

import java.lang.reflect.Type;

/**
 * mgdp
 * Created by xuxiaofeng on 2019/7/7 3:01 PM
 */

public class WifiSerializer implements JsonSerializer<Wifi> {
    @Override
    public JsonElement serialize(Wifi src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bssid", src.getBssid());
        jsonObject.addProperty("ssid", src.getSsid());
        jsonObject.addProperty("strength", src.getStrength());

        return jsonObject;
    }
}
