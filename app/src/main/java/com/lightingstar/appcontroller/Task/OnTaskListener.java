package com.lightingstar.appcontroller.Task;

/**
* 定义回调接口
*/
public interface OnTaskListener {
    /**
     * 成功之后调用这个方法
     */
    void onTaskSuccess();

    /**
     * 失败之后调用这个方法
     */
    void onTaskFailed();
}
