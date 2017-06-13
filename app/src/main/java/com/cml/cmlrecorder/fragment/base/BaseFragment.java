package com.cml.cmlrecorder.fragment.base;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * author：cml on 2017/6/7
 * github：https://github.com/cmlgithub
 */

public abstract class BaseFragment extends Fragment {

    protected Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }



}
