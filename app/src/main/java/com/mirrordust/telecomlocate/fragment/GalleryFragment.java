package com.mirrordust.telecomlocate.fragment;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mirrordust.telecomlocate.R;
import com.mirrordust.telecomlocate.activity.GalleryActivity;
import com.mirrordust.telecomlocate.databinding.FragmentGalleryBinding;
import com.mirrordust.telecomlocate.gui.TCLBaseFragment;
import com.mirrordust.telecomlocate.util.Constants;
import com.mirrordust.telecomlocate.util.StringUtils;
import com.mirrordust.telecomlocate.viewmodel.GalleryViewModel;

import java.util.List;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class GalleryFragment extends TCLBaseFragment {
    private static final String TAG = "GalleryFragment";

    private GalleryViewModel mViewModel;
    private FragmentGalleryBinding fragmentGalleryBinding;
    private MapView mapView;

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
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel.start();
    }

    private void setViewModel() {
        mViewModel.getPointListLiveData().observe(this, new Observer<List<Point>>() {
            @Override
            public void onChanged(@Nullable List<Point> points) {
                if (null == points) {
                    mViewModel.showDefatult();
                } else {
                    mViewModel.updateMapPoints(points);
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
        mViewModel.start();
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

}
