package com.cml.cmlrecorder.fragment;

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
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cml.cmlrecorder.R;
import com.cml.cmlrecorder.fragment.base.BaseDialogFragment;
import com.cml.cmlrecorder.utils.Utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * author：cml on 2017/6/8
 * github：https://github.com/cmlgithub
 */

public class PlayFragment extends BaseDialogFragment {


    private static final String NAME = "name";
    private static final String PATH = "path";
    private static final String LENGTH = "length";
    private TextView mFileName;
    private SeekBar mSeekBar;
    private TextView mCurrentProgressText;
    private FloatingActionButton mPlayOrPause;
    private TextView mDurationText;
    private MediaPlayer mediaPlayer;
    private String mPath;


    public static PlayFragment newInstance(String name,String path,String length){
        PlayFragment playFragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putString(NAME,name);
        bundle.putString(PATH,path);
        bundle.putString(LENGTH,length);
        playFragment.setArguments(bundle);
        return playFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.play_fragment, null);
        builder.setView(dialogView);
        Bundle arguments = getArguments();
        String name = arguments.getString(NAME);
        mPath = arguments.getString(PATH);
        String length = arguments.getString(LENGTH);

        mFileName = (TextView) dialogView.findViewById(R.id.file_name_text_view);
        mCurrentProgressText = (TextView) dialogView.findViewById(R.id.current_progress_text_view);
        mDurationText = (TextView) dialogView.findViewById(R.id.file_length_text_view);
        mSeekBar = (SeekBar) dialogView.findViewById(R.id.seekBar);
        mPlayOrPause = (FloatingActionButton) dialogView.findViewById(R.id.fab_play);

        mFileName.setText(name+"");
        String duration = length;
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
        if(mediaPlayer != null && mediaPlayer.isPlaying()){//暂停播放
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//clear keep screen light on
            mPlayOrPause.setImageResource(R.mipmap.ic_media_play);
            mediaPlayer.pause();
            return;
        }

        if(mediaPlayer != null){//从暂停状态开始播放
            mediaPlayer.start();
            return;
        }

        //新的音频开始播放
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//keep screen light on
        mPlayOrPause.setImageResource(R.mipmap.ic_media_stop);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(mPath);
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
            Toast.makeText(mContext, e.toString()+"___"+getString(R.string.startFailed), Toast.LENGTH_SHORT).show();
            mPlayOrPause.setImageResource(R.mipmap.ic_media_play);
            dismiss();
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
