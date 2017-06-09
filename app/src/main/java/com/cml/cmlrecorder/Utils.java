package com.cml.cmlrecorder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author：cml on 2017/6/8
 * github：https://github.com/cmlgithub
 */

public class Utils {

    public static boolean isNumerStr(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
}
