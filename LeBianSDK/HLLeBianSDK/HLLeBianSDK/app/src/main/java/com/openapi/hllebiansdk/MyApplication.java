package com.openapi.hllebiansdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.hoolai.open.fastaccess.channel.HoolaiApplication;


public class MyApplication extends Application {
    public void onCreate() {
        super.onCreate();
    }

    public void onTerminate() {
        super.onTerminate();
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}

