package com.example.doodling.widget;

import android.app.Application;

import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.crash.CrashHandler;

public class DrawApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.init(this);
        CrashHandler.getInstance().setOnCrashListener(new MyCrashListener());
    }
}
