package com.lightingstar.appcontroller.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lightingstar.appcontroller.server.WindowMonitorService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PermissionsUtil {
    private final int mRequestCode = 100;//权限请求码
    public static boolean showSystemSetting = true;

    private PermissionsUtil() {
    }

    private static PermissionsUtil permissionsUtils;
    private IPermissionsResult mPermissionsResult;

    public static PermissionsUtil getInstance() {
        if (permissionsUtils == null) {
            permissionsUtils = new PermissionsUtil();
        }
        return permissionsUtils;
    }

    /**
     * 辅助服务开启检查
     * @param mContext
     * @return
     */
    public boolean isAccessibilitySettingsOn(Context mContext){
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + WindowMonitorService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            LogUtil.info("info:", "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            LogUtil.info("info:", "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            LogUtil.info("info:", "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    LogUtil.info("info:", "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtil.info("info:", "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            LogUtil.info("info:", "***ACCESSIBILITY IS DISABLED***");
        }

        return false;

    }

    /**
     * 权限检查
     * @param context
     * @param permissions
     * @param permissionsResult
     */
    public void chekPermissions(Activity context, String[] permissions, @NonNull IPermissionsResult permissionsResult) {
        mPermissionsResult = permissionsResult;
        if (Build.VERSION.SDK_INT < 23) {
            //6.0才用动态权限
            permissionsResult.passPermissons();
            return;
        }

        //创建一个mPermissionList，逐个判断哪些权限未授予，未授予的权限存储到mPerrrmissionList中
        List<String> mPermissionList = new ArrayList<>();
        //逐个判断你要的权限是否已经通过
        for (int i = 0; i < permissions.length; i++) {
            if (!selfPermissionGranted(context,permissions[i])) {
                mPermissionList.add(permissions[i]);//添加还未授予的权限
            }
        }
        //申请权限
        if (mPermissionList.size() > 0) {//有权限没有通过，需要申请
            ActivityCompat.requestPermissions(context, permissions, mRequestCode);
        } else {
            //说明权限都已经通过，可以做你想做的事情去
            permissionsResult.passPermissons();
            return;
        }
    }

    public boolean selfPermissionGranted(Context context, String permission) {
        //boolean ret = true;
        if (permission.equals(Manifest.permission.SYSTEM_ALERT_WINDOW)){
            return checkFloatPermission(context);
        }
        return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                ret = context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                ret = PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        }*/
        //return ret;
    }

    /**
     * 浮动窗口权限判断
     * @param context
     * @return
     */
    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED;// || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }


    //请求权限后回调的方法 //参数： requestCode 是我们自己定义的权限请求码
    // 参数： permissions 是我们请求的权限名称数组
    // 参数： grantResults 是我们在弹出页面后是否允许权限的标识数组，数组的长度对应的是权限名称数组的长度，数组的数据0表示允许权限，-1表示我们点击了禁止权限
    public void onRequestPermissionsResult(Activity context, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean hasPermissionDismiss = false;
        //有权限没有通过
        if (mRequestCode == requestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;
                }
            }
            //如果有权限没有被允许
            if (hasPermissionDismiss) {
                if (showSystemSetting) {
                    DialogUtil.showForbiddenDialog(this.mPermissionsResult);
                } else {
                    mPermissionsResult.forbitPermissons();
                }
            } else {
                //全部权限通过，可以进行下一步操作。。。
                mPermissionsResult.passPermissons();
            }
        }
    }

    public interface IPermissionsResult {
        void passPermissons();

        void forbitPermissons();

        void checkAccessibilitySetting();
    }
}
