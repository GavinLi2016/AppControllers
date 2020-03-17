package com.lightingstar.appcontroller.Task;

import android.os.AsyncTask;

import com.lightingstar.appcontroller.MyApp;

public class MyAsyncTaskTemplate extends AsyncTask<IMyAsyncTask,Integer, Object> {
    IMyAsyncTask taskObject;
    @Override
    protected Object doInBackground(IMyAsyncTask... objects) {
        taskObject = objects[0];
        return taskObject.doAsyncTask(MyApp.getMyApplicationContext());
    }

    protected void onPostExecute(Object result) {
        taskObject.setResult(result);
    }
}
