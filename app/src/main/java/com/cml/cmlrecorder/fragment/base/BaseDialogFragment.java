package com.cml.cmlrecorder.fragment.base;

import android.content.Context;
import android.support.v4.app.DialogFragment;

/**
 * author：cml on 2017/6/8
 * github：https://github.com/cmlgithub
 */

public abstract class BaseDialogFragment extends DialogFragment {
    protected Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
