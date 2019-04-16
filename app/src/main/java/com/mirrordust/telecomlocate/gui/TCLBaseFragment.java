package com.mirrordust.telecomlocate.gui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mirrordust.telecomlocate.util.PermissionHelper;

/**
 * Created by ventus0905 on 04/14/2019
 */
public abstract class TCLBaseFragment extends ViewLifeCycleFragment implements BaseFragment, PermissionHelper.PermissionListener {
    private static final String TAG = "TCLBaseFragment";
    protected static final String FRAGMENT_TOOLBAR_ICON = "FRAGMENT_TOOLBAR_ICON";
    private View mContentView;
    private Menu mMenu;
    private HeaderIconType mHeaderIconType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getBaseActivity() != null && getHeaderType() != null) {
            // as default set header type to title
//            getBaseActivity().updateHeader(getHeaderType(), getHeaderIconType());
            setTitle(null); // set as default to avoid title overlap on switching fragment
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContentView = inflater.inflate(getLayoutId(), container, false);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public HeaderIconType getHeaderIconType() {
//        return mHeaderIconType != null ? mHeaderIconType : HeaderIconType.HAMBURGER;
//    }

    /**
     * get fragment layout
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * check whether activity is shown when calling from Fragment
     *
     * @return true if fragment in actively installed
     */
    protected boolean isAlive() {
        return null != getActivity() && !getActivity().isFinishing() && isAdded();
    }

    /**
     * set activity title via fragment
     *
     * @param strId string resource id
     */
    public void setTitle(@StringRes int strId) {
        setTitle(getString(strId));
    }

    /**
     * set title, can set null to hide title
     *
     * @param title String
     */
    public void setTitle(@Nullable String title) {
        if (getActivity() instanceof BaseActivityListener) {
            getActivity().setTitle(title);
        }
    }

    /**
     * it's for getting a unique name for each fragment
     * do not use simple name
     *
     * @return completed name with path
     */
    @Override
    public String getTransactionTag() {
        return FragmentController.getClassTag(this.getClass());
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    /**
     * permission methods for requesting user permission
     *
     * @param permission String permission defined in manifest
     */
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
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        this.mMenu = menu;
        // clear for refresh menu when switching to a new fragment
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected");
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected " + "home");
                onBackPressed();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public @Nullable
    Menu getMenu() {
        return mMenu;
    }

    /**
     * invalidate fragment
     */
    @Override
    public void invalidateUI() {
        Log.d(TAG, "invalidateUI: ");
        if (mContentView != null) {
            mContentView.post(() -> mContentView.invalidate());
        }
    }

    public void invalidateOptionsMenu() {
        Activity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
        }
    }

    @Override
    public HeaderIconType getHeaderIconType() {
        return mHeaderIconType != null ? mHeaderIconType : HeaderIconType.HAMBURGER;
    }

    /**
     * pre-set the type of the header for display on the activity
     */
    @Nullable
    @Override
    public HeaderType getHeaderType() {
        return HeaderType.TITLE;
    }

    public @Nullable
    BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

    public void popCurrentFragment() {
        BaseActivity baseActivity = getBaseActivity();
        if (null != baseActivity) {
            baseActivity.popCurrentFragment();
        }
    }


}
