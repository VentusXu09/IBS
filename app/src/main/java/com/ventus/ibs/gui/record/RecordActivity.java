package com.ventus.ibs.gui.record;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.ventus.ibs.R;
import com.ventus.ibs.gui.BaseActivity;
import com.ventus.ibs.gui.BaseFragment;
import com.ventus.ibs.util.PermissionHelper;
import com.ventus.ibs.viewmodel.RecordViewModel;
import com.ventus.ibs.viewmodel.ViewModelFactory;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ventus0905 on 05/05/2019
 */
public class RecordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record;
    }

    @Nullable
    @Override
    public BaseFragment getInitialFragment() {
        PermissionHelper.checkPermissions(this, this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE});
        return RecordFragment.newInstance();
    }

    public static RecordViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        RecordViewModel viewModel = ViewModelProviders.of(activity, factory).get(RecordViewModel.class);
        return viewModel;
    }

    @Override
    public void onPermissionGranted(@NonNull String... permission) {
        Realm.init(this.getApplicationContext());
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build());
        RecordViewModel viewModel = obtainViewModel(this);
        viewModel.start();
    }

    @Override
    public void onPermissionDenied(@NonNull String permission) {
        // dismiss, do nothing
    }

    @Override
    public void onPermissionDisable(@NonNull String permission) {
        PermissionHelper.showPermissionDialog(this, getString(R.string.permission_message, getString(R.string.storage)));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
