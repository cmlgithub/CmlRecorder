package com.cml.cmlrecorder;

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


//    public UpdateView mUpdateView;
//    public void setUpdateView(UpdateView updateView){
//        mUpdateView = updateView;
//    }
//    protected interface UpdateView{
//        void updateView();
//    }

    public UpdateFile mUpdateFile;
    public void setUpdateFile(UpdateFile updateFile){
        mUpdateFile = updateFile;
    }
    protected interface UpdateFile{
        void updateFile();
    }
}
