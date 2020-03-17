package com.lightingstar.appcontroller.util;

import android.util.Log;

public class LogUtil {
    static boolean logFlag = true;

    public static void info (String type,String txt){
        if (logFlag){
            if (type == null || type == ""){
                type = "info";
            }
            Log.i(type,txt);
        }
    }
}
