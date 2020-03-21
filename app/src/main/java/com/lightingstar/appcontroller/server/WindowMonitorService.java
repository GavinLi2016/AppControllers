package com.lightingstar.appcontroller.server;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.lightingstar.appcontroller.R;
import com.lightingstar.appcontroller.model.AppConstance;
import com.lightingstar.appcontroller.util.CommonUtil;
import com.lightingstar.appcontroller.util.LogUtil;

import java.util.ArrayList;

public class WindowMonitorService extends AccessibilityService {

    private AlertDialog dialog;
    private  boolean isShow = false;

    private ArrayList<String> forbiddentAppList = new ArrayList<>();

    ServiceBroadcastReceiver broadcastReceiver = new ServiceBroadcastReceiver();

    @Override
    protected void onServiceConnected() {
        try {
            super.onServiceConnected();
            LogUtil.info("server", "is running");
            //Configure these here for compatibility with API 13 and below.
            AccessibilityServiceInfo config = new AccessibilityServiceInfo();
            config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
            config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            if (Build.VERSION.SDK_INT >= 16) {
                //Just in case this helps
                config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
            }

            setServiceInfo(config);
            //初始化浮动窗口
            dialog = prepareDialog();
            //注册广播器
            IntentFilter filter = new IntentFilter(AppConstance.ACTION_COMMUNITY);
            filter.addAction(AppConstance.ACTION_COMMUNITY);
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);
        }
        catch (Exception e){
            LogUtil.info(null,e.getMessage());
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        try {
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                String packageName = event.getPackageName().toString();
                if (packageName == null) {
                    return;
                }
                if (isShow) {
                    return;
                }
                //LogUtil.info("change pakcage:",packageName);

                // 自己的应用
                if (packageName.equals(AppConstance.APP_PACKAGE_NAME)) {
                    return;
                }

                //窗口还是上次的app打开的
                if (packageName.equals(CommonUtil.getAppRuningInfo().getPackageName())) {
                    return;
                }

                CheckAppChangeTask sendTask = new CheckAppChangeTask();
                sendTask.execute(packageName);
            }
        }
        catch (Exception e){
            LogUtil.info(null,e.getMessage());
        }
    }

    private class CheckAppChangeTask extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                String packageName = params[0];
                //判断是否要禁止运行该App
                if (!forbiddentAppList.contains(packageName)) {
                    CommonUtil.recordAppRunningInfo(packageName);
                    return false;
                }
                CommonUtil.recordAppRunningInfo(packageName);

                LogUtil.info("send msg:", packageName);
                return true;
            }
            catch (Exception e){
                LogUtil.info(null,e.getMessage());

                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                if (result) {
                    isShow =true;
                    dialog.show();
                }
            }
            catch (Exception e){
                LogUtil.info(null,e.getMessage());
            }
        }
    }

    @Override
    public void onInterrupt() {}


    private AlertDialog prepareDialog(){
        AlertDialog dialog = new AlertDialog.Builder(getApplicationContext())
                .setMessage(R.string.content_warning)
                .setCancelable(false)
                .setPositiveButton(R.string.lab_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isShow = false;
                        dialog.dismiss();
                    }
                }).create();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        return dialog;
    }

    public class ServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            forbiddentAppList = intent.getStringArrayListExtra("data");
        }
    }
}
