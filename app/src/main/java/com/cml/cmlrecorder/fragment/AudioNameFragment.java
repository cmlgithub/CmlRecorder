package com.cml.cmlrecorder.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cml.cmlrecorder.R;
import com.cml.cmlrecorder.fragment.base.BaseDialogFragment;

/**
 * author：cml on 2017/6/8
 * github：https://github.com/cmlgithub
 */

public class AudioNameFragment extends BaseDialogFragment {

    public static AudioNameFragment newInstance(){
        return new AudioNameFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.audio_name_fragment, null);
        TextView oK = (TextView) dialogView.findViewById(R.id.okText);
        TextView giveUp = (TextView) dialogView.findViewById(R.id.giveUpText);
        final EditText newName = (EditText) dialogView.findViewById(R.id.newNameEditText);
        oK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mReturnAudioName != null){
                    String newNameStr = newName.getText().toString();
                    if(TextUtils.isEmpty(newNameStr)){
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.noName), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mReturnAudioName.returnAudioName(newNameStr);
                    dismiss();
                }
            }
        });
        giveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(dialogView);
        return builder.create();
    }

    public ReturnAudioName mReturnAudioName;
    public void setReturnAudioName(ReturnAudioName returnAudioName){
        mReturnAudioName = returnAudioName;
    }
    protected interface ReturnAudioName{
        void returnAudioName(String newName);
    }
}
