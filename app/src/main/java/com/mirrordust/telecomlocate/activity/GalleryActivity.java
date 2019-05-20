package com.mirrordust.telecomlocate.activity;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.mirrordust.telecomlocate.fragment.GalleryFragment;
import com.mirrordust.telecomlocate.fragment.RecordFragment;
import com.mirrordust.telecomlocate.gui.BaseActivity;
import com.mirrordust.telecomlocate.gui.BaseFragment;
import com.mirrordust.telecomlocate.viewmodel.GalleryViewModel;
import com.mirrordust.telecomlocate.viewmodel.ViewModelFactory;

import static com.mirrordust.telecomlocate.util.Constants.SAMPLE_INDEX;

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
