package com.openapi.hllebiansdk;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import com.hoolai.open.fastaccess.channel.HoolaiApplication;
import com.stvgame.ysdk.business.YSDKApplication;


public class MyApplication extends YSDKApplication {
    public void onCreate() {
        super.onCreate();
        //HoolaiApplication.onApplicationCreate(this);
    }

    public void onTerminate() {
        super.onTerminate();
        //HoolaiApplication.onApplicationTerminate(this);
    }

    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        //HoolaiApplication.attachBaseContext(this, context);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //HoolaiApplication.onConfigurationChanged(this, newConfig);
    }

}

