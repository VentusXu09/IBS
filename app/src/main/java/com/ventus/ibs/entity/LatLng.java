package com.ventus.ibs.entity;

import io.realm.RealmObject;

/**
 * Created by LiaoShanhe on 2017/07/26/026.
 */

public class LatLng extends RealmObject {
    private double longitude;
    private double latitude;
    private double altitude;
    private float accuracy;
    private float speed;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public static LatLng createEmptyLatLng() {
        LatLng emptyLatLng = new LatLng();
        emptyLatLng.setLatitude(0);
        emptyLatLng.setLongitude(0);
        emptyLatLng.setAltitude(0);
        emptyLatLng.setAccuracy(0);
        emptyLatLng.setSpeed(0);
        return emptyLatLng;
    }

    public boolean isEmpty() {
        return this.latitude == 0
                && this.altitude == 0;
    }

    @Override
    public String toString() {
        return "LatLng{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", altitude=" + altitude +
                ", accuracy=" + accuracy +
                ", speed=" + speed +
                '}';
    }
}
