package com.openapi.template.device;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSON;
import com.openapi.template.Constants;
import com.openapi.template.DeviceTools;
import com.openapi.template.util.APNUtil;
import com.openapi.template.util.SysUtil;
import com.openapi.template.util.UpdateSdkUtil;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by mustangkong on 2016/12/30.
 * 函数映射class
 */
public class MainActivityContext extends UnityPlayerActivity {

    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(Constants.tag, "unity3d onCreate: ");
        mContext = this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(Constants.tag, "unity3d onNewIntent");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(Constants.tag, "unity3d onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(Constants.tag, "unity3d onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(Constants.tag, "unity3d onResume");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(Constants.tag, "unity3d onKeyDown: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i(Constants.tag, "unity3d onBackPressed");
    }

    /**
     * 获取设备信息
     * @return
     */
    public void openApiGetDeviceInfo(){
        Log.i(Constants.tag, "openApiGetDeviceInfo ok1");

        try{
            DeviceTools.init(this);
        }catch (Exception e){
            Log.i(Constants.tag, "openApiInit HttpServiceTools.init err");
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("metric", "equipment");
//        params.put("snid", snid);
//        params.put("url", url);
//        params.put("gameid", gameid);
        params.put("ip", APNUtil.getIp(this));
        params.put("ds", SysUtil.getSysDate());

        String json = JSON.toJSONString(DeviceTools.headers);
        params.put("jsonString", json);

        Log.i(Constants.tag, "openApiGetDeviceInfo ok jsonString:  " + json);

        String jsonResult = JSON.toJSONString(params);

        Log.i(Constants.tag, "unity3d openApiGetDeviceInfo: " + jsonResult);
        sentMessage("MainGame", "GetMessage", jsonResult);

    }

    /**
     * apk 下载
     *  String res = JSON.parseObject(initConfigResponse).getString("value");
     JSONObject dynamicConfig = JSON.parseObject(res);
     String update_version = dynamicConfig.getString("updateVersion");
     Integer update_type = dynamicConfig.getInteger("updateType");
     String update_url = dynamicConfig.getString("updateUrl");
     String updateMsg = dynamicConfig.getString("updateMsg");
     */
    public void openApiGetdownload() {
        Log.i(Constants.tag, "openApiGetdownload ok");
        try{
            // 版本更新处理
            UpdateSdkUtil.updateSdkVersion(this, "currentVersionCode2", "res", "appname");
        }catch (Exception e){
            Log.i(Constants.tag, "openApiGetdownload err");
        }
    }

    //==================================================

    /**
     * unity3d与Android通讯接口
     * @param name
     */
    public void StartActivity0(String name)
    {
        if (name == null || name.isEmpty()) return;
        Log.i(Constants.tag, name);
        // 参数1表示发送游戏对象的名称
        // 参数2表示对象绑定的脚本接收该消息的方法
        // 参数3表示本条消息发送的字符串信息，这个方法与IOS发送消息的方式非常相像
        sentMessage("MainGame", "GetMessage", "hi:  " + name);
//        Intent intent = new Intent(mContext,Activity0.class);
//        intent.putExtra("name", name);
//        this.startActivity(intent);
    }

    /**
     *
     * @param gameObject 参数1表示发送游戏对象的名称
     * @param methodName 参数2表示对象绑定的脚本接收该消息的方法
     * @param msg        参数3表示本条消息发送的字符串信息，这个方法与IOS发送消息的方式非常相像
     */
    public static void sentMessage(String gameObject, String methodName, String msg){
        try {
            UnityPlayer.UnitySendMessage(gameObject, methodName, msg);
        }catch (Exception e){
            Log.i(Constants.tag, e.getMessage());
        }
    }


}
