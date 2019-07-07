package com.ventus.ibs.model;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.util.Log;
import com.ventus.ibs.entity.Wifi;
import io.realm.RealmList;
import rx.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * mgdp
 * Created by xuxiaofeng on 2019/7/6 9:59 PM
 */

public class WifiManager {
    private static final String TAG = "WifiManager";

    public static String WIFI_STATE_CHANGED_ACTION = "wifi state changed action";

    private Context mContext;
    private android.net.wifi.WifiManager mWifiManager;
    private BroadcastReceiver mBroadcastReceiver;

    public WifiManager(Context context) {
        mContext = context;
        mWifiManager = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (! mWifiManager.isWifiEnabled()) {
            if (mWifiManager.getWifiState() != android.net.wifi.WifiManager.WIFI_STATE_ENABLING) {
                mWifiManager.setWifiEnabled(true);
            }
        }

    }

    public List<Wifi> startListening() {
        if (null == mWifiManager) {
            return null;
        }
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int strength = wifiInfo.getRssi();
        int speed = wifiInfo.getLinkSpeed();
        String bssid = wifiInfo.getBSSID();
        String ssid = wifiInfo.getSSID();
        String units = WifiInfo.LINK_SPEED_UNITS;
        String wifiInformation = "ScanResults: ";
        String connectedWifi = "Current Wifi info: " + "bssid: " + bssid + "ssid : " + ssid + "speed : " + speed + units + "Strength : " + strength;

        List<ScanResult> results = mWifiManager.getScanResults();
        List<Wifi> wifiResults = new ArrayList<>();
        for (ScanResult result: results) {
            wifiInformation += result.BSSID + "," + result.SSID + "," + result.level + "\n";
            wifiResults.add(new Wifi(result.SSID, result.BSSID, result.level));
        }
        Log.d(TAG, connectedWifi);
        Log.d(TAG, wifiInformation);

        return wifiResults;
    }

    public rx.Observable<List<Wifi>> getWifiResults() {
        return rx.Observable.create(new rx.Observable.OnSubscribe<List<Wifi>>() {
            @Override
            public void call(Subscriber<? super List<Wifi>> subscriber) {
                mBroadcastReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context arg0, Intent intent) {
                        List<Wifi> wifiList = startListening();
                        mContext.unregisterReceiver(mBroadcastReceiver);


                        subscriber.onNext(wifiList);
                        subscriber.onCompleted();
                    }
                };
                mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(android.net.wifi.WifiManager.WIFI_STATE_CHANGED_ACTION));
            }
        });
    }

    public static RealmList<Wifi> list2RealmList(List<Wifi> wifiList) {
        RealmList<Wifi> results = new RealmList<>();

        for (Wifi wifi : wifiList) {
            results.add(wifi);
        }
        return results;
    }
}
