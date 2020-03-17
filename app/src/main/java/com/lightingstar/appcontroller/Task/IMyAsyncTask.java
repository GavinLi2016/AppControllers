package com.lightingstar.appcontroller.Task;

import android.content.Context;

public interface IMyAsyncTask {
    Object doAsyncTask(Context context);

    void setResult(Object result);
}
