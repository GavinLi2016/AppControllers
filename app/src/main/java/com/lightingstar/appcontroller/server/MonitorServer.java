package com.lightingstar.appcontroller.server;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.lightingstar.appcontroller.model.AppConstance;

public class MonitorServer extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(AppConstance.ACTION_RESTART);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setComponent(new ComponentName(AppConstance.APP_PACKAGE_NAME,
                BootBroadcastReceiver.class.getName()));

        sendBroadcast(intent);
        super.onDestroy();
    }
}
