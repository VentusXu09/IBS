package com.ventus.ibs.gui.gallery;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.ventus.ibs.R;
import com.ventus.ibs.databinding.FragmentGalleryBinding;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.gui.IBSBaseFragment;
import com.ventus.ibs.util.Constants;
import com.ventus.ibs.util.StringUtils;
import com.ventus.ibs.viewmodel.GalleryViewModel;

import java.util.List;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class GalleryFragment extends IBSBaseFragment implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "GalleryFragment";

    private GalleryViewModel mViewModel;
    private FragmentGalleryBinding fragmentGalleryBinding;
    private MapView mapView;
    private LineChart lineChart;
    private SeekBar seekBarX;
    private TextView tvX;

    protected Typeface tfRegular;
    protected Typeface tfLight;

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_gallery;
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getActivity(), Constants.MAPBOX_ACCESS_TOKEN);
        tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentGalleryBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mViewModel = GalleryActivity.obtainViewModel(getActivity());
        setViewModel();
        fragmentGalleryBinding.setViewModel(mViewModel);
        View rootView = fragmentGalleryBinding.getRoot();

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        lineChart = rootView.findViewById(R.id.lineChart);
        tvX = rootView.findViewById(R.id.tvXMax);
        seekBarX = rootView.findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);
        initLineChart();

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.start();
    }

    private void setViewModel() {
        mViewModel.getPointListLiveData().observe(this, new Observer<List<Sample>>() {
            @Override
            public void onChanged(@Nullable List<Sample> points) {
                if (null == points) {
                    mViewModel.showDefatult();
                } else {
                    mViewModel.updateMapPoints(points);
                    mViewModel.generateLinePoints(points);
                }
            }
        });
        mViewModel.getTitle().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                if (StringUtils.isEmpty(s)) {
                    setTitle(R.string.activity_label_gallery);
                } else {
                    setTitle(s);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    public void initLineChart() {
        // no description text
        lineChart.getDescription().setEnabled(false);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        lineChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setDrawGridBackground(false);
        lineChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // add data
        seekBarX.setProgress(100);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTypeface(tfLight);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        leftAxis.setTypeface(tfLight);
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(Constants.CHART_MININUM);
        leftAxis.setAxisMaximum(Constants.CHART_MAXINUM);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    @Override
    public boolean onBackPressed() {
        return mViewModel.onBackPressed();
    }

    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));

//        setData(seekBarX.getProgress(), 50);

        // redraw
//        lineChart.invalidate();
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}
