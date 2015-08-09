package com.edonoxako.geophoto.app.backend.dbtasks;

import android.content.Context;


public abstract class BaseTask implements DBTask {

    protected Context context;

    public BaseTask(Context context) {
        this.context = context;
    }
}
