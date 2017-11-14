package com.hoolai.fastaccesssdk.fastaccess;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoolai.open.fastaccess.channel.CommonCallback;
import com.hoolai.open.fastaccess.channel.ExitCallback;
import com.hoolai.open.fastaccess.channel.FastSdk;
import com.hoolai.open.fastaccess.channel.GetServerListCallback;
import com.hoolai.open.fastaccess.channel.InitCallback;
import com.hoolai.open.fastaccess.channel.OnMaintenanceCallback;
import com.hoolai.open.fastaccess.channel.PayCallback;
import com.hoolai.open.fastaccess.channel.PayParams;
import com.hoolai.open.fastaccess.channel.SelectServerCallback;
import com.hoolai.open.fastaccess.channel.ServerInfos;
import com.hoolai.open.fastaccess.channel.UserLoginResponse;
import com.hoolai.open.fastaccess.channel.bi.SendBICallback;
import com.hoolai.open.fastaccess.channel.callback.LoginCallback;
import com.hoolai.open.fastaccess.channel.util.LogUtil;
import com.hoolai.open.fastaccess.channel.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by muskong on 2017/11/2.
 */

public class FastaccessSdk {

    private String tag = "openapiunity3d";
    Context mContext = null;

    enum InitStatus {
        INITING, SUCCESS, FAIL;
    }

    private InitStatus initStatus = InitStatus.INITING;

    Handler mHandler = new Handler();

    private static final int Type_Init_Fail = 1;
    private static final int Type_Init_Success = 2;
    private static final int Type_Login_Fail = 3;
    private static final int Type_Login_Success = 4;
    private static final int Type_Logout = 5;
    private static final int Type_Pay_Fail = 6;
    private static final int Type_Pay_Success = 7;
    private static final int Type_Exit_Channel = 8;
    private static final int Type_Exit_Game = 9;
    private static final int Type_GetServers_Success = 10;
    private static final int Type_GetServers_Fail = 11;
    private static final int Type_SelectServer_Success = 12;
    private static final int Type_SelectServer_Fail = 13;

    private String SDKListening;
    private String initCallback;
    private String loginCallback;
    private String payCallback;
    private String onMaintenanceCallback;
    
    private SendMessageCallBack callBack;

    public static FastaccessSdk getInstance() {
        if (Instance == null) {
            Instance = new FastaccessSdk();
        }
        return Instance;
    }

    private static FastaccessSdk Instance = null;

    public void init(Context context, String tg, SendMessageCallBack callBack) {
        mContext = context;
        this.tag = tg;
        this.callBack = callBack;

//        // 接入开始
//        FastSdk.onCreate((Activity) mContext, FastaccessSdk.getInstance().initCallbackImpl);
    }

