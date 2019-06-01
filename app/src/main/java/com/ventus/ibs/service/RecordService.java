package com.ventus.ibs.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.ventus.ibs.R;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.gui.sample.SampleActivity;
import com.ventus.ibs.gui.sample.SamplePresenter;
import com.ventus.ibs.model.SampleManager;
import com.ventus.ibs.viewmodel.RecordViewModel;

import rx.functions.Action1;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * Created by ventus0905 on 06/01/2019
 */
public class RecordService extends Service {
    public static final String TAG = "SampleService";
    public static final int NOTIFICATION_ID = 9981;
    public static String NOTIFICATION_CHANNEL_NAME = "Telecomlocate";
    public static String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";

    private final IBinder mBinder = new RecordService.LocalBinder();
    private String mode;
    private int floor;
    private SampleManager mSampleManager;
//    private SamplePresenter mPresenter;
    private RecordViewModel viewModel;
    private Handler mHandler = new Handler();
    private Runnable mDataCollection = new Runnable() {
        @Override
        public void run() {
            try {
                requestRecord();
            } finally {
                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String stringValue = sharedPref.getString("sampling_interval",
                        getString(R.string.pref_default_sampling_interval));
                long samplingInterval = Long.parseLong(stringValue); // in seconds
                mHandler.postDelayed(mDataCollection, samplingInterval * 1000);
            }
        }
    };

    public RecordService() {
    }

    private void requestRecord() {
        mSampleManager.fetchRecord().subscribe(new Action1<Sample>() {
            @Override
            public void call(Sample sample) {
                sample.setIndex(0);
                sample.setMode(mode);
                sample.setFloor(floor);
                viewModel.addOrUpdateSample(sample);
            }
        });
    }

    public void startCollecting() {
        mDataCollection.run();
    }

    public void stopCollecting() {
        mHandler.removeCallbacks(mDataCollection);
    }

    public void setRecordManager(Context context) {
        mSampleManager = new SampleManager(context);
    }

    public boolean isRecordManagerInitialized() {
        return mSampleManager != null;
    }

    public void setViewModel(RecordViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public boolean isRecordViewModelInitialized() {
        return viewModel != null;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Intent nfIntent = new Intent(this, SampleActivity.class);
        nfIntent.setAction(Intent.ACTION_MAIN);
        nfIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        nfIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            //Create a NotificationChannel first
            NotificationChannel chan =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            chan.setImportance(NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);

            PendingIntent contentIntent = PendingIntent.getActivity(
                    getApplicationContext(), 0, nfIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID).setOngoing(true)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("Sample telco-data")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_launcher_48dp)
                    .setContentIntent(contentIntent)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(NOTIFICATION_ID, notification);
        } else {
            this.startService(nfIntent);
        }
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public RecordService getService() {
            return RecordService.this;
        }
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        super.onDestroy();
    }
}
