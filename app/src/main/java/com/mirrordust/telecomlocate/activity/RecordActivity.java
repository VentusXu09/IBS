package com.mirrordust.telecomlocate.activity;

import android.support.annotation.Nullable;

import com.mirrordust.telecomlocate.fragment.RecordFragment;
import com.mirrordust.telecomlocate.gui.BaseActivity;
import com.mirrordust.telecomlocate.gui.BaseFragment;

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
