package com.mirrordust.telecomlocate.fragment;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.geojson.Point;
import com.mirrordust.telecomlocate.R;
import com.mirrordust.telecomlocate.activity.GalleryActivity;
import com.mirrordust.telecomlocate.databinding.FragmentGalleryBinding;
import com.mirrordust.telecomlocate.gui.TCLBaseFragment;
import com.mirrordust.telecomlocate.viewmodel.GalleryViewModel;

import java.util.List;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class GalleryFragment extends TCLBaseFragment {
    private static final String TAG = "GalleryFragment";

    private GalleryViewModel mViewModel;
    private FragmentGalleryBinding fragmentGalleryBinding;

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
        return rootView;
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        mViewModel.start();
    }

    @Override
    public boolean onBackPressed() {
        return mViewModel.onBackPressed();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
