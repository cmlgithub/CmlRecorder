package com.cml.cmlrecorder.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;

/**
 * author：cml on 2017/6/12
 * github：https://github.com/cmlgithub
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final int DBVERSION = 1;

    public DBHelper(Context context) {
        super(context, "cmlRecorder.db", null, DBVERSION);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table audio(_id integer primary key autoincrement,name varchar,path varchar,length varchar )";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addData(DBHelper dbHelper,String path,String length){
        ContentValues values = new ContentValues();
        values.put("name",new File(path).getName()+"");
        values.put("path",path);
        values.put("length",length);
        dbHelper.getWritableDatabase().insert("audio",null,values);
    }

    public void deleteData(DBHelper dbHelper,String path){
        int id = dbHelper.getWritableDatabase().delete("audio", "path = ?", new String[]{path});
    }


}
