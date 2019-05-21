package com.ventus.ibs.gui.interf;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

/**
 *  the common listeners to communicate between BaseActivity and BaseFragment
 * Created by ventus0905 on 04/14/2019
 */
public interface BaseActivityListener {
    /**
     *
     * fragment can call this to refresh activity
     */
    void invalidateUI();

    void setTitle(@StringRes int strId);

    void setTitle(@Nullable CharSequence string);


}
