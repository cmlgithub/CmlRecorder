package com.cml.cmlrecorder.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cml.cmlrecorder.R;
import com.cml.cmlrecorder.bean.AudioBean;
import com.cml.cmlrecorder.db.DBHelper;
import com.cml.cmlrecorder.fragment.base.BaseFragment;
import com.cml.cmlrecorder.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author：cml on 2017/6/6
 * github：https://github.com/cmlgithub
 */

public class RecFileFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private TextView mNoAudioFileTextView;
    private DBHelper mDbHelper;

    public static RecFileFragment newsInstance(){
        RecFileFragment recFileFragment = new RecFileFragment();
        Bundle bundle = new Bundle();
        recFileFragment.setArguments(bundle);
        return recFileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DBHelper(mActivity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recorder_file_fragment,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mNoAudioFileTextView = (TextView) view.findViewById(R.id.noAudioFileTextView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        updateData();
    }
    private List<AudioBean> mAudioFiles = new ArrayList<>();

    public void updateData(){
        if(mAudioFiles != null){
            mAudioFiles.clear();
        }

        String querySql = "select * from audio";
        Cursor audioDataCursor = mDbHelper.getWritableDatabase().rawQuery(querySql, null);
        while (audioDataCursor.moveToNext()){
            mAudioFiles.add(new AudioBean(audioDataCursor.getString(audioDataCursor.getColumnIndex("_id")),
                    audioDataCursor.getString(audioDataCursor.getColumnIndex("name")),
                    audioDataCursor.getString(audioDataCursor.getColumnIndex("path")),
                    audioDataCursor.getString(audioDataCursor.getColumnIndex("length"))));
        }

        if(mAudioFiles.size() == 0){
            return;
        }
        mNoAudioFileTextView.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mRecFileAdapter = new RecFileAdapter());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){//相当于onResume(),目的:及时更新数据
            updateData();
        }
    }
    private RecFileAdapter mRecFileAdapter ;

    class RecFileAdapter extends RecyclerView.Adapter<RecFileViewHolder>{

        @Override
        public RecFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecFileViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.card_view,parent,false));
        }

        @Override
        public void onBindViewHolder(RecFileViewHolder holder, final int position) {
            final AudioBean audioBean = mAudioFiles.get(position);
            if(audioBean != null){
                holder.mFile_name_text.setText(audioBean.name+"");
                String duration = audioBean.length;
                if(!TextUtils.isEmpty(duration)){
                    if(Utils.isNumerStr(duration)){//判断字符串是否只包含数字
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(duration));
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(duration)) - TimeUnit.MINUTES.toSeconds(minutes);
                        holder.mFile_length_text.setText(String.format("%02d:%02d", minutes, seconds));
                    }
                }
                long lastModified = new File(audioBean.path).lastModified();
                holder.mFile_date_added_text.setText(
                        DateUtils.formatDateTime(
                                mActivity,
                                lastModified,
                                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                        )
                );

                holder.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FragmentTransaction fragmentTransaction = ((FragmentActivity) mActivity).getSupportFragmentManager().beginTransaction();
                        PlayFragment.newInstance(audioBean.name,audioBean.path,audioBean.length).show(fragmentTransaction,PlayFragment.class.getSimpleName());
                    }
                });
                holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ArrayList<String> entrys = new ArrayList<String>();
                        entrys.add(mActivity.getString(R.string.dialog_file_share));
                        entrys.add(mActivity.getString(R.string.dialog_file_delete));

                        final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                        builder.setTitle(mActivity.getString(R.string.dialog_title_options));
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0) {
                                    shareFileDialog(position);
                                } if (item == 1) {
                                    deleteFileDialog(position);
                                }
                            }
                        });
                        builder.setCancelable(true);
                        builder.setNegativeButton(mActivity.getString(R.string.dialog_action_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        return true;
                    }
                });
            }



        }

        @Override
        public int getItemCount() {
            return mAudioFiles.size();
        }
    }

    public void shareFileDialog(int position) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mAudioFiles.get(position).path)));
        shareIntent.setType("audio/mp3");
        mActivity.startActivity(Intent.createChooser(shareIntent, mActivity.getText(R.string.dialog_file_share)));
    }


    public void deleteFileDialog (final int position) {
        // File delete confirm
        AlertDialog.Builder confirmDelete = new AlertDialog.Builder(mActivity);
        confirmDelete.setTitle(mActivity.getString(R.string.dialog_title_delete));
        confirmDelete.setMessage(mActivity.getString(R.string.dialog_text_delete));
        confirmDelete.setCancelable(true);
        confirmDelete.setPositiveButton(mActivity.getString(R.string.dialog_action_yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            //remove item from database, recyclerview, and storage
                            mDbHelper.deleteData(mDbHelper,mAudioFiles.get(position).path);
                            mAudioFiles.remove(position);
                            mRecFileAdapter.notifyItemRemoved(position);
                            new File(mAudioFiles.get(position).path).delete();
                        } catch (Exception e) {
                           Utils.cmlLog(e.toString());
                        }

                        dialog.cancel();
                    }
                });
        confirmDelete.setNegativeButton(mActivity.getString(R.string.dialog_action_no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = confirmDelete.create();
        alert.show();
    }



    class RecFileViewHolder extends RecyclerView.ViewHolder{

        CardView mCardView;
        TextView mFile_name_text ;
        TextView mFile_length_text ;
        TextView mFile_date_added_text ;

        public RecFileViewHolder(View itemView) {
            super(itemView);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
            mFile_name_text = (TextView) itemView.findViewById(R.id.file_name_text);
            mFile_length_text = (TextView) itemView.findViewById(R.id.file_length_text);
            mFile_date_added_text = (TextView) itemView.findViewById(R.id.file_date_added_text);
        }
    }



}
