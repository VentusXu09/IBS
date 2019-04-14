package com.mirrordust.telecomlocate.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mirrordust.telecomlocate.R;
import com.mirrordust.telecomlocate.viewmodel.GalleryViewModel;
import com.mirrordust.telecomlocate.viewmodel.ViewModelFactory;

public class GalleryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GalleryViewModel galleryViewModel = obtainViewModel(this);
        galleryViewModel.init();
        setContentView(R.layout.activity_gallery);
    }

    public static GalleryViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(GalleryViewModel.class);
    }


}
