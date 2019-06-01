package com.ventus.ibs.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ventus.ibs.R;
import com.ventus.ibs.binding.SingleLiveEvent;
import com.ventus.ibs.entity.BaseStation;
import com.ventus.ibs.entity.Geomagnetism;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.gui.interf.OnAddOrUpdateSampleListener;
import com.ventus.ibs.gui.record.RecordInterface;
import com.ventus.ibs.model.DataHelper;
import com.ventus.ibs.service.RecordService;
import com.ventus.ibs.util.Constants;
import com.ventus.ibs.util.Utils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by ventus0905 on 06/01/2019
 */
public class RecordViewModel extends AndroidViewModel implements RecordInterface.RecordViewModelAction, OnAddOrUpdateSampleListener {
    //UI Observer
    private final ObservableList<Sample> pointList = new ObservableArrayList<>();
    private final ObservableField<LineData> lineData = new ObservableField<>();
    private final ObservableField<Drawable> smarker = new ObservableField<>();
    private final ObservableField<Drawable> emarker = new ObservableField<>();
    private final MutableLiveData<String> title = new MutableLiveData<>();

    //LiveData
    private final LiveData<List<Sample>> pointListLiveData;
    private final MutableLiveData<TriggerIndex> trigger = new MutableLiveData<>();
    private final MutableLiveData<Boolean> fabIconSampling = new MutableLiveData<>();
    private final SingleLiveEvent showControlPanel = new SingleLiveEvent();

    //Service
    private RecordService mSampleService;

    //realm
    private Realm mRealm;

    private boolean isRecording = false;
    private boolean isInitializing = false;
    private boolean mBound = false;
    private String mMode;

    private long index;

    private static final float TIME_GAP = 3f;
    private static final float SIGNAL_STRENGTH_GAP = 12f;
    private static final float MAGNETIC_STRENGTH_GAP = 15f;

