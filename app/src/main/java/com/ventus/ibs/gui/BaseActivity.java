package com.ventus.ibs.gui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ventus.ibs.R;
import com.ventus.ibs.gui.about.AboutActivity;
import com.ventus.ibs.gui.data.DataActivity;
import com.ventus.ibs.gui.about.SettingsActivity;
import com.ventus.ibs.gui.interf.BaseActivityListener;
import com.ventus.ibs.util.PermissionHelper;
import com.ventus.ibs.util.UIUtils;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PermissionHelper.PermissionListener,
        BaseActivityListener,
        FragmentManager.OnBackStackChangedListener {
    private static final String TAG = "BaseActivity";
    protected FragmentController mFragmentController;
    protected Menu mMenu;
    protected CoordinatorLayout mCoordinatorLayout;

    protected FrameLayout mMainContainer;
    protected BaseFragment mFragment;
    protected Toolbar mToolbar;

    // header type is pending on fragment setting, or defined by child class
    private HeaderType mHeaderType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mFragmentController = new FragmentController(this, this);
        initUI(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //needed to reset correct header when user returns to app
        BaseFragment frag = getCurrentFragment();
        if (frag != null) {
            updateHeader(frag.getHeaderType(), frag.getHeaderIconType());
        }
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
        mToolbar = findViewById(R.id.toolbar);
        mCoordinatorLayout = findViewById(R.id.coordinator_layout);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        }
        mMainContainer = findViewById(R.id.tcl_container);
        BaseFragment baseFragment = getInitialFragment();
        if (baseFragment != null && null == savedInstanceState) {
            installFragment(baseFragment, true);
        }
    }

    // A method to find height of the status bar
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void setHeaderType(HeaderType headerType) {
        mHeaderType = headerType;
        updateHeader();
    }

    public void updateHeader(HeaderType headerType, HeaderIconType iconType){
        updateHeaderWithIconType(iconType);
        setHeaderType(headerType);
    }

    protected void updateHeader() {
        if (getSupportActionBar() != null && mHeaderType != null) {
            //reset visibility first
            mCoordinatorLayout.setVisibility(View.VISIBLE);
            mToolbar.setVisibility(View.VISIBLE);
            mMainContainer.setVisibility(View.VISIBLE);

            switch (mHeaderType) {
                case TITLE:
                    mCoordinatorLayout.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mToolbar.setVisibility(View.VISIBLE);
                    mMainContainer.setPadding(0, 0, 0, 0);
                    break;
                case BLANK:
                    if (mToolbar != null) {
                        mToolbar.setVisibility(View.GONE);
                    }
                    break;
                case BLANK_FITS_SYSTEM_WINDOW:
                    setTitle(null);
                    mToolbar.setVisibility(View.GONE);
                    mMainContainer.setPadding(0, 0, 0, 0);
                    break;
            }
        }

    }

    private void updateHeaderWithIconType(HeaderIconType iconType) {
        if (getSupportActionBar() != null && mToolbar != null) {
            int color = Color.parseColor("#CAE4FB");
            int iconId = iconType.getIcon();
            UIUtils.setToolbarBackIcon(mToolbar, iconId, color, PorterDuff.Mode.SRC_ATOP);
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

    @Override
    public void onBackStackChanged() {

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
