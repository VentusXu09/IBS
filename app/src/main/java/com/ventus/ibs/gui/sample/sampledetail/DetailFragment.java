package com.ventus.ibs.gui.sample.sampledetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ventus.ibs.R;
import com.ventus.ibs.entity.Wifi;
import com.ventus.ibs.gui.sample.sampledetail.SampleDetailActivity;
import com.ventus.ibs.gui.sample.sampledetail.SampleDetailAdapter;
import com.ventus.ibs.entity.BaseStation;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.model.DataHelper;
import com.ventus.ibs.pojo.DetailItem;
import com.ventus.ibs.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment {

    private static final String TAG = "DetailFragment";
    private RecyclerView mRecyclerView;

    public DetailFragment() {
        // Required empty public constructor
    }

//    public static DetailFragment newInstance(String id) {
//        DetailFragment fragment = new DetailFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_SAMPLE_ID, id);
//        fragment.setArguments(args);
//        return fragment;
//    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_SAMPLE_ID);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FrameLayout frameLayout = (FrameLayout) inflater.inflate(R.layout.fragment_detail, container, false);
        mRecyclerView = (RecyclerView) frameLayout.findViewById(R.id.detail_rv);
        return frameLayout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecycleView();
    }

    private void initRecycleView() {
        mRecyclerView.setHasFixedSize(true);
        SampleDetailActivity parentActivity = (SampleDetailActivity) getActivity();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(parentActivity));
        mRecyclerView.setAdapter(new SampleDetailAdapter(getDetailItems(parentActivity)));
    }

    private List<DetailItem> getDetailItems(SampleDetailActivity parentActivity) {
        String mID = parentActivity.getSampleId();
        List<DetailItem> detailItems = new ArrayList<>();
        Sample sample = DataHelper.getSample(parentActivity.getRealm(), mID);
        //ID
        detailItems.add(new DetailItem("ID:", mID));

        //time location mode
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ Basis ]", ""));
        detailItems.add(new DetailItem("time", Utils.timestamp2LocalTime(sample.getTime())));
        detailItems.add(new DetailItem("longitude", Double.toString(sample.getLatLng().getLongitude())));
        detailItems.add(new DetailItem("latitude", Double.toString(sample.getLatLng().getLatitude())));
        detailItems.add(new DetailItem("altitude", Double.toString(sample.getLatLng().getAltitude())));
        detailItems.add(new DetailItem("accuracy", Float.toString(sample.getLatLng().getAccuracy())));
        detailItems.add(new DetailItem("speed", Float.toString(sample.getLatLng().getSpeed())));
        detailItems.add(new DetailItem("mode", sample.getMode()));
        detailItems.add(new DetailItem("floor", String.valueOf(sample.getFloor())));
        //BS list
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ BS list ]", ""));
        for (int i = 0; i < sample.getBSList().size(); i++) {
            BaseStation bs = sample.getBSList().get(i);
            detailItems.add(new DetailItem(String.format("# %s ---------", i + 1), ""));
            detailItems.add(new DetailItem("mcc", Integer.toString(bs.getMcc())));
            detailItems.add(new DetailItem("mnc", Integer.toString(bs.getMnc())));
            detailItems.add(new DetailItem("lac", Integer.toString(bs.getLac())));
            detailItems.add(new DetailItem("cid", Integer.toString(bs.getCid())));
            detailItems.add(new DetailItem("arfcn", Integer.toString(bs.getArfcn())));
            detailItems.add(new DetailItem("bsic_psc_pci", Integer.toString(bs.getBsic_psc_pci())));
            detailItems.add(new DetailItem("lon", Double.toString(bs.getLon())));
            detailItems.add(new DetailItem("lat", Double.toString(bs.getLat())));
            detailItems.add(new DetailItem("asuLevel", Integer.toString(bs.getAsuLevel())));
            detailItems.add(new DetailItem("signalLevel", Integer.toString(bs.getSignalLevel())));
            detailItems.add(new DetailItem("dbm", Integer.toString(bs.getDbm())));
            detailItems.add(new DetailItem("type", bs.getType()));
        }
        //Connected base station
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ Connected BS ]", ""));
        detailItems.add(new DetailItem("mcc", Integer.toString(sample.getMBS().getMcc())));
        detailItems.add(new DetailItem("mnc", Integer.toString(sample.getMBS().getMnc())));
        detailItems.add(new DetailItem("lac", Integer.toString(sample.getMBS().getLac())));
        detailItems.add(new DetailItem("cid", Integer.toString(sample.getMBS().getCid())));
        detailItems.add(new DetailItem("arfcn", Integer.toString(sample.getMBS().getArfcn())));
        detailItems.add(new DetailItem("bsic_psc_pci", Integer.toString(sample.getMBS().getBsic_psc_pci())));
        detailItems.add(new DetailItem("lon", Double.toString(sample.getMBS().getLon())));
        detailItems.add(new DetailItem("lat", Double.toString(sample.getMBS().getLat())));
        detailItems.add(new DetailItem("asuLevel", Integer.toString(sample.getMBS().getAsuLevel())));
        detailItems.add(new DetailItem("signalLevel", Integer.toString(sample.getMBS().getSignalLevel())));
        detailItems.add(new DetailItem("dbm", Integer.toString(sample.getMBS().getDbm())));
        detailItems.add(new DetailItem("type", sample.getMBS().getType()));
        //Mobile signal strength
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ Mobile signal strength ]", ""));
        detailItems.add(new DetailItem("CellId", Integer.toString(sample.getSignal().getCellid())));
        detailItems.add(new DetailItem("dbm", Integer.toString(sample.getSignal().getDbm())));
        detailItems.add(new DetailItem("isGsm", Boolean.toString(sample.getSignal().isGsm())));
        detailItems.add(new DetailItem("signalToNoiseRatio", Integer.toString(sample.getSignal().getSignalToNoiseRatio())));
        detailItems.add(new DetailItem("evdoEcio", Integer.toString(sample.getSignal().getEvdoEcio())));
        detailItems.add(new DetailItem("level", Integer.toString(sample.getSignal().getLevel())));
        //Battery
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ Battery ]", ""));
        detailItems.add(new DetailItem("level", Double.toString(sample.getBtry().getLevel())));
        detailItems.add(new DetailItem("capacity", Double.toString(sample.getBtry().getCapacity())));
        //Geomagnetism
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ Geomagnetism ]", ""));
        detailItems.add(new DetailItem("x", Double.toString(sample.getGm().getX())));
        detailItems.add(new DetailItem("y", Double.toString(sample.getGm().getY())));
        detailItems.add(new DetailItem("z", Double.toString(sample.getGm().getZ())));
        detailItems.add(new DetailItem("α", Double.toString(sample.getGm().getAlpha())));
        detailItems.add(new DetailItem("β", Double.toString(sample.getGm().getBeta())));
        detailItems.add(new DetailItem("γ", Double.toString(sample.getGm().getGamma())));
        detailItems.add(new DetailItem("Magnetic Intensity", Float.toString(sample.getGm().getMagneticIntensity())));
        //Barometric
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[ Barometric ]", ""));
        detailItems.add(new DetailItem("pressure", Double.toString(sample.getBaro().getPressure())));
        //Wifi
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[Wifi ]", ""));
        for (int i = 0; i < sample.getWifiList().size(); i++) {
            Wifi wifi = sample.getWifiList().get(i);
            detailItems.add(new DetailItem(String.format("# %s ---------", i + 1), ""));
            detailItems.add(new DetailItem("bssid", wifi.getBssid()));
            detailItems.add(new DetailItem("ssid", wifi.getSsid()));
            detailItems.add(new DetailItem("level", String.valueOf(wifi.getStrength())));
        }
        //extra label
        detailItems.add(new DetailItem("", ""));
        detailItems.add(new DetailItem("[Extra Labels]", ""));
        detailItems.add(new DetailItem("extra label 1", "Value1"));
        detailItems.add(new DetailItem("extra label 2", "Value2"));
        detailItems.add(new DetailItem("extra label 3", "Value3"));
        return detailItems;
    }
}
