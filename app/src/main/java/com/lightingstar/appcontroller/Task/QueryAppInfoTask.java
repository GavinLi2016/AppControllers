package com.lightingstar.appcontroller.Task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.lightingstar.appcontroller.R;
import com.lightingstar.appcontroller.model.AppBasicInfo;
import com.lightingstar.appcontroller.util.ForbiddentAppInfoUtil;

import java.util.ArrayList;
import java.util.List;

public class QueryAppInfoTask implements IMyAsyncTask {
    List<AppBasicInfo> appBasicInfos;

    OnTaskListener taskListener;

    public QueryAppInfoTask(Context context, OnTaskListener taskListener){
        this.taskListener = taskListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public List<AppBasicInfo> doAsyncTask(Context context) {
        //读取磁盘内的配置信息
        ForbiddentAppInfoUtil.initForbiddentPackages();
        List<AppBasicInfo> appInfos = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        // Return a List of all packages that are installed on the device.
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppBasicInfo appInfo = new AppBasicInfo();
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();

                appInfo.setPackageName(packageInfo.packageName); //获取应用包名，可用于卸载和启动应用
                appInfo.setName(appName);//获取应用版本名
                appInfo.setVersion(packageInfo.versionName);//获取应用版本号
                try {
                    Drawable appIcon = context.getPackageManager()
                            .getApplicationIcon(packageInfo.packageName);
                    appInfo.setAppIcon(appIcon);
                    //根据磁盘存储的禁用app信息设置checkbox
                    if (ForbiddentAppInfoUtil.getForbiddentPackages().contains(packageInfo.packageName)){
                        appInfo.setChecked(true);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    appInfo.setAppIcon(context.getDrawable(R.drawable.ic_launcher));
                }
                appInfos.add(appInfo);

            } else {
                // 系统应用
            }
        }

        return appInfos;
    }

    @Override
    public void setResult(Object result) {
        appBasicInfos = (List<AppBasicInfo>) result;
        taskListener.onTaskSuccess();
    }

    public List<AppBasicInfo> getAppBasicInfos(){
        return appBasicInfos;
    }
}
