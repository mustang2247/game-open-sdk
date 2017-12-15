package com.openapi.hllebiansdk;

import android.content.Context;
import android.util.Log;

import com.excelliance.lbsdk.IQueryUpdateCallback;
import com.excelliance.lbsdk.LebianSdk;

/**
 * 乐变工具类
 */
public class LebianTools {

    public static final String tag = "openapiunity3d";
    private static Context m_context;

    public static void init(Context context){
        m_context = context;
    }

    public static void queryUpdate() {
        final IQueryUpdateCallback callBack = new IQueryUpdateCallback() {
            public void onUpdateResult(int result) {
                Log.i(tag, "IQueryUpdateCallback ok ");
            }
        };

        LebianSdk.queryUpdate(m_context, callBack, null);
    };

}
