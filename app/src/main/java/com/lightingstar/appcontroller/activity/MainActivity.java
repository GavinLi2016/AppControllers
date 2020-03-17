package com.lightingstar.appcontroller.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.lightingstar.appcontroller.R;
import com.lightingstar.appcontroller.Task.OnTaskListener;
import com.lightingstar.appcontroller.Task.QueryAppInfoTask;
import com.lightingstar.appcontroller.adapter.AppViewListAdapter;
import com.lightingstar.appcontroller.core.BaseActivity;
import com.lightingstar.appcontroller.enums.OperationEnum;
import com.lightingstar.appcontroller.model.AppBasicInfo;
import com.lightingstar.appcontroller.util.CommonUtil;
import com.lightingstar.appcontroller.util.DialogUtil;
import com.lightingstar.appcontroller.util.FileOperationUtil;
import com.lightingstar.appcontroller.util.ForbiddentAppInfoUtil;
import com.lightingstar.appcontroller.util.PermissionMonitorUtil;
import com.lightingstar.appcontroller.util.PermissionsUtil;
import com.lightingstar.appcontroller.util.SqliteUtil;
import com.lightingstar.appcontroller.util.XToastUtils;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xui.utils.WidgetUtils;
import com.xuexiang.xui.widget.dialog.MiniLoadingDialog;
import com.xuexiang.xui.widget.textview.supertextview.SuperButton;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements OnTaskListener {
    @BindView(R.id.btn_save)
    SuperButton saveButton;
    @BindView(R.id.btn_exist)
    SuperButton existButton;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.listView)
    AbsListView listView;

    MiniLoadingDialog mMiniLoadingDialog;
    AppViewListAdapter mAdapter;
    OperationEnum operationEnum = OperationEnum.QUERY_APP;
    //权限判断类
    PermissionMonitorUtil permissionMonitorUtil = new PermissionMonitorUtil();
    //查找应用信息的Task
    QueryAppInfoTask queryAppInfoTask;
    //boolean iniFlag = false;

    private SqliteUtil databaseHelper;   //用于创建帮助器对象



    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean isSupportSlideBack() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionMonitorUtil.checkPermission(this);
        if (permissionMonitorUtil.isPermissionPassFlag()){
            this.initView();
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        permissionMonitorUtil.checkPermission(this);
        if (permissionMonitorUtil.isPermissionPassFlag() && !iniFlag){
            this.initView();
        }
    }*/

    /**
     * 初始化视图
     */
    public void initView(){
        //createDatabase();
        //startService(new Intent(getApplicationContext(), MonitorServer.class));
        mMiniLoadingDialog =  WidgetUtils.getMiniLoadingDialog(this);
        mMiniLoadingDialog.show();
        initListeners();
        if (queryAppInfoTask== null) {
            queryAppInfoTask = new QueryAppInfoTask(this.getBaseContext(), this);
            CommonUtil.getMyAsyncTaskTemplate().execute(queryAppInfoTask);
        }
    }

    public void createDatabase() {
        Map<String,String> createTablesSql = new HashMap<>();
        createTablesSql.put("app_controller_t","create table app_controller_t(packageName varchar(128))");
        databaseHelper = SqliteUtil.getInstance(this, "appcontroller", 1,createTablesSql);
        databaseHelper.getWritableDatabase();
    }

    /**
     * 权限回调事件
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //就多一个参数this
        PermissionsUtil.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    /**
     * 键盘事件
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 任务成功事件
     */
    @Override
    public void onTaskSuccess() {
        switch (operationEnum) {
            case QUERY_APP: {
                bindAppInfoData();
                break;
            }
            case SAVE_CONFIG:{
                FileOperationUtil.saveFile(ForbiddentAppInfoUtil.getForbiddentPackages());
                break;
            }
            case EXIST_APP:{
                this.finish();
                break;
            }
        }
    }

    /**
     * 绑定app数据
     */
    private void bindAppInfoData(){
        //iniFlag = true;
        mAdapter = new AppViewListAdapter(queryAppInfoTask.getAppBasicInfos());
        listView.setAdapter(mAdapter);

        //item 点击测试
        mAdapter.setOnItemClickListener(new SmartViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                CheckBox checkBox = itemView.findViewById(R.id.app_forbidden);
                AppBasicInfo appBasicInfo = mAdapter.getItem(position);
                if (!checkBox.isChecked()){
                    XToastUtils.toast("选中:" + position+":"+appBasicInfo.getName()+":"+appBasicInfo.getPackageName());
                    checkBox.setChecked(true);
                    appBasicInfo.setChecked(true);

                    ForbiddentAppInfoUtil.add(appBasicInfo.getPackageName());
                }
                else{
                    XToastUtils.toast("取消:" + position+":"+appBasicInfo.getName()+":"+appBasicInfo.getPackageName());
                    checkBox.setChecked(false);
                    appBasicInfo.setChecked(false);
                    ForbiddentAppInfoUtil.remove(appBasicInfo.getPackageName());
                }
            }
        });

        mMiniLoadingDialog.hide();
    }

    /**
     * 任务失败事件
     */
    @Override
    public void onTaskFailed() {

    }

    /**
     * 初始化监听器
     */
    protected void initListeners() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationEnum = OperationEnum.SAVE_CONFIG;
                DialogUtil.showConfirmDialog(MainActivity.this);
            }
        });
        existButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operationEnum = OperationEnum.EXIST_APP;
                DialogUtil.showConfirmDialog(MainActivity.this);
            }
        });
    }
}
