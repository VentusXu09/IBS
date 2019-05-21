package com.ventus.ibs.model;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by LiaoShanhe on 2017/07/18/018.
 */

public class LocationManager {
    private static final String TAG = "LocationManager";
    private final static float LOCATION_REFRESH_DISTANCE = 15;
    private final static long LOCATION_REFRESH_TIME = 10 * 1000;
    private final static long TWO_MINUTES = (long) 2 * 60 * 1000 * 1000 * 1000;
    private Subscriber<? super Location> mLocationOnSubscribe;
    private android.location.LocationManager mLocationManager;
    private Context mContext;
    private final android.location.LocationListener mLocationListener = new android.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "Location changed...");
            Log.v(TAG, "Latitude :        " + location.getLatitude());
            Log.v(TAG, "Longitude :       " + location.getLongitude());
            if (location.getProvider().equalsIgnoreCase(mLocationManager.GPS_PROVIDER)) {

            }
//            mLocationOnSubscribe.onNext(location);
//            mLocationOnSubscribe.onCompleted();
//            stopListening();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "changed status location : " + status);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "provider enabled : " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "provider disabled : " + provider);
            //stopListening();
            //mLocationOnSubscribe.onCompleted();
        }
    };


    public LocationManager(Context context) {
        mContext = context;
        mLocationManager = (android.location.LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }



    /**
     * This function always return GPS provider
     * While GPS is not reliable in poor environment
     * use {@link #getLastKnownLocation()} for better position
     * @return
     */
    @Deprecated
    private String getProvider() {
        Criteria criteria =  new Criteria();

        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        criteria.setAltitudeRequired(true);

        criteria.setBearingRequired(true);

        criteria.setCostAllowed(true);

        criteria.setPowerRequirement(Criteria.POWER_LOW);

        return mLocationManager.getBestProvider(criteria, true);
    }

    private void startListening() {
        Log.v(TAG, "start request listener");

        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

//            String provider = getProvider();
//            if (!mLocationManager.isProviderEnabled(provider)) {
//                Log.v(TAG, "Provider not available = " + provider);
//                mLocationOnSubscribe.onNext(null);
//                mLocationOnSubscribe.onCompleted();
//                return;
//            }
//            mLocationManager.requestSingleUpdate(provider, mLocationListener, null);
            Location lastKnownLocation = getLastKnownLocation();
            if (lastKnownLocation != null) {
                Log.v(TAG, lastKnownLocation.getLongitude() + ", " + lastKnownLocation.getLatitude());
                mLocationOnSubscribe.onNext(lastKnownLocation);
                mLocationOnSubscribe.onCompleted();
            } else {
                Log.e(TAG, "lastKnownLocation is null");
                mLocationOnSubscribe.onNext(null);
                mLocationOnSubscribe.onCompleted();
            }
        } else {
            Log.v(TAG, "Check permission location failed");
            mLocationOnSubscribe.onNext(null);
            mLocationOnSubscribe.onCompleted();
        }
    }

    private Location getLastKnownLocation() throws SecurityException {
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            //passive provider is redundant
            if (provider.equalsIgnoreCase(mLocationManager.PASSIVE_PROVIDER)) continue;
            mLocationManager.requestLocationUpdates(
                    provider,
                    LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            bestLocation = getBestLocation(l, bestLocation);
        }
        return bestLocation;
    }

    private void stopListening() {
        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }

    public Observable<Location> getLocation() {
        Log.v(TAG, "get location [permission okay]");
        return rx.Observable.create(new Observable.OnSubscribe<Location>() {
            @Override
            public void call(Subscriber<? super Location> subscriber) {
                Log.v(TAG, "start observer location");
                mLocationOnSubscribe = subscriber;
                startListening();
            }
        });
    }

    protected Location getBestLocation(Location location,
                                       Location currentBestLocation) {
        if (currentBestLocation == null) {
            //check whether the first location is fresh
            long timeDelta = location.getElapsedRealtimeNanos() - SystemClock.elapsedRealtimeNanos();
            boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
            if (isSignificantlyOlder) {
                return null;
            }
            // A new and fresh location is better than no location
            return location;
        }
        if (location == null) return currentBestLocation;
        // Check whether the new location fix is newer or older
        long timeDelta = location.getElapsedRealtimeNanos() - currentBestLocation.getElapsedRealtimeNanos();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;
        // If it's been more than two minutes since the current location, use
        // the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return location;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return currentBestLocation;
        }
        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
                .getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = location.getProvider().equalsIgnoreCase(currentBestLocation.getProvider());
        // Determine location quality using a combination of timeliness and
        // accuracy
        if (isMoreAccurate) {
            return location;
        } else if (isNewer && !isLessAccurate) {
            return location;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return location;
        }
        return currentBestLocation;
    }
}
