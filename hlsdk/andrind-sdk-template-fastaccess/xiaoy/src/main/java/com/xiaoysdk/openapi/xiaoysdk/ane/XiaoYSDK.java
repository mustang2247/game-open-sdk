package com.xiaoysdk.openapi.xiaoysdk.ane;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.stvgame.ysdk.bridge.LaunchParameter;
import com.stvgame.ysdk.business.SdkHelper;
import com.stvgame.ysdk.core.CheckUpdate;
import com.stvgame.ysdk.core.SDKCore;
import com.stvgame.ysdk.model.GameAccount;

import org.json.JSONObject;

/**
 * Created by mustangkong on 2016/12/30.
 * 函数映射class
 */
public class XiaoYSDK {

	private String tag = "openapiunity3d";
	Context mContext = null;

	enum InitStatus {
		INITING, SUCCESS, FAIL;
	}

	private InitStatus initStatus = InitStatus.INITING;

	Handler mHandler = new Handler();

	public static final int Type_Init_Fail = 1;
	public static final int Type_Init_Success = 2;
	public static final int Type_Login_Fail = 3;
	public static final int Type_Login_Success = 4;
	public static final int Type_Logout = 5;
	public static final int Type_Pay_Fail = 6;
	public static final int Type_Pay_Success = 7;
	public static final int Type_Exit_Channel = 8;
	public static final int Type_Exit_Game = 9;
	public static final int Type_GetServers_Success = 10;
	public static final int Type_GetServers_Fail = 11;
	public static final int Type_SelectServer_Success = 12;
	public static final int Type_SelectServer_Fail = 13;

	public String SDKListening;
	public String initCallback;
	public String loginCallback;
	public String payCallback;
	public String onMaintenanceCallback;


	public XiaoYSDKSendMessageCallBack callBack;
	public XiaoYSDKInterface sdkInterface;

	public static XiaoYSDK getInstance() {
		if (Instance == null) {
			Instance = new XiaoYSDK();
		}
		return Instance;
	}

	private static XiaoYSDK Instance = null;

	/**
	 * 初始化
	 */
	public void init(Context context, String tg, XiaoYSDKSendMessageCallBack callBack) {
		mContext = context;
		this.tag = tg;
		this.callBack = callBack;
		this.sdkInterface = new XiaoYSDKInterface();
		Log.i(tag, " 来了初始化！！！！！！！！！！~~~~~~~~~");
	}

	public String toResultData(int resultCode, XiaoYEntry entry) {
		com.alibaba.fastjson.JSONObject jo = new com.alibaba.fastjson.JSONObject();
		jo.put("resultCode", resultCode);
		if (entry != null) {
			jo.put(entry.getKey(), entry.getValue());
		}
		return jo.toJSONString();
	}

	private void login(){
		Log.i(tag, " doLogin0000！！！！！！！！！！~~~~~~~~~");
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(tag, " doLogin1111！！！！！！！！！！~~~~~~~~~");
				SdkHelper.doLogin();
			}
		});
	}

	private void exit(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				SdkHelper.userLogoutBySdk();
			}
		});
	}

	private void logout(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				SdkHelper.userLogoutBySdk();
			}
		});
	}

	private void pay(final int amount, final String itemName, final String callbackInfo, final String customParams){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				//JSONObject jsonObject = new JSONObject(itemName);
				String[] info = callbackInfo.split("#");

				Log.i("openapiunity3d", " pay！！！！！！！！！！~~~~~~~~~");
				SdkHelper.xiaoyPay(info[0],customParams,amount+"",info[2],itemName,info[1]);

			}
		});
	}

	private void initSDK(){
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Log.i(tag, " 来了initSDK！！！！！！！！！！~~~~~~~~~");
				//版本更新入口 应保证在游戏主activity里只初始化一次 appid 为小y为游戏分配的游戏id，如有疑问请联系我们
				CheckUpdate.getInstance().init(mContext, new CheckUpdate.CheckUpdateCallback() {
					@Override
					public void checkComplete() {
						//插件启动入口
						Log.i(tag, " 插件启动入口！！！！！！！！！！~~~~~~~~~");
						LaunchParameter parameter = new LaunchParameter((Activity) mContext);
						parameter.setSdkInterface(sdkInterface);
						SDKCore.main(parameter, new SDKCore.LaunchListener() {
							@Override
							public void onComplete() {
								//启动插件成功
								//SdkHelper.onSceneChange("loginui");
								Log.i(tag, " 启动插件成功！！！！！！！！！！~~~~~~~~~");

								callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Success, new XiaoYEntry("data", Type_Init_Success)));
							}

							@Override
							public void onFail() {
								Log.i(tag, " 启动插件失败！！！！！！！！！！~~~~~~~~~");
								callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Fail, new XiaoYEntry("data", Type_Init_Fail)));
							}
						});
					}
				});
			}
		});
	}



	public void doLogin(final String paramString) {
		Log.i(tag, " doLogin2222222！！！！！！！！！！~~~~~~~~~");
		login();
	}

	public void doSetXiaoYAccount(final String userId, final String userName) {
		Log.i(tag, " doSetXiaoYAccount！！！！！！！！！！~~~~~~~~~");
		SdkHelper.setGameAccount(new GameAccount(userId, userName, "1"));
	}

	public void doPay(final int amount, final String itemName, final String callbackInfo, final String customParams) {
		pay(amount, itemName, callbackInfo, customParams);
	}

	public void doLogout(final String paramString) {
		logout();
	}

	public void doExit(final String SDKListening, final String exitCallbackMethod) {
		exit();
	}

	public void doGetServers(final String SDKListening, final String callbackMethod, final String version) {

	}

	public void doSelectServer(final String SDKListening, final String callbackMethod, final String serverId) {

	}

	public void doSendBIData(final String SDKListening, final String callbackMethod, final String metric, final String jsonString) {

	}

	public void setExtData(final String json) {

	}

	public void doInit(final String SDKListening, final String initCallback, final String loginCallback, final String payCallback, String onMaintenanceCallback) {
		this.SDKListening = SDKListening;
		this.initCallback = initCallback;
		this.loginCallback = loginCallback;
		this.payCallback = payCallback;
		this.onMaintenanceCallback = onMaintenanceCallback;
		initSDK();
	}
}
