package com.opensdktemplate.device;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.alibaba.fastjson.JSON;
import com.opensdktemplate.Constants;
import com.opensdktemplate.DeviceTools;
import com.opensdktemplate.util.APNUtil;
import com.opensdktemplate.util.SysUtil;
import com.opensdktemplate.util.UpdateSdkUtil;
import com.unity3d.player.UnityPlayerActivity;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by mustangkong on 2016/12/30.
 * 函数映射class
 */
public class NativeExtensionContext extends UnityPlayerActivity {

    public NativeExtensionContext() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String json = openApiGetDeviceInfo();

        Log.i(Constants.tag, "unity3d onCreate: " + json);
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
    private String openApiGetDeviceInfo(){
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
        return jsonResult;
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
    private void openApiGetdownload() {
        Log.i(Constants.tag, "openApiGetdownload ok");
        try{
            // 版本更新处理
            UpdateSdkUtil.updateSdkVersion(this, "currentVersionCode2", "res", "appname");
        }catch (Exception e){
            Log.i(Constants.tag, "openApiGetdownload err");
        }
    }
}
