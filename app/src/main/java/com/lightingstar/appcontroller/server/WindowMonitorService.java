package com.lightingstar.appcontroller.server;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;

import com.lightingstar.appcontroller.activity.MainActivity;
import com.lightingstar.appcontroller.model.AppConstance;
import com.lightingstar.appcontroller.util.CommonUtil;
import com.lightingstar.appcontroller.util.DialogUtil;
import com.lightingstar.appcontroller.util.ForbiddentAppInfoUtil;
import com.lightingstar.appcontroller.util.LogUtil;

public class WindowMonitorService extends AccessibilityService {

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

            //PermissionMonitorUtil.setAccessibilityPassFlag(true);

            boolean flag = CommonUtil.moveToFront();
            if (!flag) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
            }
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
                if (DialogUtil.isShow()) {
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
                if (!ForbiddentAppInfoUtil.getForbiddentPackages().contains(packageName)) {
                    CommonUtil.recordAppRunningInfo(packageName);
                    return false;
                }
                CommonUtil.recordAppRunningInfo(packageName);

                LogUtil.info("send msg:", packageName);

                //MessageUtil.sendMessage(packageName, AppConstance.WIN_CHANGE_MSG);
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
                    DialogUtil.showAlertDialog("");
                }
            }
            catch (Exception e){
                LogUtil.info(null,e.getMessage());
            }
        }
    }

    @Override
    public void onInterrupt() {}
}
