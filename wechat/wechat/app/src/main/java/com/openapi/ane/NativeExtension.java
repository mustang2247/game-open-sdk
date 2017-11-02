package com.openapi.ane;

import android.annotation.SuppressLint;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;
import com.openapi.Constants;

/**
 * Created by mustangkong on 2016/12/30.
 * ane 初始入口点
 * 在扩展描述符文件的 <initializer> 元素中指定类的完全限定名
 */
public class NativeExtension implements FREExtension {

    private static NativeExtensionContext context = null;

    @SuppressLint("LongLogTag")
    @Override
    public void initialize() {
        Log.i(Constants.tag, "Initialize");
        // nothing to initialize for this example
    }

    @Override
    public FREContext createContext(String s) {
        Log.i(Constants.tag, "Creating context");
        if( context == null) context = new NativeExtensionContext(Constants.tag);
        return context;
    }

    @Override
    public void dispose() {
        Log.i(Constants.tag, "Disposing extension");
//        context = null;
    }

    public static void callback(String fun, String msg) {
        Log.d(Constants.tag, "前 fun:" + fun + " msg: " + msg);
        if (msg == null) {
            msg = "";
        }
        context.dispatchEvent(fun, msg);
        Log.d(Constants.tag, "fun:" + fun + " msg: " + msg);
    }


}
