package com.lightingstar.appcontroller.util;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lightingstar.appcontroller.MyApp;
import com.lightingstar.appcontroller.Task.MyAsyncTaskTemplate;
import com.lightingstar.appcontroller.model.AppConstance;
import com.lightingstar.appcontroller.model.AppRuningInfo;
import com.lightingstar.appcontroller.server.WindowMonitorService;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {

    private static AppRuningInfo appRuningInfo = new AppRuningInfo();

    public static AppRuningInfo getAppRuningInfo() {
        return appRuningInfo;
    }

    public static void recordAppRunningInfo(String newPackageName){
        /*AppRuningInfo lastAppRunningInfo = new AppRuningInfo();
        lastAppRunningInfo.setPackageName(CommonUtil.getAppRuningInfo().getPackageName());
        lastAppRunningInfo.setStartTime(CommonUtil.getAppRuningInfo().getStartTime());
        lastAppRunningInfo.setEndTime(System.currentTimeMillis());*/

        appRuningInfo.setPackageName(newPackageName);
        appRuningInfo.setStartTime(System.currentTimeMillis());
    }

    public static MyAsyncTaskTemplate getMyAsyncTaskTemplate() {
        return new MyAsyncTaskTemplate();
    }

    @TargetApi(11)
    public static boolean moveToFront() {
        boolean findBackendTaskFlag = false;
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            ActivityManager manager = (ActivityManager) MyApp.getInstanceRef().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> recentTasks = manager.getRunningTasks(Integer.MAX_VALUE);
            for (int i = 0; i < recentTasks.size(); i++){
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf(AppConstance.APP_PACKAGE_NAME) > -1) {
                    findBackendTaskFlag = true;
                    manager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                    break;
                }
            }
        }

        return findBackendTaskFlag;
    }

    public static void sendBroadcast(ArrayList<String> data){
        Intent intent = new Intent(AppConstance.ACTION_COMMUNITY);
        intent.setComponent(new ComponentName(AppConstance.APP_PACKAGE_NAME,
                WindowMonitorService.ServiceBroadcastReceiver.class.getName()));
        intent.putExtra("data",data);

        LocalBroadcastManager.getInstance(MyApp.getMyApplicationContext()).sendBroadcast(intent);
    }
}
