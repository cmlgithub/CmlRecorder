package com.cml.cmlrecorder.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.cml.cmlrecorder.R;
import com.cml.cmlrecorder.db.DBHelper;
import com.cml.cmlrecorder.utils.MySharedPreferences;
import com.cml.cmlrecorder.utils.Utils;

import java.io.File;
import java.io.IOException;

/**
 * author：cml on 2017/6/6
 * github：https://github.com/cmlgithub
 */

public class RecorderService extends Service {

    public static final String FILENAME = "mFileName";
    public static final String FILEPATH = "mFilePath";
    private String mFileName;
    private String mFilePath = Environment.getExternalStorageDirectory().getPath()+"/CmlRecorder/";
    private String rootPath = Environment.getExternalStorageDirectory().getPath();
    private String defaultFilePath = rootPath +"/CmlRecorder/";
    private MediaRecorder mMediaRecorder;
    private long mStartingTimeMillis;
    private DBHelper mdbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        mdbHelper = new DBHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        dealAudioPath(intent);

        startRecorder();

        return START_STICKY;
    }

    private void startRecorder() {

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mFilePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioChannels(1);
        if(MySharedPreferences.getPrefHighQuality(this)){
            mMediaRecorder.setAudioSamplingRate(44100);
            mMediaRecorder.setAudioEncodingBitRate(192000);
        }
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();
        } catch (IOException e) {
            Utils.cmlLog(e.toString());
            Toast.makeText(this, getString(R.string.audio_failed), Toast.LENGTH_SHORT).show();
        }

    }

    private void dealAudioPath(Intent intent) {
        mFileName = intent.getStringExtra(FILENAME);
        if(TextUtils.isEmpty(mFileName)){
            mFileName = ""+System.currentTimeMillis();
        }
        mFileName = "recorder" + mFileName;
        mFilePath = intent.getStringExtra(FILEPATH);
        String fileDirectory = "";
        if(TextUtils.isEmpty(mFilePath)){
            fileDirectory = defaultFilePath;
            mFilePath = fileDirectory + mFileName + ".mp3";
        }else {
            fileDirectory = rootPath + "/" + mFilePath;
            mFilePath = fileDirectory + "/" + mFileName + ".mp3";
        }
        File file = new File(fileDirectory);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMediaRecorder != null){
            stopRecorder();
        }
    }

    private void stopRecorder() {
        mMediaRecorder.stop();
        mdbHelper.addData(mdbHelper,mFilePath,System.currentTimeMillis() - mStartingTimeMillis+"");
        mMediaRecorder.release();
        mMediaRecorder = null;
    }
}
