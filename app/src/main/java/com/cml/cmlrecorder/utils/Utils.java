package com.cml.cmlrecorder.utils;

import android.util.Log;

import com.cml.cmlrecorder.BuildConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author：cml on 2017/6/8
 * github：https://github.com/cmlgithub
 */

public class Utils {

    public static final String TAG = "CML";
    public static final boolean isDebug = BuildConfig.IS_DEBUG;

    public static boolean isNumerStr(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    public static void cmlLog(String msg){
        if(isDebug && msg != null){
            Log.e(TAG,msg);
        }
    }
}