    private Drawable startMarker = getApplication().getResources().getDrawable(R.drawable.marker_64dp, null);
    private Drawable endMarker = getApplication().getResources().getDrawable(R.drawable.checkered_flag_64dp, null);

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            RecordService.LocalBinder binder = (RecordService.LocalBinder) iBinder;
            mSampleService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };

    public RecordViewModel(@NonNull Application application) {
        super(application);
        mRealm = Realm.getDefaultInstance();
        pointListLiveData = Transformations.switchMap(trigger, (points) -> {
            if (null == mRealm) {
                return null;
            }
            return getNewSample();
        });

    }

    public void start() {
        trigger.setValue(new TriggerIndex(index));
        title.setValue("Record");
    }

    @Override
    public void bindService(Context context) {
        Intent intent = new Intent(context, RecordService.class);
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
    public void addOrUpdateSample(Sample sample) {
        DataHelper.addOrUpdateSampleAsync(mRealm, sample, this);
    }

//    private void displayMainView() {
//        mSampleView.switchMainView(hasNewSample());
//    }

    @Override
    public void subscribe() {
        mRealm = Realm.getDefaultInstance();
    }

    @Override
    public void unsubscribe() {
        mRealm.close();
    }

    @Override
    public void stopSampling() {
        if (isRecording || isInitializing) {
            mSampleService.stopCollecting();
            isRecording = false;
            isInitializing = false;
            //TODO: View logic
            title.setValue("IBS");
            fabIconSampling.setValue(false);
//            mSampleView.setActivityTitle("IBS");
//            mSampleView.setFabIconSampling(false);
//            displayMainView();
        }
    }

    @Override
    public void saveNewData(String dataSetName) {
        //get the next index for new dataset
        long maxIdx = DataHelper.getMaxDataSetIndex(mRealm);
        long nextIdx = maxIdx + 1;
        DataHelper.saveNewSamples(mRealm, nextIdx, dataSetName);
//        displayMainView();
    }

    @Override
    public void discardNewData() {
        DataHelper.deleteSamples(mRealm, 0);
        pointList.clear();
        lineData.set(new LineData());
//        displayMainView();
    }

    @Override
    public boolean hasNewSample() {
        return getNewSample().getValue().size() != 0;
    }

    @Override
    public MutableLiveData<List<Sample>> getNewSample() {
        MutableLiveData<List<Sample>> pointLiveData = new MutableLiveData<>();
        List<Sample> result = new ArrayList<>();
        final RealmResults<Sample> samples =mRealm.where(Sample.class)
                .equalTo("index", 0)
                .findAll();
        for (Sample sample : samples) {
            result.add(sample);
        }
        pointLiveData.setValue(result);
        return pointLiveData;
    }

    @Override
    public void onFabClick() {
        showControlPanel.call();
//        mSampleView.showControlPanel();
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

        if (!isRecording) {
            //TODO: view logic
//            mSampleView.switchMainView(true);
//
            if (!mSampleService.isRecordManagerInitialized()) {
                mSampleService.setRecordManager(context);
            }
            if (!mSampleService.isRecordViewModelInitialized()) {
                mSampleService.setViewModel(this);
            }
            mSampleService.setMode(mMode);
            mSampleService.setFloor(floor);
            mSampleService.startCollecting();
            isInitializing = true;
            title.setValue("Initializing...");
            fabIconSampling.setValue(true);
//            mSampleView.setActivityTitle("Initializing...");
//            mSampleView.setFabIconSampling(true);
        } else {
            mSampleService.setMode(mMode);
            mSampleService.setFloor(floor);
        }
    }

    @Override
    public void onAddOrUpdateSample() {
        //In case too frequent recording
        if (!isRecording && !isInitializing) return;
        isInitializing = false;
        isRecording = true;
        //TODO : view logic
        title.setValue("Recording");
        trigger.setValue(new TriggerIndex(0));
//        mSampleView.setActivityTitle("Recording...");
//        mSampleView.addSample();
    }


    //Map
    public void updateMapPoints(List<Sample> points) {
        pointList.clear();
        pointList.addAll(points);
    }

    public void showDefault() {

    }

    //Line Chart
    public void generateLinePoints(List<Sample> samples) {
        List<Entry> entries2G = new ArrayList<>();
        List<Entry> entries4G = new ArrayList<>();
        long time = 0;
        List<Entry> magnetic = new ArrayList<>();
        List<Float> i2o = new ArrayList<>();
        List<Float> o2i = new ArrayList<>();
        for (Sample sample : samples) {
            List<BaseStation> baseStations = sample.getBSList();
            for (BaseStation baseStation : baseStations) {
                if (!Utils.isNearBaseStation(baseStation)) {
                    if (baseStation.getType().equalsIgnoreCase(Constants.BaseStationType.LTE.getValue())) {
                        entries4G.add(new Entry(time, baseStation.getDbm()));
                    } else {
                        entries2G.add(new Entry(time, baseStation.getDbm()));
                    }
                }
            }
            int index = (int)time / (int)TIME_GAP - 1;
            if (index > 0 && sample.getFloor() > samples.get(index).getFloor()) {
                o2i.add((float) time);
            } else if (index > 0 && sample.getFloor() < samples.get(index).getFloor()) {
                i2o.add((float) time);
            }
            Geomagnetism geomagnetism = sample.getGm();
            magnetic.add(new Entry(time, geomagnetism.getMagneticIntensity()));
            time += TIME_GAP;
        }

        LineDataSet set1 = generateLineDataSet(entries2G, "2G Signal", -1);
        LineData data = new LineData(set1);

        LineDataSet set2 = generateLineDataSet(entries4G, "4G Signal", 0);
        data.addDataSet(set2);

        if (magnetic.size() > 0){
            LineDataSet set3 = generateLineDataSet(magnetic, "Magnetic" ,1);
            data.addDataSet(set3);
        }

//        List<Float> abrupt2G = getAbruptPeriodSignal(entries2G);
//        List<Float> abrupt4G = getAbruptPeriodSignal(entries4G);
//        List<Float> abruptMagneticUp = getAbruptPeriodMagnetic(magnetic, true);
//        List<Float> abruptMagneticDown = getAbruptPeriodMagnetic(magnetic, false);
        data = addSwitchLineToChart(i2o, data, 2);
        data = addSwitchLineToChart(o2i, data, 3);

        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        lineData.set(data);
        emarker.set(startMarker);
    }

    private LineDataSet generateLineDataSet(List<Entry> entries, String label, int index) {
        LineDataSet set = new LineDataSet(entries, label);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(-1 == index ? ColorTemplate.getHoloBlue() : ColorTemplate.COLORFUL_COLORS[index]);
        set.setLineWidth(1.5f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setFillAlpha(65);
        set.setFillColor(-1 == index ? ColorTemplate.getHoloBlue() : ColorTemplate.COLORFUL_COLORS[index]);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawCircleHole(false);

        return set;
    }

    private LineData addSwitchLineToChart(List<Float> signals, LineData data, int index) {
        for (Float signal : signals) {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(signal, Constants.CHART_MAXINUM));
            entries.add(new Entry(signal, Constants.CHART_MININUM));
            data.addDataSet(generateLineDataSet(entries, "", index));
        }
        return data;
    }

    //Getter and Setter
    public ObservableList<Sample> getPointList() {
        return pointList;
    }

    public ObservableField<LineData> getLineData() {
        return lineData;
    }

    public LiveData<List<Sample>> getPointListLiveData() {
        return pointListLiveData;
    }

    public MutableLiveData<TriggerIndex> getTrigger() {
        return trigger;
    }

    public LiveData<String> getTitle() {
        return title;
    }

    public ObservableField<Drawable> getSmarker() {
        return smarker;
    }

    public ObservableField<Drawable> getEmarker() {
        return emarker;
    }

    public SingleLiveEvent getShowControlPanel() {
        return showControlPanel;
    }

    public MutableLiveData<Boolean> getFabIconSampling() {
        return fabIconSampling;
    }
}
