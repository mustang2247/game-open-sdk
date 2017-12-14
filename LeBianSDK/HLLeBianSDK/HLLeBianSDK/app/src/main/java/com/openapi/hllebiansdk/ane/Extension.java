package com.openapi.hllebiansdk.ane;

import android.annotation.SuppressLint;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

/**
 * Created by mustangkong on 2016/12/30.
 * ane 初始入口点
 * 在扩展描述符文件的 <initializer> 元素中指定类的完全限定名
 */
public class Extension implements FREExtension {

    private static final String EXT_NAME = "com.openapi.hllebiansdk.Extension";
    public static ExtensionContext context = null;
    public static final String tag = "openapiane";

    @SuppressLint("LongLogTag")
    @Override
    public void initialize() {
        Log.i(tag, "Initialize");
        // nothing to initialize for this example
    }

    @Override
    public FREContext createContext(String s) {
        Log.i(tag, "Creating context");
        if( context == null) context = new ExtensionContext(tag);
        return context;
    }

    @Override
    public void dispose() {
        Log.i(tag, "Disposing extension");
        context = null;
    }
}
