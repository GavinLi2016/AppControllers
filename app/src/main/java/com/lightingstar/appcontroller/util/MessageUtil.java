package com.lightingstar.appcontroller.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.lightingstar.appcontroller.MyApp;
import com.lightingstar.appcontroller.activity.MainActivity;
import com.lightingstar.appcontroller.model.AppConstance;

public class MessageUtil {
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String msgContent = msg.getData().get("data").toString();
            switch (msg.what){
                case AppConstance.WIN_CHANGE_MSG: {
                    DialogUtil.showAlertDialog(msgContent);
                    break;
                }
                case AppConstance.APP_CLOSE_MSG: {
                    Intent mBootIntent = new Intent(MyApp.getMyApplicationContext(), MainActivity.class);
                    //下面这句话必须加上才能开机自动运行app的界面
                    mBootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApp.getMyApplicationContext().startActivity(mBootIntent);
                    break;
                }
            }
        }
    };

    private final static Messenger serverMsger = new Messenger(handler);

    public static void sendMessage(String msgContent, int msgType){
        Message msg = new Message() ;
        msg.arg1 = 1 ;
        Bundle bundle = new Bundle() ;
        bundle.putString("data" , msgContent);
        msg.setData(bundle);
        msg.what = msgType;
        try {
            serverMsger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
