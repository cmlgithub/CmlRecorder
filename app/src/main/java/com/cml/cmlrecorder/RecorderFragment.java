package com.cml.cmlrecorder;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

import java.io.File;

/**
 * author：cml on 2017/6/6
 * github：https://github.com/cmlgithub
 */

public class RecorderFragment extends BaseFragment implements View.OnClickListener {


    private FloatingActionButton mFloatingActionButton;
    private AudioNameFragment mAudioNameFragment;
    private Chronometer mChronometer;

    public static RecorderFragment newsInstance(){
        RecorderFragment recorderFragment = new RecorderFragment();
        Bundle bundle = new Bundle();
        recorderFragment.setArguments(bundle);
        return recorderFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recorder_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        mChronometer = (Chronometer) view.findViewById(R.id.chronometer);
        mFloatingActionButton.setOnClickListener(this);
        mAudioNameFragment = AudioNameFragment.newInstance();
        mAudioNameFragment.setReturnAudioName(new AudioNameFragment.ReturnAudioName() {
            @Override
            public void returnAudioName(String newName) {
                String needRenameAudioPath = MySharedPreferences.getNeedRenameAudioPath(mActivity);
                if(!TextUtils.isEmpty(needRenameAudioPath)){
                    File file = new File(needRenameAudioPath);
                    if(file.exists()){
                        String substring = needRenameAudioPath.substring(needRenameAudioPath.indexOf("recorder") + 8, needRenameAudioPath.indexOf("_"));
                        needRenameAudioPath = needRenameAudioPath.replace(substring,newName);
                        file.renameTo(new File(needRenameAudioPath));
                        MySharedPreferences.saveNeedRenameAudioPath(mActivity,"");
                    }
                }
            }
        });
    }
    private boolean isStartRecorder = true;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floatingActionButton:
                if(mActivity != null){
                    if(isStartRecorder){
                        mFloatingActionButton.setImageResource(R.mipmap.ic_media_stop);

                        //way 1 with fileName and filePath by self give
//                        Intent intent = new Intent(activity, RecorderService.class);
//                        intent.putExtra(RecorderService.FILEPATH,"cml");
//                        intent.putExtra(RecorderService.FILENAME,"cmlName");
//                        activity.startService(intent);

                        //way 2 with all default
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        mChronometer.start();
                        mActivity.startService(new Intent(mActivity, RecorderService.class));
                    }else {
                        mFloatingActionButton.setImageResource(R.mipmap.ic_mic_white_36dp);
                        mActivity.stopService(new Intent(mActivity,RecorderService.class));
                        mChronometer.stop();
                        mChronometer.setBase(SystemClock.elapsedRealtime());
                        FragmentTransaction fragmentTransaction = ((FragmentActivity) mActivity).getSupportFragmentManager().beginTransaction();
                        mAudioNameFragment.show(fragmentTransaction,"AudioNameFragment");
                    }
                    isStartRecorder = !isStartRecorder;
                }
                break;
        }
    }
}
