package com.openapi.template.lebian.excelliance.open;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.excelliance.lbsdk.LBSdkHelper;
import com.openapi.template.lebian.openapi.hllebiansdk.MyApplication;

import java.lang.reflect.Field;

/* 请将YourApplicationClassName替换为你们自己的Application类名。
 * 如果在使用我们SDK之前没有自己的Application，强烈建议增加自己的Application类。
 * 特别注意：该文件除了替换这一个地方，别的地方不要改动。如需修改，请修改你们自己的Application类。
 *
 * Please replace YourApplicationClassName with your Application Class name.
 * It's strongly recommended to add your own Application Class if you don't have one yet.
 * NOTE:
 * Please remain this file untouched except replacing YourApplicationClassName.
 * Should you modify your own Application implementation in any case you want to.
 */
public class LBApplication extends MyApplication {
    private boolean exec = true;

    @Override
    public void onCreate() {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (exec || !GlobalSettings.USE_LEBIAN) {
            super.onCreate();
        } else {
            LBSdkHelper.getInstance(getBaseContext()).onCreate(this);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        try {
            Field mBase = ContextWrapper.class.getDeclaredField("mBase");
            mBase.setAccessible(true);
            mBase.set(this, base);
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlobalSettings.refreshState();
        if (GlobalSettings.USE_LEBIAN)
            exec = LBSdkHelper.getInstance(base).attachBaseContext(this, base);

        if (exec || !GlobalSettings.USE_LEBIAN) {
            try {
                Field mBase = ContextWrapper.class.getDeclaredField("mBase");
                mBase.setAccessible(true);
                mBase.set(this, null);

                super.attachBaseContext(base);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (exec || !GlobalSettings.USE_LEBIAN) {
            super.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (exec || !GlobalSettings.USE_LEBIAN) {
            super.onLowMemory();
        }
    }

    @Override
    public void onTerminate() {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (exec || !GlobalSettings.USE_LEBIAN) {
            super.onTerminate();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (exec || !GlobalSettings.USE_LEBIAN) {
            super.onTrimMemory(level);
        }
    }

    @Override
    public String getPackageName() {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        String pkgName = super.getPackageName();
        if ((pkgName != null && pkgName.length() > 0) || !GlobalSettings.USE_LEBIAN) {
            return pkgName;
        } else {
            return getBaseContext().getPackageName();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (super.getClassLoader() != null || !GlobalSettings.USE_LEBIAN) {
            return super.getClassLoader();
        } else {
            return getBaseContext().getClassLoader();
        }
    }
	
	@Override
    public Resources getResources() {
        // 切勿修改此方法！！请修改你们自己的Application类，如有疑问请和卓盟联系
        if (exec || !GlobalSettings.USE_LEBIAN) {
            return super.getResources();
        } else {
            return getBaseContext().getResources();
        }
    }
}
