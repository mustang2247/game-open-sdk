package com.openapi;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.openapi.template.activity.MainActivity;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by muskong on 2017/11/6.
 */

public class MainWechatActivity extends MainActivity {
    IWXAPI api;

    /**
     * 注册微信
     */
    private void openApiRegWXAPI(String gameObject, String methodName, String appid) {
        try {
            Constants.APP_ID = appid;
            Log.i(Constants.tag, "openApiRegWXAPI ok " + Constants.APP_ID);

            api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, true);
            api.registerApp(Constants.APP_ID);

            Map<String, String> objectHashMap = new HashMap<>();
            objectHashMap.put("isInstall", String.valueOf(api.isWXAppInstalled()));
            objectHashMap.put("isSupport", String.valueOf(api.isWXAppSupportAPI()));

            MainActivity.sentMessage(gameObject, methodName, JSON.toJSONString(objectHashMap));
        } catch (Exception e) {
            Log.i(Constants.tag, e.getMessage());
        }

        Log.i(Constants.tag, "openApiRegWXAPI ok 1");
    }

    /**
     * 微信分享webpage
     */
    private void openApiShareWebpageWXAPI(Integer scene, String url, String title, String description) {
        try {
            Log.i(Constants.tag, "openApiShareWebpageWXAPI ok " + scene + " : " + url + " : " + description);
//                ByteBuffer byteBuffer = this.getBitmapDataFromFREObject((FREBitmapData) args[4]);

            WXShareManager.get_instance().shareWebPage(api, scene, url, title, description, null);

        } catch (Exception e) {
            Log.i(Constants.tag, e.getMessage());
        }

        Log.i(Constants.tag, "openApiShareWebpageWXAPI ok 1");
    }

    /**
     * 微信分享text
     */
    private void openApiShareTextWXAPI(Integer scene, String title, String content) {
        try {
            Log.i(Constants.tag, "openApiShareTextWXAPI ok " + scene + " : " + title);

            WXShareManager.get_instance().shareWXText(api, scene, title, content);

        } catch (Exception e) {
            Log.i(Constants.tag, e.getMessage());
        }

        Log.i(Constants.tag, "openApiShareTextWXAPI ok 1");
    }

}
