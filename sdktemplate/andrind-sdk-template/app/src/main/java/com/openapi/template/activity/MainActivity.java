package com.openapi.template.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.openapi.template.Constants;
import com.openapi.template.info.DeviceTools;
import com.openapi.template.util.UpdateSdkUtil;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

public class MainActivity extends UnityPlayerActivity {

    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        Log.i(Constants.tag, "unity3d onCreate 当前版本: " + DeviceTools.getVersionCode(this));
    }


//===================================================================================

    /**
     * 获取设备信息
     *
     * @return
     */
    public void openApiGetDeviceInfo(String gameObject, String methodName) {
        try {
            sentMessage(gameObject, methodName, DeviceTools.openApiGetDeviceInfo(this));
        }catch (Exception e){
            Log.i(Constants.tag, e.getMessage());
        }
    }

    /**
     * apk 下载
     * String res = JSON.parseObject(initConfigResponse).getString("value");
     * JSONObject dynamicConfig = JSON.parseObject(res);
     * String update_version = dynamicConfig.getString("updateVersion");
     * Integer update_type = dynamicConfig.getInteger("updateType");
     * String update_url = dynamicConfig.getString("updateUrl");
     * String updateMsg = dynamicConfig.getString("updateMsg");
     */
    public void openApiGetdownload() {
        Log.i(Constants.tag, "openApiGetdownload ok");
        try {
            // 版本更新处理
            UpdateSdkUtil.updateSdkVersion(this, "currentVersionCode2", "res", "appname");
        } catch (Exception e) {
            Log.i(Constants.tag, "openApiGetdownload err");
        }
    }

    //==================================================

    /**
     * unity3d与Android通讯接口
     *
     * @param name
     */
    public void StartActivity0(String name) {
        if (name == null || name.isEmpty()) return;
        Log.i(Constants.tag, name);
        // 参数1表示发送游戏对象的名称
        // 参数2表示对象绑定的脚本接收该消息的方法
        // 参数3表示本条消息发送的字符串信息，这个方法与IOS发送消息的方式非常相像
        sentMessage("MainGame", "GetMessage", "hi:  " + name);
    }

    /**
     * @param gameObject 参数1表示发送游戏对象的名称
     * @param methodName 参数2表示对象绑定的脚本接收该消息的方法
     * @param msg        参数3表示本条消息发送的字符串信息，这个方法与IOS发送消息的方式非常相像
     */
    public static void sentMessage(String gameObject, String methodName, String msg) {
        Log.i(Constants.tag, "sentMessage:  " + gameObject + " : " + methodName + "  :  " + msg);
        try {
            UnityPlayer.UnitySendMessage(gameObject, methodName, msg);
        } catch (Exception e) {
            Log.i(Constants.tag, e.getMessage());
        }
    }
}
