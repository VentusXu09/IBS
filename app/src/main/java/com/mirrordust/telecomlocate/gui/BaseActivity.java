package com.mirrordust.telecomlocate.gui;

import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mirrordust.telecomlocate.R;
import com.mirrordust.telecomlocate.activity.AboutActivity;
import com.mirrordust.telecomlocate.activity.DataActivity;
import com.mirrordust.telecomlocate.activity.SettingsActivity;
import com.mirrordust.telecomlocate.util.PermissionHelper;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PermissionHelper.PermissionListener,
        BaseActivityListener {
    private static final String TAG = "BaseActivity";
    private FragmentController mFragmentController;

    protected FrameLayout mMainContainer;
    private BaseFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initUI(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initUI(@Nullable Bundle savedInstanceState) {
//        mAppBarLayout = findViewById(R.id.app_bar_layout);
//        mToolbar = findViewById(R.id.toolbar);
//        mContainerUnderToolbar = findViewById(R.id.container_under_toolbar);
//        mMainContainer = findViewById(R.id.sf_container);
//        if (mToolbar != null) {
//            setSupportActionBar(mToolbar);
//            mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
//        }
//        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
//        mOfflineIndicator = findViewById(R.id.header_border_frame);
//        SFFragment sfFragment = getInitialFragment();
//        if (sfFragment != null && savedInstanceState == null) {
//            installFragment(sfFragment, true);
//        }
        mMainContainer = findViewById(R.id.tcl_container);
        BaseFragment baseFragment = getInitialFragment();
        if (baseFragment != null && null == savedInstanceState) {
            installFragment(baseFragment, true);
        }
    }

    /**
     * place the initial fragment inside here for better handling fragment
     * put null if there is not fragment needed
     */
    public abstract
    @Nullable
    BaseFragment getInitialFragment();

    public
    @Nullable
    BaseFragment getCurrentFragment() {
        return (BaseFragment) getSupportFragmentManager().findFragmentById(R.id.tcl_container);
    }

    /**
     * install fragment into default main container
     */
    public void installFragment(@NonNull BaseFragment fragment, boolean toBackStack) {
        installFragment(R.id.tcl_container, fragment, toBackStack, Integer.MIN_VALUE);
    }

    /**
     * install fragment into default main container
     */
    public void installFragment(@NonNull BaseFragment fragment, boolean toBackStack, int delay) {
        installFragment(R.id.tcl_container, fragment, toBackStack, delay);
    }

    /**
     * install fragment into container
     */
    public void installFragment(@IdRes int viewId, @NonNull BaseFragment fragment, boolean toBackStack, int delay) {
        // when replace or install fragment, disable refresh
//        updateRefreshStatus(false);
        // to avoid duplicate fragment
        if (getCurrentFragment() == null || !getCurrentFragment().getTransactionTag().equals(fragment.getTransactionTag())) {
            if (delay > 0) {
                mFragmentController.installFragment(viewId, fragment, toBackStack, delay);
            } else {
                mFragmentController.installFragment(viewId, fragment, toBackStack);
            }
            mFragment = fragment;
//            updateHeader();
        }
    }

    public BaseFragment getFragment() {
        return mFragment;
    }

    /**
     * override if needs new layout
     */
    protected
    @LayoutRes
    int getLayoutId() {
        return R.layout.activity_base;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle sample_detail_navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_test:
                // TODO: 2017/07/30/030 test activity
                Toast.makeText(this, "Not available now", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_data:
                Intent data_intent = new Intent(this, DataActivity.class);
                startActivity(data_intent);
                break;
            case R.id.nav_prediction:
                // TODO: 2017/07/30/030 prediction
                Toast.makeText(this, "Not available now", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                Intent setting_intent = new Intent(this, SettingsActivity.class);
                startActivity(setting_intent);
                break;
            case R.id.nav_about:
                Intent about_intent = new Intent(this, AboutActivity.class);
                startActivity(about_intent);
                break;
            default:
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //BaseActivityListener
    @Override
    @CallSuper
    public void invalidateUI() {
        Log.d(TAG, "invalidateUI: ");
        if (getCurrentFragment() != null) {
            getCurrentFragment().invalidateUI();
        }
    }

    /**
     * Override to set title on toolbar
     */
    @Override
    public void setTitle(@StringRes int id) {
        setTitle(getString(id));
    }

    @Override
    public void setTitle(@Nullable CharSequence string) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(string);
        }
    }

    /**
     * handle when click back button, last fragment will be pop
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");

//        UIUtils.hideKeyboard(this);

        // to handle custom fragment back press
        if (getCurrentFragment() != null && getCurrentFragment().onBackPressed()) {
            return;
        }

        if (!popCurrentFragment()) {
            super.onBackPressed();
        }
    }

    /**
     * pop current fragment
     *
     * @return true if fragment was popped, false otherwise
     */
    public boolean popCurrentFragment() {
        if (getFragmentManager() != null && getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return true;
        } else if (getSupportFragmentManager() != null && getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    //Permission Check
    @Override
    public void onPermissionGranted(@NonNull String... permission) {

    }

    @Override
    public void onPermissionDenied(@NonNull String permission) {

    }

    @Override
    public void onPermissionDisable(@NonNull String permission) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionHelper.PERMISSION_REQ_CODE) {
            PermissionHelper.onRequestPermissionResult(this, permissions, grantResults);
            if (getCurrentFragment() instanceof PermissionHelper.PermissionListener) {
                PermissionHelper.onRequestPermissionResult((PermissionHelper.PermissionListener) getCurrentFragment(), permissions, grantResults);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getCurrentFragment() instanceof TCLBaseFragment) {
            ((TCLBaseFragment) getCurrentFragment()).onActivityResult(requestCode, resultCode, data);
        }
    }
}
