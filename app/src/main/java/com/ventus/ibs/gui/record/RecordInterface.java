package com.ventus.ibs.gui.record;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.ventus.ibs.entity.Sample;

import java.util.List;

import io.realm.RealmResults;

/**
 * Created by ventus0905 on 06/01/2019
 */
public interface RecordInterface {
    interface RecordFragmentView {

    }

    interface RecordViewModelAction {
        void startSampling(String mode, Context context);

        void startSampling(String mode, int floor, Context context);

        void stopSampling();

        void saveNewData(String dataSetName);

        void discardNewData();

        boolean hasNewSample();

        MutableLiveData<List<Sample>> getNewSample();

        void onFabClick();

        void bindService(Context context);

        void unBindService(Context context);

//        void onResume();
//
//        void onPause();

        void addOrUpdateSample(Sample sample);

        /**
         * subscribe service in viewmodel
         */
        void subscribe();

        /**
         * unsubscribe service in viewmodel
         */
        void unsubscribe();
    }
}
