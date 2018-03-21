package com.xiaoysdk.openapi.xiaoysdk.ane;

import android.app.Activity;
import android.util.Log;

import com.stvgame.ysdk.bridge.SDKInterface;
import com.stvgame.ysdk.business.SdkHelper;
import com.stvgame.ysdk.business.UMEvent;
import com.stvgame.ysdk.core.SDKCore;
import com.stvgame.ysdk.utils.ChannelUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.Observable;

/**
 * Created by JGLOR￥ on 2018/2/6.
 */

public class XiaoYSDKInterface implements SDKInterface {
    /**
     * 游客、微信、手机号登录成功后调用此方法，返回的用户名和token可以直接登录游戏
     * @param loginType
     * @param userName
     * @param accessToken
     * @param vMacNum
     * @param uuidInt
     */
    @Override
    public void loginCompleted(int loginType, String userName, String accessToken, int vMacNum, int uuidInt)
    {
        com.alibaba.fastjson.JSONObject successObj = new com.alibaba.fastjson.JSONObject();
        successObj.put("uid", userName + "");
        successObj.put("accessToken", accessToken);
        successObj.put("channel", ChannelUtils.getXiaoYChannel(XiaoYSDK.getInstance().mContext));
        XiaoYSDK.getInstance().callBack.sentMessage(XiaoYSDK.getInstance().SDKListening, XiaoYSDK.getInstance().loginCallback, XiaoYSDK.getInstance().toResultData(XiaoYSDK.Type_Login_Success, new XiaoYEntry("data", successObj)));
    }
    /**
     * 用户点击SDK中三方登录按钮调用此方法，登录成功后调用SdkHelper.doThirdLogin("account", "token")通知sdk
     * @param fastLogin
     * @param accessToken
     * @param vMacNum
     * @param uuidInt
     * @param userType
     * @param userName
     * @param userNickName
     */
    @Override
    public void thirdLogin(int fastLogin, String accessToken, int vMacNum, int uuidInt, int userType, String userName, String userNickName){

    }
    /**
     * 三方登录成功后回调此方法
     * @param isSuccess
     */
    @Override
    public void thirdLoginComplete(boolean isSuccess){

    }
    /**
     * 注销成功回调
     */
    @Override
    public void logout(){

    }
    /**
     * 调用注销登录接口的回调
     */
    @Override
    public void logoutSuccess(){
        Log.i("openapiunity3d", " logoutSuccess！！！！！！！！！！~~~~~~~~~");
        XiaoYSDK.getInstance().callBack.sentMessage(XiaoYSDK.getInstance().SDKListening, XiaoYSDK.getInstance().loginCallback, XiaoYSDK.getInstance().toResultData(XiaoYSDK.Type_Logout, new XiaoYEntry("customParams", "onSuccess")));
    }
    @Override
    public void logoutFailed(){

    }
    @Override
    public void onExit(){
        Log.i("openapiunity3d", " onExit！！！！！！！！！！~~~~~~~~~");
        SdkHelper.onKillProcess(XiaoYSDK.getInstance().mContext);
    }

    /**
     *
     * @param payType 0小y支付 1 三方支付
     * @param status （当payType== 0）status的值：0表示成功、 -1表示失败、 -2表示取消；
     * 				 （当payType== 1）status不做处理
     * @param message 成功的订单号 或者失败的信息
     */
    @Override
    public void payCallBack(int payType, int status, String message){
        if( payType == 0 )
        {
            if( status == 0 )
            {
                XiaoYSDK.getInstance().callBack.sentMessage(XiaoYSDK.getInstance().SDKListening, XiaoYSDK.getInstance().payCallback, XiaoYSDK.getInstance().toResultData(XiaoYSDK.Type_Pay_Success, new XiaoYEntry("customParams", "onSuccess Pay ")));
            }else if( status == -1 )
            {
                XiaoYSDK.getInstance().callBack.sentMessage(XiaoYSDK.getInstance().SDKListening, XiaoYSDK.getInstance().payCallback, XiaoYSDK.getInstance().toResultData(XiaoYSDK.Type_Pay_Fail, new XiaoYEntry("customParams", "onFail Pay ")));
            }else if( status == -2 )
            {
                XiaoYSDK.getInstance().callBack.sentMessage(XiaoYSDK.getInstance().SDKListening, XiaoYSDK.getInstance().payCallback, XiaoYSDK.getInstance().toResultData(XiaoYSDK.Type_Pay_Fail, new XiaoYEntry("customParams", "onFail Pay ")));
            }
        }
    }
    @Override
    public Observable getEventObservable(){
        return SDKCore.getEventObservable();
    }
    /**
     * SDK 中的统计锚点
     * @param key
     * @param value
     * @param map
     * @param du
     */
    @Override
    public void onEventValue(String key, String value, Map<String, String> map, int du){
        UMEvent.onEventValue(XiaoYSDK.getInstance().mContext,key,value,map,du);
    }

    @Override
    public void onPluginMessage(String message){
        if( gameObjectName == null || functionName == null )
        {
            Log.i("openapiunity3d", " gameObjectName   functionName~~~~~~~~~");
            return;
        }
        if( message == null )
        {
            Log.i("openapiunity3d", " message == null ~~~~~~~~~");
            return;
        }

        Log.i("openapiunity3d", " message 发送成功 ~~~~~~~~~");
        XiaoYSDK.getInstance().callBack.sentMessage(gameObjectName, functionName, message);
    }

    private static String gameObjectName = "XYSingletionRoot";
    private static String functionName = "pathData";

    public static String XiaoYDLLMessage(String data)
    {
        try {
            Log.i("openapiunity3d", "来了XiaoYDLLMessage ~~~~~~~~~");
            JSONObject json = new JSONObject(data);
            Log.i("openapiunity3d", "json  =  " + json.toString()+" ~~~~~~~~~");

            String action = json.getString("action");
            if("init".equalsIgnoreCase(action))
            {
                gameObjectName = json.getString("gameObjectName");
                functionName = json.getString("functionName");
            }else if("sceneChange".equalsIgnoreCase(action))
            {

               SdkHelper.onSceneChange(json.getString("message"));
                //SdkHelper.onSceneChange(json.getJSONObject("message").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "message";
    }
}
