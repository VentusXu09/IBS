package com.ventus.ibs.gui.record;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.ventus.ibs.R;
import com.ventus.ibs.databinding.FragmentRecordBinding;
import com.ventus.ibs.entity.Sample;
import com.ventus.ibs.gui.IBSBaseFragment;
import com.ventus.ibs.gui.about.AboutActivity;
import com.ventus.ibs.gui.about.SettingsActivity;
import com.ventus.ibs.gui.data.DataActivity;
import com.ventus.ibs.gui.gallery.GalleryActivity;
import com.ventus.ibs.gui.sample.SamplePresenter;
import com.ventus.ibs.util.Constants;
import com.ventus.ibs.util.StringUtils;
import com.ventus.ibs.viewmodel.RecordViewModel;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static android.content.Context.POWER_SERVICE;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;


/**
 * Created by ventus0905 on 05/05/2019
 */
public class RecordFragment extends IBSBaseFragment implements SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "RecordFragment";

    private RecordViewModel mViewModel;
    private FragmentRecordBinding fragmentRecordBinding;

    private FloatingActionButton mFab;

    private PowerManager.WakeLock wakeLock;


    private MapView mapView;
    private LineChart lineChart;
    private SeekBar seekBarX;
    private TextView tvX;

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private MenuItem saveMenu;
    private MenuItem discardMenu;

    public static RecordFragment newInstance() {
        return new RecordFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(getContext(), Constants.MAPBOX_ACCESS_TOKEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRecordBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        mViewModel = RecordActivity.obtainViewModel(getActivity());
        setViewModel();
        fragmentRecordBinding.setViewModel(mViewModel);

        // Create the presenter
        Realm.init(getContext());
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        mViewModel.subscribe();
        mViewModel.bindService(getActivity());

        View rootView = fragmentRecordBinding.getRoot();

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        lineChart = rootView.findViewById(R.id.lineChart);
        tvX = rootView.findViewById(R.id.tvXMax);
        seekBarX = rootView.findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);
        initLineChart();

        return rootView;
    }

    private void setViewModel() {
        mViewModel.getPointListLiveData().observe(this, new Observer<List<Sample>>() {
            @Override
            public void onChanged(@Nullable List<Sample> points) {
                if (null == points) {
                    mViewModel.showDefault();
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
        mViewModel.getShowControlPanel().observe(this, new Observer() {
            @Override
            public void onChanged(@Nullable Object o) {
                showControlPanel();
            }
        });
        mViewModel.getFabIconSampling().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                setFabIconSampling(aBoolean);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setTitle("Record");
//        initMainView();

        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.onFabClick();
            }
        });

        PowerManager powerManager = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "arctic:sampling");
        wakeLock.acquire();
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
    public int getLayoutId() {
        return R.layout.fragment_record;
    }

    public void setFabIconSampling(boolean isSampling) {
        if (isSampling) {
            mFab.setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.ic_fab_stop));
        } else {
            mFab.setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(), R.drawable.ic_fab_start));
        }
    }

    public void showControlPanel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        final View controlPanelView = inflater.inflate(R.layout.controlpanel, null);
        final RadioGroup radioGroup = (RadioGroup) controlPanelView.findViewById(R.id.motion_modes);
        final EditText customMode = (EditText) controlPanelView.findViewById(R.id.mode_custom);
        final EditText floorNumberInput = (EditText) controlPanelView.findViewById(R.id.floor_number_input);

        builder.setView(controlPanelView)
                .setPositiveButton("Record", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isPermissionGranted()) {
                            String theMode;
                            // start using selected or typed mode
                            int selectedID = radioGroup.getCheckedRadioButtonId();
                            RadioButton selectedButton = (RadioButton) controlPanelView.findViewById(selectedID);
                            String mode1 = String.valueOf(selectedButton.getText());

                            String mode2 = String.valueOf(customMode.getText());

                            if (!mode2.equals("")) {
                                theMode = mode2;
                            } else {
                                theMode = mode1;
                                if (selectedID == R.id.mode_other) {
                                    theMode = "not-set";
                                }
                            }

                            String floorNumberText = String.valueOf(floorNumberInput.getText());
                            if (StringUtils.isEmpty(floorNumberText)) {
                                mViewModel.startSampling(theMode,  getApplicationContext());
                            } else {
                                mViewModel.startSampling(theMode, Integer.valueOf(floorNumberText), getApplicationContext());
                            }
                        } else {
//                            requestPermissions();
                        }
                    }
                })
                .setNeutralButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.stopSampling();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    private boolean isPermissionGranted() {
        return !(
                ActivityCompat
                        .checkSelfPermission(getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        ||
                        ActivityCompat
                                .checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.READ_PHONE_STATE)
                                != PackageManager.PERMISSION_GRANTED
                        ||
                        ActivityCompat
                                .checkSelfPermission(getApplicationContext(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.menu_new_sample, menu);
        saveMenu = menu.findItem(R.id.action_save);
        saveMenu.setVisible(true);
        saveMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSaveDataDialog();
                return true;
            }
        });
        discardMenu = menu.findItem(R.id.action_discard);
        discardMenu.setVisible(true);
        discardMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showConfirmDiscardDialog();
                return true;
            }
        });
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_save) {
//            showSaveDataDialog();
//        } else if (id == R.id.action_discard) {
//            showConfirmDiscardDialog();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void showConfirmDiscardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are your sure to delete all data?")
                .setTitle("warning")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mViewModel.discardNewData();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    public void showSaveDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input)
                .setTitle("Save new samples")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = input.getText().toString();
                        mViewModel.saveNewData(name);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create()
                .show();
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
