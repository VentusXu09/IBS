package com.mirrordust.telecomlocate.gui;

import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by ventus0905 on 04/12/2019
 */
public class FragmentController {
    private static final String TAG = "fragmentController";
    private BaseActivity baseActivity;

    public FragmentController(@NonNull BaseActivity activity, FragmentManager.OnBackStackChangedListener onBackStackChangedListener) {
        baseActivity = activity;
        baseActivity.getSupportFragmentManager().addOnBackStackChangedListener(onBackStackChangedListener);
    }

    /**
     * @param viewId      container view id
     * @param fragment    fragment to replace
     * @param toBackStack control back stack behavior
     */
    public void installFragment(@IdRes final int viewId, @NonNull final BaseFragment fragment, final boolean toBackStack) {
        installFragment(viewId, fragment, toBackStack, Integer.MIN_VALUE);
    }

    /**
     * @param viewId      container view id
     * @param fragment    fragment to replace
     * @param toBackStack control back stack behavior
     * @param delay       delay between closing the drawer and executing the fragment transaction
     */
    public void installFragment(@IdRes final int viewId, @NonNull final BaseFragment fragment, final boolean toBackStack, int delay) {
        if (isValid(fragment)) {
            if (fragment instanceof Fragment) {
                FragmentManager manager = baseActivity.getSupportFragmentManager();
                Fragment frag = manager.findFragmentById(viewId);
                if (frag == null) { // container is empty
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.add(viewId, (Fragment) fragment);
                    transaction.commitAllowingStateLoss();
                } else {
                    if (delay > 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                replaceFragment(viewId, fragment, toBackStack);
                            }
                        }, delay);
                    } else {
                        replaceFragment(viewId, fragment, toBackStack);
                    }
                }
            }
        } else {
            Log.e(TAG, "installFragment: failed");
        }
    }

    /**
     * replace current fragment with new fragment
     *
     * @param viewId   the view container to replace
     * @param fragment the fragment to install
     * @param toStack  control back stack behavior
     */
    public void replaceFragment(@IdRes int viewId, @NonNull BaseFragment fragment, boolean toStack) {
        if (isValid(fragment)) {
            if (fragment instanceof Fragment) {
                FragmentTransaction transaction = baseActivity.getSupportFragmentManager().beginTransaction();
                if (!toStack) {
                    transaction.remove(getInstalledFragment(viewId));
                    transaction.add(viewId, (Fragment) fragment);
                } else {
                    transaction.addToBackStack(((Fragment) fragment).getTag());
                    transaction.replace(viewId, (Fragment) fragment);
                }
                transaction.commitAllowingStateLoss();
            }
            // after replace the new fragment, refresh the menu
            baseActivity.invalidateOptionsMenu();
        } else {
            Log.d(TAG, "replaceFragment: failed, its not a valid fragment");
        }
    }

    public Fragment getInstalledFragment(@IdRes int viewId) {
        return baseActivity.getSupportFragmentManager().findFragmentById(viewId);
    }

    public void clearBackStack() {
        FragmentManager manager = baseActivity.getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            manager.popBackStack();
        }
    }

    /**
     * to get a unique tag for the class
     *
     * @param tClass
     * @return
     */
    public static String getClassTag(Class tClass) {
        return tClass.getName();
    }

    private boolean isValid(BaseFragment fragment) {
        return fragment instanceof Fragment;
    }
}
