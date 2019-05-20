package com.mirrordust.telecomlocate.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mirrordust.telecomlocate.entity.Sample;

import java.lang.reflect.Type;

/**
 * Created by ventus0905 on 05/08/2019
 */
public class SampleSerializer implements JsonSerializer<Sample> {
    @Override
    public JsonElement serialize(Sample src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mID", src.getmID());
        jsonObject.addProperty("index", src.getIndex());
        jsonObject.addProperty("time", src.getTime());
        jsonObject.addProperty("mode", src.getMode());
        jsonObject.addProperty("floor", src.getFloor());
        jsonObject.add("LatLng", context.serialize(src.getLatLng()));
        jsonObject.add("ConnectedBS", context.serialize(src.getMBS()));
        jsonObject.add("BaseStationList", context.serialize(src.getBSList()));
        jsonObject.add("Geomagnetic", context.serialize(src.getGm()));
        jsonObject.add("Battery", context.serialize(src.getBtry()));
        jsonObject.add("Barometric", context.serialize(src.getBaro()));
        return jsonObject;
    }
}
