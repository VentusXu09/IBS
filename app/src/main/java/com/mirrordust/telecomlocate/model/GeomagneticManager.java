package com.mirrordust.telecomlocate.model;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.mirrordust.telecomlocate.entity.Geomagnetism;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by LiaoShanhe on 2017/07/18/018.
 */

public class GeomagneticManager {
    private static final String TAG = "GeomagneticManager";
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private final float[] I = new float[9];
    private Context mContext;
    private SensorManager mSensorManager;
    private boolean mAccelero_flag = false;
    private boolean mMagneto_flag = false;

    private Subscriber<? super Geomagnetism> mSubscriber;

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, mAccelerometerReading, 0, mAccelerometerReading.length);
                mAccelero_flag = true;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, mMagnetometerReading, 0, mMagnetometerReading.length);
                mMagneto_flag = true;
            }
            if (mAccelero_flag && mMagneto_flag) {
                updateOrientationAngles();
                mAccelero_flag = false;
                mMagneto_flag = false;
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public GeomagneticManager(Context context) {
        mContext = context;
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
    }

    public void updateOrientationAngles() {
        SensorManager.getRotationMatrix(mRotationMatrix, I, mAccelerometerReading, mMagnetometerReading);
        float magneticIntensity = calMagneticIntensity();
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        measuring(mMagnetometerReading, mOrientationAngles, magneticIntensity);
    }

    private void measuring(float[] geomag, float[] orientation, float magneticIntensity) {
        Geomagnetism geomagneticRecord = new Geomagnetism();
        geomagneticRecord.setX(geomag[0]);
        geomagneticRecord.setY(geomag[1]);
        geomagneticRecord.setZ(geomag[2]);
        // azimuth
        geomagneticRecord.setAlpha(orientation[0]);
        // pitch
        geomagneticRecord.setBeta(orientation[1]);
        // roll
        geomagneticRecord.setGamma(orientation[2]);

        geomagneticRecord.setMagneticIntensity(magneticIntensity);
        mSubscriber.onNext(geomagneticRecord);
        mSubscriber.onCompleted();
    }

    private float calMagneticIntensity() {
        float intensity = (I[3]*mRotationMatrix[0]+I[4]*mRotationMatrix[3]+I[5]*mRotationMatrix[6])*mMagnetometerReading[0]+
                (I[3]*mRotationMatrix[1]+I[4]*mRotationMatrix[4]+I[5]*mRotationMatrix[7])*mMagnetometerReading[1]+
                (I[3]*mRotationMatrix[2]+I[4]*mRotationMatrix[5]+I[5]*mRotationMatrix[8])*mMagnetometerReading[2];
        return intensity;
    }

    private void startListening() {
        mSensorManager.registerListener(
                mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(
                mSensorEventListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopListening() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    public rx.Observable<Geomagnetism> getGeomagneticInfo() {
        Log.v(TAG, "get geomagnetic information");
        return observe().flatMap(new Func1<Geomagnetism, Observable<Geomagnetism>>() {
            @Override
            public rx.Observable<Geomagnetism> call(Geomagnetism geomagneticRecord) {
                stopListening();
                return rx.Observable.just(geomagneticRecord);
            }
        });
    }

    public rx.Observable<Geomagnetism> observe() {
        return rx.Observable.create(new rx.Observable.OnSubscribe<Geomagnetism>() {
            @Override
            public void call(Subscriber<? super Geomagnetism> subscriber) {
                mSubscriber = subscriber;
                startListening();
            }
        });
    }
}
