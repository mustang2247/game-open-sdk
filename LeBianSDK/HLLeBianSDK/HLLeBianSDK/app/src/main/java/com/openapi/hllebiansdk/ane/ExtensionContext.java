package com.openapi.hllebiansdk.ane;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.excelliance.lbsdk.IQueryUpdateCallback;
import com.excelliance.lbsdk.LebianSdk;

import java.util.HashMap;
import java.util.Map;

import com.openapi.hllebiansdk.ane.fun.BaseFunction;

/**
 * Created by mustangkong on 2016/12/30.
 * 函数映射class
 */
public class ExtensionContext extends FREContext {
    private static final String CTX_NAME = "ExtensionContext";
    private String tag;

    public ExtensionContext(String extensionName) {
        tag = extensionName;
        Log.i(tag, "Creating context  " + extensionName);
    }

    @Override
    public Map<String, FREFunction> getFunctions() {
        Log.i(tag, "Creating function Map");
        Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
        functionMap.put( "queryUpdate", queryUpdate );
        return functionMap;
    }

    public String getIdentifier() {
        return tag;
    }

    @Override
    public void dispose() {
        Log.i(tag, "Dispose context");
    }

    private FREFunction queryUpdate = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            dispatchEvent("queryUpdate", "queryUpdate ok");
            final IQueryUpdateCallback callBack = new IQueryUpdateCallback() {
                public void onUpdateResult(int result) {
                    dispatchEvent("queryUpdate", String.valueOf(result));
                }
            };

            LebianSdk.queryUpdate(ctx.getActivity().getApplicationContext(), callBack, null);
            return null;
        }
    };


    public void dispatchEvent(String type, String data) {

        try {
            dispatchStatusEventAsync(type, "" + data);
        }
        catch (Exception exception) {
            Log.e(tag, "dispatchStatusEventAsync", exception);
        }
    }

}
