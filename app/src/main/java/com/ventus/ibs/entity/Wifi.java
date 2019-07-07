package com.ventus.ibs.entity;

import io.realm.RealmObject;

/**
 * mgdp
 * Created by xuxiaofeng on 2019/7/7 1:29 PM
 */

public class Wifi extends RealmObject {
    private int strength;

    private String ssid;

    private String bssid;

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public Wifi() {

    }

    public Wifi(String ssid, String bssid, int strength) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.strength = strength;
    }

    @Override
    public String toString() {
        return "Wifi{" +
                "bssid:" + bssid +
                "ssid" + ssid +
                "level:" + strength
                + "}";
    }
}
