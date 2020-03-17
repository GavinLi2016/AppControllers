package com.lightingstar.appcontroller;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.lightingstar.appcontroller.util.sdkinit.ANRWatchDogInit;
import com.lightingstar.appcontroller.util.sdkinit.UMengInit;
import com.lightingstar.appcontroller.util.sdkinit.XBasicLibInit;
import com.lightingstar.appcontroller.util.sdkinit.XUpdateInit;

import java.lang.ref.WeakReference;

public class MyApp extends Application {
    /** 用来保存当前该Application的context */
    private static Context instance;
    /** 用来保存最新打开页面的context */
    private volatile static WeakReference<Context> instanceRef = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //解决4.x运行崩溃的问题
        MultiDex.install(this);
    }

    @Override public void onCreate() {
        super.onCreate();
        instance = this;
        //Stetho.initializeWithDefaults(this);

        this.initLibs();
    }

    /**
     * 初始化基础库
     */
    private void initLibs() {
        XBasicLibInit.init(this);

        XUpdateInit.init(this);

        //运营统计数据运行时不初始化
        if (!MyApp.isDebug()) {
            UMengInit.init(this);
        }

        //ANR监控
        ANRWatchDogInit.init();
    }

    /**
     * @return 当前app是否是调试开发模式
     */
    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static Context getInstanceRef(){
        Context context = instanceRef.get();
        if (context == null){
            return instance;
        }

        return context;
    }

    public static void setInstanceRef(Context context ){
        instanceRef = new WeakReference<>(context);
    }

    public  static Context getMyApplicationContext(){
        return instance;
    }

}
