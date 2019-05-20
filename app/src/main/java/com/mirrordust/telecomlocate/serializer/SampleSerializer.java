package com.mirrordust.telecomlocate.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mirrordust.telecomlocate.entity.BaseStation;
import com.mirrordust.telecomlocate.entity.Sample;

import java.lang.reflect.Type;
import java.util.List;

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
        jsonObject.addProperty("BSnumber", src.getBSList().size());
        jsonObject.add("LatLng", context.serialize(src.getLatLng()));
        jsonObject.add("ConnectedBS", context.serialize(src.getMBS()));
        List<BaseStation> baseStationList = src.getBSList();
        for (int i = 0; i < 7; i++) {
            BaseStation baseStation = BaseStation.emptyInstance();
            if (i < baseStationList.size()) {
                baseStation = baseStationList.get(i);
            }
            if (null == baseStation) {
                baseStation = BaseStation.emptyInstance();
            }
            jsonObject.addProperty("mcc_" + i, baseStation.getMcc());
            jsonObject.addProperty("mnc_" + i, baseStation.getMnc());
            jsonObject.addProperty("cid_" + i, baseStation.getCid());
            jsonObject.addProperty("lac_" + i, baseStation.getLac());
            jsonObject.addProperty("arfcn_" + i, baseStation.getArfcn());
            jsonObject.addProperty("bsic_psc_pci_" + i, baseStation.getBsic_psc_pci());
            jsonObject.addProperty("asuLevel_" + i, baseStation.getAsuLevel());
            jsonObject.addProperty("signalLevel_" + i, baseStation.getSignalLevel());
            jsonObject.addProperty("type_" + i, baseStation.getType());
            jsonObject.addProperty("Dbm_" + i, baseStation.getDbm());

        }
        jsonObject.add("Signal", context.serialize(src.getSignal()));
        jsonObject.add("Geomagnetic", context.serialize(src.getGm()));
        jsonObject.add("Battery", context.serialize(src.getBtry()));
        jsonObject.add("Barometric", context.serialize(src.getBaro()));
        return jsonObject;
    }
}
