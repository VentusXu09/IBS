package com.mirrordust.telecomlocate.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.mirrordust.telecomlocate.activity.SampleActivity;
import com.mirrordust.telecomlocate.entity.Sample;
import com.mirrordust.telecomlocate.interf.OnAddOrUpdateSampleListener;
import com.mirrordust.telecomlocate.interf.SampleContract;
import com.mirrordust.telecomlocate.model.DataHelper;
import com.mirrordust.telecomlocate.service.SampleService;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by LiaoShanhe on 2017/07/29/029.
 */

public class SamplePresenter implements SampleContract.Presenter, OnAddOrUpdateSampleListener {

    private boolean mRecording = false;
    private boolean isInitializing = false;
    private Realm mRealm;
    private String mMode;
    private SampleContract.View mSampleView;
    private SampleService mSampleService;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SampleService.LocalBinder binder = (SampleService.LocalBinder) iBinder;
            mSampleService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    private boolean mBound = false;

    public SamplePresenter(SampleContract.View sampleView) {
        mSampleView = sampleView;
    }

    @Override
    public void subscribe() {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void bindService(Context context) {
        Intent intent = new Intent(context, SampleService.class);
        context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mBound = true;
    }

    @Override
    public void unBindService(Context context) {
        if (mBound) {
            mSampleService.stopForeground(true);
            context.unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onResume() {
        if (isInitializing) {
            mSampleView.setActivityTitle("Initializing...");
        }else if (isRecording()) {
            mSampleView.setActivityTitle("Recording...");
        } else {
            mSampleView.setActivityTitle("TelecomLocate");
        }
        displayMainView();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void addOrUpdateSample(Sample sample) {
        DataHelper.addOrUpdateSampleAsync(mRealm, sample, this);
    }

    private void displayMainView() {
        mSampleView.switchMainView(hasNewSample());
    }

    @Override
    public void unsubscribe() {
        mRealm.close();
    }

    @Override
    public void stopSampling() {
        if (isRecording() || isInitializing) {
            mSampleService.stopCollecting();
            mRecording = false;
            isInitializing = false;
            mSampleView.setActivityTitle("TelecomLocate");
            mSampleView.setFabIconSampling(false);
            displayMainView();
        }
    }

    @Override
    public void saveNewData(String dataSetName) {
        //get the next index for new dataset
        long maxIdx = DataHelper.getMaxDataSetIndex(mRealm);
        long nextIdx = maxIdx + 1;
        DataHelper.saveNewSamples(mRealm, nextIdx, dataSetName);
        displayMainView();
    }

    @Override
    public void discardNewData() {
        DataHelper.deleteSamples(mRealm, 0);
        displayMainView();
    }

    @Override
    public boolean hasNewSample() {
        return getNewSample().size() != 0;
    }

    @Override
    public RealmResults<Sample> getNewSample() {
        return mRealm.where(Sample.class)
                .equalTo("index", 0)
                .findAll();
    }

    private boolean isRecording() {
        return mRecording;
    }

    @Override
    public void onFabClick() {
        mSampleView.showControlPanel();
//        if (isRecording()) {
//            mSampleView.showConfirmStopDialog();
//        } else {
//            mSampleView.checkPermission();
//        }
    }


    @Override
    public void startSampling(String mode, Context context) {
        startSampling(mode, 1, context);
    }

    @Override
    public void startSampling(String mode, int floor, Context context) {
        mMode = mode;

        if (!isRecording()) {
            mSampleView.switchMainView(true);

            if (!mSampleService.isRecordManagerInitialized()) {
                mSampleService.setRecordManager(context);
            }
            if (!mSampleService.isSamplePresenterInitialized()) {
                mSampleService.setSamplePresenter(this);
            }
            mSampleService.setMode(mMode);
            mSampleService.setFloor(floor);
            mSampleService.startCollecting();
            isInitializing = true;
            mSampleView.setActivityTitle("Initializing...");
            mSampleView.setFabIconSampling(true);
        } else {
            mSampleService.setMode(mMode);
            mSampleService.setFloor(floor);
        }
    }

    @Override
    public void onAddOrUpdateSample() {
        isInitializing = false;
        mRecording = true;
        mSampleView.addSample();
    }
}
