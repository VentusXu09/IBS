package com.ventus.ibs.gui.gallery;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.ventus.ibs.gui.BaseActivity;
import com.ventus.ibs.gui.BaseFragment;
import com.ventus.ibs.viewmodel.GalleryViewModel;
import com.ventus.ibs.viewmodel.ViewModelFactory;

import static com.ventus.ibs.util.Constants.SAMPLE_INDEX;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class GalleryActivity extends BaseActivity {

    public static void launchActivity(Activity ctx, long index) {
        Intent intent = new Intent(ctx, GalleryActivity.class);
        intent.putExtra(SAMPLE_INDEX, index);
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GalleryViewModel galleryViewModel = obtainViewModel(this);
        long index = getIntent().getLongExtra(SAMPLE_INDEX, 0);
        galleryViewModel.init(index);
    }

    public static GalleryViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(GalleryViewModel.class);
    }

    @Override
    public BaseFragment getInitialFragment() {
        return GalleryFragment.newInstance();
    }

}
