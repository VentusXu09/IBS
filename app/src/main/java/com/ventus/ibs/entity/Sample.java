package com.ventus.ibs.entity;

import java.util.UUID;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by LiaoShanhe on 2017/07/18/018.
 */

public class Sample extends RealmObject {
    @PrimaryKey
    private String mID;

    private long index;                     // used to distinguish which data set sample belongs to

    /* Basic attributes */

    // timestamp in milliseconds
    private long time;

    // location
    private LatLng latLng;

    // motion modes
    private String mode;

    //floor number start from 1
    private int floor;

    /* Measurements */

    private RealmList<BaseStation> BSList;  // base stations list, get from #getAllCellInfo()

    private BaseStation MBS;                // connected base station

    private Signal signal;                  // mobile signal strength,
                                            // get from #PhoneStateListener.LISTEN_SIGNAL_STRENGTHS

    private Battery btry;                   // battery

    private Geomagnetism gm;                // geomagnetic measurements

    private Barometric baro;                // barometric pressure

    private RealmList<Wifi> wifiList;       // wifi list get from WifiManager.getScanResult()

    public Sample() {
        mID = UUID.randomUUID().toString();
        time = System.currentTimeMillis();
    }

    // Getter and Setter for all fields
    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getFloor() {
        return floor;
    }

    public RealmList<BaseStation> getBSList() {
        return BSList;
    }

    public void setBSList(RealmList<BaseStation> BSList) {
        this.BSList = BSList;
    }

    public BaseStation getMBS() {
        return MBS;
    }

    public void setMBS() {
        if (null == BSList || BSList.size() == 0) {
            this.MBS = BaseStation.emptyInstance();
            return;
        }
        this.MBS = BSList.first();
    }

    public Signal getSignal() {
        return signal;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
    }

    public Battery getBtry() {
        return btry;
    }

    public void setBtry(Battery btry) {
        this.btry = btry;
    }

    public Geomagnetism getGm() {
        return gm;
    }

    public void setGm(Geomagnetism gm) {
        this.gm = gm;
    }

    public Barometric getBaro() {
        return baro;
    }

    public void setBaro(Barometric baro) {
        this.baro = baro;
    }

    public RealmList<Wifi> getWifiList() {
        return wifiList;
    }

    public void setWifiList(RealmList<Wifi> wifiList) {
        this.wifiList = wifiList;
    }

    @Override
    public String toString() {
        return "Sample{" +
                "mID='" + mID + '\'' +
                ", index=" + index +
                ", time=" + time +
                ", latLng=" + latLng +
                ", mode='" + mode + '\'' +
                ", BSList=" + BSList +
                ", MBS=" + MBS +
                ", signal=" + signal +
                ", btry=" + btry +
                ", gm=" + gm +
                ", baro=" + baro +
                '}';
    }
}
