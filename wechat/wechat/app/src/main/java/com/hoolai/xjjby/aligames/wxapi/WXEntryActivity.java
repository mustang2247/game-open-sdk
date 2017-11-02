package com.hoolai.xjjby.aligames.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.openapi.Constants;
import com.openapi.ane.NativeExtension;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

//    private static WXEntryActivity Instance;
    //IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Constants.tag, "WXEntryActivity onCreate ok");

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID, false);

        //注意：
        //第三方开发者如果使用透明界面来实现WXEntryActivity，需要判断handleIntent的返回值，如果返回值为false，则说明入参不合法未被SDK处理，应finish当前透明界面，避免外部通过传递非法参数的Intent导致停留在透明界面，引起用户的疑惑
        try {
            api.handleIntent(this.getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.i(Constants.tag, "WXEntryActivity onCreate ok 1");

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i(Constants.tag, "WXEntryActivity onNewIntent ok");
        super.onNewIntent(intent);

        setIntent(intent);
        api.handleIntent(intent, this);
        Log.i(Constants.tag, "WXEntryActivity onNewIntent ok 1");
    }


    /**
     * 微信发送请求到第三方应用时，会回调到该方法
     *
     * @param req
     */
    @Override
    public void onReq(BaseReq req) {
        Log.i(Constants.tag, "WXEntryActivity onReq ok " + req.getType());

        Map<String, String> data = new HashMap<String, String>();
        data.put("type", String.valueOf(req.getType()));
        data.put("openId", req.openId);
        data.put("transaction", req.transaction);

        NativeExtension.callback(Constants.OPENAPI_ANE_WXAPI_REQ_CB, JSON.toJSONString(data));
        Log.i(Constants.tag, "WXEntryActivity onReq ok 1");

        this.finish();
    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
     *
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {
        Log.i(Constants.tag, "WXEntryActivity onResp ok " + resp.errCode);

        NativeExtension.callback(Constants.OPENAPI_ANE_WXAPI_RESP_CB, String.valueOf(resp.errCode));
        this.finish();
    }


}