package com.ventus.ibs.model;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.ventus.ibs.entity.*;

import java.util.List;

import io.realm.RealmList;
import rx.Observable;
import rx.functions.Func4;
import rx.functions.Func6;
import rx.functions.Func7;

/**
 * Created by LiaoShanhe on 2017/07/18/018.
 */

public class SampleManager {
    private static final String TAG = "SampleManager";

    private LocationManager mLocationManager;
    private SignalManager mSignalManager;
    private BaseStationManager mBaseStationManager;
    private BatteryManager mBatteryManager;
    private GeomagneticManager mGeomagneticManager;
    private BarometricManager mBarometricManager;
    private WifiManager mWifiManager;
    private Context mContext;

    public SampleManager(Context context) {
        mContext = context;

        mLocationManager = new LocationManager(mContext);
        mSignalManager = new SignalManager(mContext);
        mBaseStationManager = new BaseStationManager(mContext);
        mBatteryManager = new BatteryManager(mContext);
        mGeomagneticManager = new GeomagneticManager(mContext);
        mBarometricManager = new BarometricManager(mContext);
        mWifiManager = new WifiManager(mContext);
    }

    public Observable<Sample> fetchRecord() {
        return Observable.combineLatest(mSignalManager.observeOnce(),
                mLocationManager.getLocation(),
                mBaseStationManager.nerbyTower(),
                mBatteryManager.getBatteryLevel(),
                mGeomagneticManager.getGeomagneticInfo(),
                mBarometricManager.getBarometerPressure(),
                mWifiManager.getWifiResults(),
                new Func7<Signal, Location, List<BaseStation>, Battery, Geomagnetism, Barometric, List<Wifi>, Sample>() {
                    @Override
                    public Sample call(Signal signalRecord, Location location, List<BaseStation> cellularTowers, Battery battery, Geomagnetism geomagnetic, Barometric barometric, List<Wifi> wifiList) {
                        Log.v(TAG, "start creating record...");
                        Sample record = new Sample();
                        if (location != null) {
                            Log.v(TAG, "Location user : " + location.getLatitude() + " : " + location.getLongitude());
                            LatLng latLng = new LatLng();
                            latLng.setLatitude(location.getLatitude());
                            latLng.setLongitude(location.getLongitude());
                            latLng.setAltitude(location.getAltitude());
                            latLng.setAccuracy(location.getAccuracy());
                            latLng.setSpeed(location.getSpeed());
                            record.setLatLng(latLng);
                        } else {
                            record.setLatLng(null);
                        }
                        RealmList<BaseStation> cellularTowerRealmList = new RealmList<>();
                        if (cellularTowers != null && cellularTowers.size() > 0) {
                            for (BaseStation tower : cellularTowers) {
                                cellularTowerRealmList.add(tower);
                            }
                        }
                        record.setBSList(cellularTowerRealmList);
                        record.setBtry(battery);
                        record.setSignal(signalRecord);
                        record.setMBS();
                        record.setGm(geomagnetic);
                        record.setBaro(barometric);
                        record.setWifiList(WifiManager.list2RealmList(wifiList));

                        return record;
                    }

                });
    }

    public Observable<Sample> fetchPredictRecord() {
        return Observable.combineLatest(mSignalManager.observeOnce(),
                mBaseStationManager.nerbyTower(),
                mBatteryManager.getBatteryLevel(),
                mGeomagneticManager.getGeomagneticInfo(),
                new Func4<Signal, List<BaseStation>, Battery, Geomagnetism, Sample>() {

                    @Override
                    public Sample call(Signal signalRecord, List<BaseStation> cellularTowers, Battery battery, Geomagnetism geomagnetic) {
                        Sample record = new Sample();
                        if (cellularTowers != null) {
                            RealmList<BaseStation> cellularTowerRealmList = new RealmList<>();
                            for (BaseStation tower : cellularTowers) {
                                cellularTowerRealmList.add(tower);
                            }
                            record.setBSList(cellularTowerRealmList);
                        }
                        record.setBtry(battery);
                        record.setSignal(signalRecord);
                        record.setMBS();
                        record.setGm(geomagnetic);
                        return record;
                    }
                });
    }
}
