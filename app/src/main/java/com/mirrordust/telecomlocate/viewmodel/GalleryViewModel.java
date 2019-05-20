package com.mirrordust.telecomlocate.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mapbox.geojson.Point;
import com.mirrordust.telecomlocate.entity.BaseStation;
import com.mirrordust.telecomlocate.entity.DataSet;
import com.mirrordust.telecomlocate.entity.Geomagnetism;
import com.mirrordust.telecomlocate.entity.Sample;
import com.mirrordust.telecomlocate.model.DataHelper;
import com.mirrordust.telecomlocate.util.Constants;
import com.mirrordust.telecomlocate.util.UIUtils;
import com.mirrordust.telecomlocate.util.Utils;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class GalleryViewModel extends AndroidViewModel {
    //UI Observer
    private final ObservableList<Sample> pointList = new ObservableArrayList<>();
    private final ObservableField<LineData> lineData = new ObservableField<>();
    private final LiveData<String> title;

    //LiveData
    private final LiveData<List<Sample>> pointListLiveData;
    private final MutableLiveData<TriggerIndex> trigger = new MutableLiveData<>();


    //Events

    //realm
    private Realm mRealm;

    private long index;

    private static final float TIME_GAP = 3f;
    private static final float SIGNAL_STRENGTH_GAP = 15f;
    private static final float MAGNETIC_STRENGTH_GAP = 18f;

    private HashMap<String, Integer> colorDict = new HashMap<>();

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

    public void updateMapPoints(List<Sample> points) {
        pointList.clear();
        pointList.addAll(points);
    }

    public void showDefatult() {

    }

    private float getRandom(float range, float start) {
        return (float) (Math.random() * range) + start;
    }

    public void generateLinePoints(List<Sample> samples) {
        List<Entry> entries2G = new ArrayList<>();
        List<Entry> entries4G = new ArrayList<>();
        long time = 0;
        List<Entry> magnetic = new ArrayList<>();
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
            Geomagnetism geomagnetism = sample.getGm();
            magnetic.add(new Entry(time, geomagnetism.getMagneticIntensity()));
            time += TIME_GAP;
        }

        List<Entry> indoorAbrupt = new ArrayList<>();
        List<Entry> outdoorAbrupt = new ArrayList<>();


        LineDataSet set1 = generateLineDataSet(entries2G, "2G Signal", -1);
        LineData data = new LineData(set1);

        LineDataSet set2 = generateLineDataSet(entries4G, "4G Signal", 0);
        data.addDataSet(set2);

        if (magnetic.size() > 0){
            LineDataSet set3 = generateLineDataSet(magnetic, "Magnetic" ,1);
            data.addDataSet(set3);
        }

        List<Float> abrupt2G = getAbruptPeriodSignal(entries2G);
        List<Float> abrupt4G = getAbruptPeriodSignal(entries4G);
        List<Float> abruptMagneticUp = getAbruptPeriodMagnetic(magnetic, true);
        List<Float> abruptMagneticDown = getAbruptPeriodMagnetic(magnetic, false);
        data = addSwitchLineToChart(judgeIOSwitch(abrupt2G, abrupt4G, abruptMagneticUp), data, 2);
        data = addSwitchLineToChart(judgeIOSwitch(abrupt2G, abrupt4G, abruptMagneticDown), data, 3);

        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(9f);

        lineData.set(data);
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

    private List<Float> getAbruptPeriodSignal(List<Entry> entries) {
        List<Float> changedPoints = new ArrayList<>();
        float dataGap = SIGNAL_STRENGTH_GAP;
        for (int i = 3; i < entries.size(); i++) {
            Entry entry1 = entries.get(i);
            Entry entry2 = entries.get(i-3);
            if (Math.abs(entry1.getY() - entry2.getY()) > dataGap) {
                changedPoints.add(entry1.getX());
            }
        }
        return changedPoints;
    }

    private List<Float> getAbruptPeriodMagnetic(List<Entry> entries, boolean isMore) {
        List<Float> abruptPoints = new ArrayList<>();
        float dataGap = isMore ? MAGNETIC_STRENGTH_GAP : -MAGNETIC_STRENGTH_GAP;
        for (int i = 3; i < entries.size(); i++) {
            Entry entry1 = entries.get(i);
            Entry entry2 = entries.get(i-3);
            if (entry1.getY() - entry2.getY() > dataGap) {
                abruptPoints.add(entry1.getX());
            }
        }
        return abruptPoints;
    }

    private List<Float> judgeIOSwitch(List<Float> signals2G, List<Float> signals4G, List<Float> magnetics) {
        List<Float> signals = signals2G;
        signals.retainAll(signals4G);
        signals.retainAll(magnetics);
        for (int i = 3; i<signals.size(); i++) {
            if (signals.get(i) - signals.get(i-3) == TIME_GAP) {
                signals.remove(i-3);
            }
        }
        return signals;
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

    public boolean onBackPressed() {
        return false;
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
}
