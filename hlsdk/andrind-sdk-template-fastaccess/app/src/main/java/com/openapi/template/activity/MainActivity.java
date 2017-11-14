package com.openapi.template.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import com.hoolai.fastaccesssdk.fastaccess.FastaccessSdk;
import com.hoolai.fastaccesssdk.fastaccess.SendMessageCallBack;
import com.hoolai.open.fastaccess.channel.FastSdk;
import com.openapi.template.Constants;
import com.openapi.template.cb.ChannelInterfaceProxy;
import com.openapi.template.info.DeviceTools;
import com.openapi.template.util.UpdateSdkUtil;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

/**
 * 主unity activity
 */
public class MainActivity extends UnityPlayerActivity {

    Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        Log.i(Constants.tag, "unity3d onCreate 当前版本: " + DeviceTools.getVersionCode(this));

        Log.i(Constants.tag, "unity3d onCreate: ");

        FastaccessSdk.getInstance().init(this, Constants.tag, new SendMessageCallBack() {
            @Override
            public void sentMessage(String gameObject, String methodName, String msg) {
                this.sentMessage(gameObject, methodName, msg);
            }
        });

        // 接入开始
        FastSdk.onCreate(this, FastaccessSdk.getInstance().initCallbackImpl);
        // 获取当前SDK版本号接口
        String sdkVersion = FastSdk.getSdkVersion();
        Log.i(Constants.tag, "当前SDK版本号：" + sdkVersion);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FastSdk.onStart(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        FastSdk.onRestart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FastSdk.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FastSdk.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        FastSdk.onStop(this);
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            FastSdk.onDestroy(this);
        } catch (Exception e) {
            Log.e(Constants.tag, "onDestroy occured exception", e);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FastSdk.onConfigurationChanged(this, newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FastSdk.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        FastSdk.onNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FastSdk.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FastSdk.onBackPressed();
    }

    public void doLogin(final String paramString) {
        FastaccessSdk.getInstance().doLogin(paramString);
    }

    public void doPay(final int amount, final String itemName, final String callbackInfo, final String customParams) {
        FastaccessSdk.getInstance().doPay(amount, itemName, callbackInfo, customParams);
    }

    public void doLogout(final String paramString) {
        FastaccessSdk.getInstance().doLogout(paramString);
    }

    public void doExit(final String SDKListening, final String exitCallbackMethod) {
        FastaccessSdk.getInstance().doExit(SDKListening, exitCallbackMethod);
    }

    public void doGetServers(final String SDKListening, final String callbackMethod, final String version) {
        FastaccessSdk.getInstance().doGetServers(SDKListening, callbackMethod, version);
    }

    public void doSelectServer(final String SDKListening, final String callbackMethod, final String serverId) {
        FastaccessSdk.getInstance().doSelectServer(SDKListening, callbackMethod, serverId);
    }

    public void doSendBIData(final String SDKListening, final String callbackMethod, final String metric, final String jsonString) {
        FastaccessSdk.getInstance().doSendBIData(SDKListening, callbackMethod, metric, jsonString);
    }

    public void setExtData(final String json) {
        FastaccessSdk.getInstance().setExtData(json);
    }

    public void doInit(final String SDKListening, final String initCallback, final String loginCallback, final String payCallback, String onMaintenanceCallback) {
        FastaccessSdk.getInstance().doInit(SDKListening, initCallback, loginCallback, payCallback, onMaintenanceCallback);
    }

    public void releaseResource() {
        FastaccessSdk.getInstance().releaseResource();
    }

    public void showToast(final String paramString) {
        FastaccessSdk.getInstance().showToast(paramString);
    }

    public String getMainifestMetaData(String name) {
        return FastaccessSdk.getInstance().getMainifestMetaData(name);
    }

    public String getSdkVersion() {
        return FastaccessSdk.getInstance().getSdkVersion();
    }


//===================================================================================


//===================================================================================

    /**
     * 获取设备信息
     *
     * @return
     */
    public void openApiGetDeviceInfo(String gameObject, String methodName) {
        try {
            sentMessage(gameObject, methodName, DeviceTools.openApiGetDeviceInfo(this));
        } catch (Exception e) {
            Log.i(Constants.tag, e.getMessage());
        }
    }

    /**
     * apk 下载
     * JSONObject dynamicConfig = JSON.parseObject(initConfigResponse);
     * String update_version = dynamicConfig.getString("updateVersion");
     * Integer update_type = dynamicConfig.getInteger("updateType");
     * String update_url = dynamicConfig.getString("updateUrl");
     * String updateMsg = dynamicConfig.getString("updateMsg");
     */
    public void openApiGetdownload(String currentVersionCode, String configInfo, final String appName) {
        Log.i(Constants.tag, "openApiGetdownload ok");
        try {
//            String json = "{\"updateVersion\":\"2.1.0\",\"updateType\":\"2\",\"packageName\":\"com.hule.fishing\",\"updateUrl\":\"http://huleshikongres-10034783.cossh.myqcloud.com/download.apk\",\"updateMsg\":\"2.1.0\"}";
            // 版本更新处理
            UpdateSdkUtil.updateSdkVersion(this, currentVersionCode, configInfo, appName, new ChannelInterfaceProxy.ApplicationInitCallback() {
                @Override
                public void execute() {
                    Log.i(Constants.tag, "加载apk失败，参数不正确！！！！！");
                }
            });
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
    public void sentMessage(String gameObject, String methodName, String msg) {
        Log.i(Constants.tag, "sentMessage:  " + gameObject + " : " + methodName + "  :  " + msg);
        try {
            UnityPlayer.UnitySendMessage(gameObject, methodName, msg);
        } catch (Exception e) {
            Log.i(Constants.tag, e.getMessage());
        }
    }
}
