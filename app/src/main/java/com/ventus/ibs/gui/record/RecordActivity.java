package com.ventus.ibs.gui.record;

import android.support.annotation.Nullable;

import com.ventus.ibs.gui.BaseActivity;
import com.ventus.ibs.gui.BaseFragment;

/**
 * Created by ventus0905 on 05/05/2019
 */
public class RecordActivity extends BaseActivity {
    @Nullable
    @Override
    public BaseFragment getInitialFragment() {
        return RecordFragment.newInstance();
    }


}