    // 初始化回调
    public InitCallback initCallbackImpl = new InitCallback() {

        @Override
        public void onInitSuccess(String s) {
            Log.d(tag, "onInitSuccess");
            initStatus = InitStatus.SUCCESS;
            if (!Strings.isNullOrEmpty(initCallback)) {
                callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Success, new Entry("data", FastSdk.getChannelInfo().getInitParams())));
            }
        }

        @Override
        public void onInitFail(String s) {
            Log.d(tag, "onInitFail");
            initStatus = InitStatus.FAIL;
            if (!Strings.isNullOrEmpty(initCallback)) {
                callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Fail, null));
            }
        }
    };
    // 登录回调
    private LoginCallback loginCallbackImpl = new LoginCallback() {
        @Override
        public void onLogout(Object... paramObject) {
//            if (paramObject == null || paramObject.length == 0 || !(paramObject[0] instanceof String)) {
//                if (paramObject == null){
//                    paramObject = new Object[]{};
//                }
//                paramObject[0] = "onLogout";
//            }
            Log.d(tag, "onLogoutSuccess start");
            callBack.sentMessage(SDKListening, loginCallback, toResultData(Type_Logout, new Entry("customParams", paramObject)));
            Log.d(tag, "onLogoutSuccess end");
        }

        @Override
        public void onLoginSuccess(UserLoginResponse userLoginResponse) {
            Log.d(tag, "登陆成功:" + JSON.toJSONString(userLoginResponse));
            try {
                JSONObject jo = new JSONObject();
                jo.put("nickName", userLoginResponse.getNickName());
                jo.put("uid", userLoginResponse.getUid() + "");
                jo.put("accessToken", userLoginResponse.getAccessToken());
                jo.put("channelId", FastSdk.getChannelInfo().getId() + "");
                jo.put("channel", userLoginResponse.getChannel());
                jo.put("productId", FastSdk.getChannelInfo().getProductId() + "");
                jo.put("channelUid", userLoginResponse.getChannelUid());
                jo.put("extendInfo", userLoginResponse.getExtendInfo());
//				jo.put("customParams", "onLoginSuccess");

                Log.d(tag, "onLoginSuccess");
                callBack.sentMessage(SDKListening, loginCallback, toResultData(Type_Login_Success, new Entry("data", jo)));
            } catch (Exception e) {
                Log.e(tag, "验证access出现异常", e);
            }
        }

        @Override
        public void onLoginFailed(String resultValue) {
            Log.d(tag, "登陆失败:" + resultValue);
            JSONObject jo = new JSONObject();
            jo.put("customParams", "onLoginFailed");
            jo.put("detail", resultValue);
            Log.d(tag, "onLoginFailed");
            callBack.sentMessage(SDKListening, loginCallback, toResultData(Type_Login_Fail, new Entry("data", jo)));
        }
    };
    // 支付回调
    private PayCallback payCallbackImpl = new PayCallback() {
        @Override
        public void onSuccess(String param) {
            Log.d(tag, "onPaySuccess");
            callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Success, new Entry("customParams", "onSuccess Pay " + param)));
        }

        @Override
        public void onFail(String param) {
            Log.d(tag, "onPayFail");
            callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new Entry("customParams", "onFail Pay " + param)));
        }
    };

    public void doLogin(final String paramString) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (Strings.isNullOrEmpty(onMaintenanceCallback)) {
                    FastSdk.login(mContext, paramString);
                } else {
                    FastSdk.login(mContext, paramString, new OnMaintenanceCallback() {

                        @Override
                        public void onMaintenance(String msg) {
                            callBack.sentMessage(SDKListening, onMaintenanceCallback, msg);
                        }
                    });
                }
            }
        });
    }

    public void doPay(final int amount, final String itemName, final String callbackInfo, final String customParams) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                PayParams payParams = new PayParams();
                payParams.setAmount(amount);
                payParams.setItemName(itemName);
                payParams.setCallbackInfo(callbackInfo);
                FastSdk.pay(mContext, payParams, payCallbackImpl);
            }
        });
    }

    public void doLogout(final String paramString) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                FastSdk.logout(mContext, paramString);
            }
        });
    }

    public void doExit(final String SDKListening, final String exitCallbackMethod) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "doExit：SDKListening=" + SDKListening + ",exitCallbackMethod=" + exitCallbackMethod);
                // 调用SDK退出功能
                FastSdk.exit(mContext, "", new ExitCallback() {
                    @Override
                    public void onExitSuccess(String s) {
                        Log.d(tag, "onExitSuccess start");
                        callBack.sentMessage(SDKListening, exitCallbackMethod, toResultData(Type_Exit_Channel, null));
                        Log.d(tag, "onExitSuccess end");
                    }

                    @Override
                    public void onCustomExit(final String s) {
                        Log.d(tag, "onCustomExit start");
                        callBack.sentMessage(SDKListening, exitCallbackMethod, toResultData(Type_Exit_Game, null));
                        Log.d(tag, "onCustomExit end");
                    }
                });
            }
        });
    }

    public void doGetServers(final String SDKListening, final String callbackMethod, final String version) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "doGetServers：SDKListening=" + SDKListening + ",callbackMethod=" + callbackMethod);
                FastSdk.getServerList(mContext, version, new GetServerListCallback() {

                    @Override
                    public void onSuccess(ServerInfos serverInfos) {
                        Log.d(tag, "onGetServersSuccess");
                        callBack.sentMessage(SDKListening, callbackMethod, toResultData(Type_GetServers_Success, new Entry("serverInfos", serverInfos)));
                    }

                    @Override
                    public void onFail(String code, String desc) {
                        Log.d(tag, "onGetServersFail");
                        callBack.sentMessage(SDKListening, callbackMethod, toResultData(Type_GetServers_Fail, new Entry("desc", desc)));
                    }
                });
            }
        });
    }

    public void doSelectServer(final String SDKListening, final String callbackMethod, final String serverId) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "doSelectServer：SDKListening=" + SDKListening + ",callbackMethod=" + callbackMethod);
                FastSdk.selectServer(mContext, serverId, new SelectServerCallback() {

                    @Override
                    public void onSuccess() {
                        Log.d(tag, "onSelectServersSuccess");
                        callBack.sentMessage(SDKListening, callbackMethod, toResultData(Type_SelectServer_Success, null));
                    }

                    @Override
                    public void onFail(String code, String desc) {
                        Log.d(tag, "onSelectServersFail");
                        callBack.sentMessage(SDKListening, callbackMethod, toResultData(Type_SelectServer_Fail, new Entry("desc", desc)));
                    }
                });
            }
        });
    }

    public void doSendBIData(final String SDKListening, final String callbackMethod, final String metric, final String jsonString) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "doSendBIData：SDKListening=" + SDKListening + ",callbackMethod=" + callbackMethod);
                FastSdk.getBiInterface(mContext).sendBIData(metric, jsonString, new SendBICallback() {

                    @Override
                    public void onResult(String message) {
                        Log.d(tag, "onSendBiData Result=" + message);
                        callBack.sentMessage(SDKListening, callbackMethod, message);
                    }
                });
            }
        });
    }

    public void setExtData(final String json) {
        mHandler.post(new Runnable() {

            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                Map<String, String> userExtData = null;
                try {
                    userExtData = JSON.parseObject(json, Map.class);
                } catch (Exception e) {
                    Log.e(tag, "setUserExtData:json=" + json, e);
                    showToast("数据扩展JSON格式错误!");
                    return;
                }
                Log.d(tag, "setExtData");
                FastSdk.setUserExtData(mContext, userExtData);
            }
        });
    }

    public void doInit(final String SDKListening, final String initCallback, final String loginCallback, final String payCallback, String onMaintenanceCallback) {
        this.SDKListening = SDKListening;
        this.initCallback = initCallback;
        this.loginCallback = loginCallback;
        this.payCallback = payCallback;
        this.onMaintenanceCallback = onMaintenanceCallback;

        Log.d(tag, "doInit：SDKListening=" + SDKListening + ",initCallback=" + initCallback + ",loginCallback=" + loginCallback + ",payCallback=" + payCallback);
        FastSdk.setLoginCallback(loginCallbackImpl);
        if (initStatus == InitStatus.SUCCESS) {
            callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Success, new Entry("data", FastSdk.getChannelInfo().getInitParams())));
        } else {
            callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Fail, null));
        }
    }

    public String getSpecialFunctionCodes() {
        // 因为Unity不支持map中的key为int类型，所以这里转成String
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<Integer, String> entry : FastSdk.getSpecialFunctionCodes().entrySet()) {
            map.put(entry.getKey() + "", entry.getValue());
        }
        String jsonStr = JSON.toJSONString(map);
        LogUtil.d("getSpecialFunctionCodes=" + jsonStr);
        return jsonStr;
    }

    public void invokeSpecialFunction(final String SDKListening, final int functionCode, String paramsJsonArray, String callbackJsonArray) {
        LogUtil.d("invokeSpecialFunction:functionCode=" + functionCode + ",paramsJsonArray=" + paramsJsonArray + ",callbackJsonArray=" + callbackJsonArray);
        JSONArray params = null;
        JSONArray callbacks = null;
        try {
            params = JSON.parseArray(paramsJsonArray);
        } catch (Exception e) {
        }
        try {
            callbacks = JSON.parseArray(callbackJsonArray);
        } catch (Exception e) {
        }

        if (params == null) {
            params = new JSONArray();
        }
        if (callbacks == null) {
            callbacks = new JSONArray();
        }

        final Object[] args = new Object[params.size() + callbacks.size()];
        int index = 0;
        for (Object object : params) {
            args[index++] = object;
        }
        for (final Object methodName : callbacks) {
            args[index++] = new CommonCallback() {

                @Override
                public void process(Object... objects) {
                    String jsonStr = JSON.toJSONString(objects);
                    callBack.sentMessage(SDKListening, (String) methodName, jsonStr);
                }
            };
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                FastSdk.invokeSpecialFunction(functionCode, args);
            }
        });
    }

    private String toResultData(int resultCode, Entry entry) {
        JSONObject jo = new JSONObject();
        jo.put("resultCode", resultCode);
        if (entry != null) {
            jo.put(entry.getKey(), entry.getValue());
        }
        return jo.toJSONString();
    }

    public void releaseResource() {
        mHandler.post(new Runnable() {
            public void run() {
                FastSdk.onStop(mContext);
                FastSdk.onDestroy(mContext);
                FastSdk.getChannelInterface().applicationDestroy(mContext);
            }
        });
    }

    public void showToast(final String paramString) {
        mHandler.post(new Runnable() {
            public void run() {
                Toast.makeText(mContext, paramString, Toast.LENGTH_LONG).show();
            }
        });
    }

    public String getMainifestMetaData(String name) {
        return FastSdk.getMetaDataConfig(mContext, name);
    }

    public String getSdkVersion() {
        return FastSdk.getSdkVersion();
    }

    public void androidKillProcess() {
        Log.d(tag, "androidKillProcess");
        ((Activity) mContext).moveTaskToBack(true);
        ((Activity) mContext).finish();
        // 退出程序
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        Process.killProcess(Process.myPid());
    }

    public void runOnAndroidUIThread(final String gameObject, final String callbackMethod) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                Log.d(tag, "runOnAndroidUIThread：SDKListening=" + gameObject + ",callbackMethod=" + callbackMethod);
                callBack.sentMessage(gameObject, callbackMethod, "");
            }
        });
    }
//
//    //===============================================
//
//    public void onStart() {
//        FastSdk.onStart(mContext);
//    }
//
//    public void onRestart() {
//        FastSdk.onRestart(mContext);
//    }
//
//    public void onResume() {
//        FastSdk.onResume(mContext);
//    }
//
//    public void onPause() {
//        FastSdk.onPause(mContext);
//    }
//
//    public void onStop() {
//        FastSdk.onStop(mContext);
//    }
//
//    public void onDestroy() {
//        FastSdk.onDestroy(mContext);
//    }
//
//    public void onConfigurationChanged(Configuration newConfig) {
//        FastSdk.onConfigurationChanged(mContext, newConfig);
//    }
//
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        FastSdk.onActivityResult(mContext, requestCode, resultCode, data);
//    }
//
//    public void onNewIntent(Intent intent) {
//        FastSdk.onNewIntent(intent);
//    }
//
//    public void onSaveInstanceState(Bundle outState) {
//        FastSdk.onSaveInstanceState(outState);
//    }
//
//    public void onBackPressed() {
//        FastSdk.onBackPressed();
//    }

}
