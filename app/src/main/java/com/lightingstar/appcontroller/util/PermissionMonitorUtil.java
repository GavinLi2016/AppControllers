package com.lightingstar.appcontroller.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.lightingstar.appcontroller.MyApp;
import com.lightingstar.appcontroller.R;

public class PermissionMonitorUtil implements PermissionsUtil.IPermissionsResult {
    public boolean isPermissionPassFlag() {
        return permissionPassFlag;
    }

    private boolean permissionPassFlag =false;
    private static boolean accessibilityPassFlag =false;
    private Activity mainActivity;


    /*public static boolean isAccessibilityPassFlag() {
        return accessibilityPassFlag;
    }

    public static void setAccessibilityPassFlag(boolean flag) {
         accessibilityPassFlag =flag;
    }*/

    /**
     * 检查权限是否开启
     */
    public void checkPermission(Activity activity) {
        mainActivity = activity;
        if (permissionPassFlag){
            //checkAccessibilitySetting();
            return;
        }
        //弹出框权限
        String[] permissions = new String[]{Manifest.permission.SYSTEM_ALERT_WINDOW};
        PermissionsUtil.showSystemSetting = true;//是否支持显示系统设置权限设置窗口跳转
        //这里的this不是上下文，是Activity对象！
        PermissionsUtil.getInstance().chekPermissions(activity, permissions, this);
    }

    @Override
    public void passPermissons() {
        permissionPassFlag = true;
        //checkAccessibilitySetting();
    }

    @Override
    public void forbitPermissons() {
        XToastUtils.toast(R.string.no_permission);
        mainActivity.finish();
    }

    @Override
    public void checkAccessibilitySetting() {
        if (accessibilityPassFlag) return;
        Context context = MyApp.getInstanceRef();

        if (!PermissionsUtil.getInstance().isAccessibilitySettingsOn(MyApp.getMyApplicationContext())){
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity(intent);
        }
        else{
            accessibilityPassFlag = true;
        }
    }
}
