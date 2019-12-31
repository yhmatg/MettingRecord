package com.example.doodling.widget;

import android.app.Application;
import android.os.StrictMode;

import com.xuexiang.xlog.XLog;
import com.xuexiang.xlog.crash.CrashHandler;

public class DrawApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XLog.init(this);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        CrashHandler.getInstance().setOnCrashListener(new MyCrashListener());
    }
}
