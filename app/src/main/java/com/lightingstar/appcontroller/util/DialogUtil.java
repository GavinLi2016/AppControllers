package com.lightingstar.appcontroller.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.lightingstar.appcontroller.MyApp;
import com.lightingstar.appcontroller.R;
import com.lightingstar.appcontroller.Task.OnTaskListener;
import com.lightingstar.appcontroller.activity.MainActivity;
import com.lightingstar.appcontroller.model.AppConstance;
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.edittext.verify.VerifyCodeEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DialogUtil {
    private static MaterialDialog alertDialog;
    private static MaterialDialog passWordDialog;
    private static MaterialDialog forbiddenDialog;
    private static OnTaskListener taskListener;
    private static boolean showFlag = false;
    private static PermissionsUtil.IPermissionsResult mPermissionsResult;

    public static boolean isShow() {
        return showFlag;
    }

    public static void showAlertDialog(String msg){
        if (alertDialog == null){
            alertDialog = prepareAlertDialog();
        }
        if (!showFlag) {
            showFlag = true;
            //alertDialog.setTitle(msg);
            alertDialog.show();
        }
    }

    private static MaterialDialog prepareAlertDialog(){
        final Context context = MyApp.getInstanceRef();
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .cancelable(false)
                .content(R.string.content_warning)
                .positiveText(R.string.lab_ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        showFlag = false;
                        dialog.dismiss();
                        if (!CommonUtil.getAppRuningInfo().getPackageName().equals(AppConstance.APP_PACKAGE_NAME)
                                && !CommonUtil.moveToFront()
                        ) {
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    }
                }).build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }else {
            dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }


        return dialog;
    }

    private static MaterialDialog prepareConfirmDialog(){
        final Context context = MyApp.getInstanceRef();
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .customView(R.layout.password_confirm, true)
                .title(R.string.hint_please_input_password)
                .positiveText(R.string.lab_ok)
                .negativeText(R.string.lab_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        VerifyCodeEditText vcetPassword = dialog.findViewById(R.id.vcet_password);
                        String password = vcetPassword.getInputValue();
                        vcetPassword.clearInputValue();
                        Date now = Calendar.getInstance().getTime();
                        String dateVal = new SimpleDateFormat("yyyyMMdd").format(now);
                        String compateWord = dateVal.substring(2);

                        if (password.equals(compateWord)){
                            taskListener.onTaskSuccess();
                        }
                        else{
                            XToastUtils.toast(R.string.hint_wrong_input_password);
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        VerifyCodeEditText vcetPassword = dialog.findViewById(R.id.vcet_password);
                        vcetPassword.clearInputValue();
                        dialog.dismiss();
                    }
                }).build();

       return dialog;
    }

    public static void showConfirmDialog(OnTaskListener callbackRef){
        taskListener = callbackRef;
        if (passWordDialog == null){
            passWordDialog = prepareConfirmDialog();
        }
        passWordDialog.show();
    }

    private static MaterialDialog prepareForbiddenDialog(){
        final Activity contextActivity = (Activity) MyApp.getInstanceRef();
        MaterialDialog dialog = new MaterialDialog.Builder(contextActivity)
                .cancelable(false)
                .content(R.string.no_permission_hint)
                .positiveText(R.string.config_button)
                .negativeText(R.string.lab_cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri packageURI = Uri.parse("package:" + AppConstance.APP_PACKAGE_NAME);
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        contextActivity.startActivity(intent);
                        contextActivity.finish();
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mPermissionsResult.forbitPermissons();
                        dialog.dismiss();
                    }
                })
                .build();

        return dialog;
    }

    public static void showForbiddenDialog(PermissionsUtil.IPermissionsResult permissionsResult){
        mPermissionsResult = permissionsResult;
        if (forbiddenDialog == null){
            forbiddenDialog = prepareForbiddenDialog();
        }
        forbiddenDialog.show();
    }
}
