package com.cml.cmlrecorder;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

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

    @Override
    public void onCreate() {
        super.onCreate();
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
            Log.e("CML",e.toString()+"");
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
        MySharedPreferences.setAudioPath(this,fileDirectory);
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
        long duration = System.currentTimeMillis() - mStartingTimeMillis;
        File file = new File(mFilePath);
        if(file.exists()){
            String fileName = file.getName();
            StringBuilder stringBuilder = new StringBuilder(fileName);
            stringBuilder.insert(fileName.indexOf('.'),"_"+duration);
            String newFilePath = mFilePath.replace(mFilePath.substring(mFilePath.lastIndexOf("recorder"), mFilePath.length()),"")+stringBuilder.toString();
            MySharedPreferences.saveNeedRenameAudioPath(this,newFilePath);
            file.renameTo(new File(newFilePath));
        }
        mMediaRecorder.release();
        mMediaRecorder = null;
    }
}
