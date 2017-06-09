package com.cml.cmlrecorder;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author：cml on 2017/6/8
 * github：https://github.com/cmlgithub
 */

public class PlayFragment extends BaseDialogFragment {


    private static final String POSITION = "position";
    private File mAudioFile;
    private TextView mFileName;
    private SeekBar mSeekBar;
    private TextView mCurrentProgressText;
    private FloatingActionButton mPlayOrPause;
    private TextView mDurationText;
    private MediaPlayer mediaPlayer;



    public static PlayFragment newInstance(int position){
        PlayFragment playFragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION,position);
        playFragment.setArguments(bundle);
        return playFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog =  super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.play_fragment, null);
        builder.setView(dialogView);
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        List<File> audioList = getAudioList();
        int position = getArguments().getInt(POSITION);
        mAudioFile = audioList.get(position);
        if(mAudioFile == null){
            return builder.create();
        }
        mFileName = (TextView) dialogView.findViewById(R.id.file_name_text_view);
        mCurrentProgressText = (TextView) dialogView.findViewById(R.id.current_progress_text_view);
        mDurationText = (TextView) dialogView.findViewById(R.id.file_length_text_view);
        mSeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar);
        mPlayOrPause = (FloatingActionButton) dialogView.findViewById(R.id.fab_play);

        String fileName = mAudioFile.getName();
        mFileName.setText(fileName);
        String duration = fileName.substring(fileName.indexOf("_") + 1, fileName.indexOf(".mp3"));
        if(!TextUtils.isEmpty(duration)){
            if(Utils.isNumerStr(duration)){//判断字符串是否只包含数字
                long minutes = TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(duration)) - TimeUnit.MINUTES.toSeconds(minutes);
                mDurationText.setText(String.format("%02d:%02d", minutes, seconds));
            }
        }
        mPlayOrPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playOrPauseAudio();
            }
        });

        return builder.create();
    }

    private void playOrPauseAudio() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mPlayOrPause.setImageResource(R.mipmap.ic_media_play);
            mediaPlayer.pause();
            return;
        }

        if(mediaPlayer != null){
            mediaPlayer.start();
            return;
        }

        mPlayOrPause.setImageResource(R.mipmap.ic_media_stop);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mAudioFile.getPath());
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mSeekBar.setMax(mediaPlayer.getDuration());
                    mediaPlayer.start();
                    handler.sendEmptyMessage(1);
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mCurrentProgressText.setText("00:00");
                    mPlayOrPause.setImageResource(R.mipmap.ic_media_play);
                    mSeekBar.setProgress(0);
                    handler.removeMessages(1);
                }
            });
        } catch (IOException e) {
            Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            updateSeekBar();
        }
    };

    private void updateSeekBar(){
        if(mediaPlayer == null){
            return;
        }
        int currentPosition = mediaPlayer.getCurrentPosition();
        mSeekBar.setProgress(currentPosition);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(minutes);
        mCurrentProgressText.setText(String.format("%02d:%02d", minutes, seconds));
        handler.sendEmptyMessage(1);
    }


    private List<File> getAudioList(){
        List<File> audioList = new ArrayList<>();
        String audioPathDirectory = MySharedPreferences.getAudioPath(mContext);
        if(!TextUtils.isEmpty(audioPathDirectory)){
            File[] allFiles = new File(audioPathDirectory).listFiles();
            for (File file : allFiles){
                if(file.getAbsolutePath().contains(".mp3")){
                    audioList.add(file);
                }
            }
        }
        return audioList;
    }

    @Override
    public void onPause() {
        super.onPause();
        playOrPauseAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
