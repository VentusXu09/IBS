package com.mirrordust.telecomlocate.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import com.mapbox.geojson.Point;
import com.mirrordust.telecomlocate.entity.DataSet;
import com.mirrordust.telecomlocate.model.DataHelper;

import java.util.List;

import io.realm.Realm;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class GalleryViewModel extends AndroidViewModel {
    //UI Observer
    private final ObservableList<Point> pointList = new ObservableArrayList<>();
    private final LiveData<String> title;

    //LiveData
    private final LiveData<List<Point>> pointListLiveData;
    private final MutableLiveData<TriggerIndex> trigger = new MutableLiveData<>();

    //Events

    //realm
    private Realm mRealm;

    private long index;

    public GalleryViewModel(@NonNull Application application) {
        super(application);
        mRealm = Realm.getDefaultInstance();
        pointListLiveData = Transformations.switchMap(trigger, (points) -> {
            if (null == mRealm) {
                return null;
            }
            return DataHelper.getPointsWithIndex(mRealm, trigger.getValue().getIndex());
        });
        title = Transformations.switchMap(trigger, (s) -> {
            if (null == mRealm) {
                return null;
            }
            return DataHelper.getDataSetName(mRealm, trigger.getValue().getIndex());
        });
    }

    public void start() {
        trigger.setValue(new TriggerIndex(index));
    }

    public void init(long index) {
        this.index = index;
    }

    public void updateMapPoints(List<Point> points) {
        pointList.clear();
        pointList.addAll(points);

    }

    public void showDefatult() {

    }

    public boolean onBackPressed() {
        return false;
    }

    //Getter and Setter
    public ObservableList<Point> getPointList() {
        return pointList;
    }

    public LiveData<List<Point>> getPointListLiveData() {
        return pointListLiveData;
    }

    public MutableLiveData<TriggerIndex> getTrigger() {
        return trigger;
    }

    public LiveData<String> getTitle() {
        return title;
    }
}
