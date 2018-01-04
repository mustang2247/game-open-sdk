package com.bdsdk.openapi.bdsdk.ane;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.duoku.platform.demo.single.PhoneUtil;
import com.duoku.platform.single.DKPlatform;
import com.duoku.platform.single.DKPlatformSettings;
import com.duoku.platform.single.DkErrorCode;
import com.duoku.platform.single.DkProtocolKeys;
import com.duoku.platform.single.callback.IDKSDKCallBack;
import com.duoku.platform.single.item.DKCMGBData;
import com.duoku.platform.single.item.DKCMMMData;
import com.duoku.platform.single.item.DKCpWoStoreData;
import com.duoku.platform.single.item.GamePropsInfo;
import com.duoku.platform.single.setting.DKSingleSDKSettings;
import com.duoku.platform.single.util.SharedUtil;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by mustangkong on 2016/12/30.
 * 函数映射class
 */
public class BaiDuSDK {

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


	private String mPropsId = "mPropsId";//百度游戏计费点ID
	private String mPrice = "mPrice";//计费点价格
	private String mTitle = "mTitle";//计费点名称
	private String thirdpay = "thirdpay";//只接入第三方支付（支付宝、微信支付等）的，将此值设为 qpfangshua
	private String mUserdata= "mUserdata";

	private BaiDuSDKSendMessageCallBack callBack;
	private IDKSDKCallBack loginlistener;

	public static BaiDuSDK getInstance() {
		if (Instance == null) {
			Instance = new BaiDuSDK();
		}
		return Instance;
	}

	private static BaiDuSDK Instance = null;

	/**
	 * 初始化
	 */
	public void init(Context context, String tg, BaiDuSDKSendMessageCallBack callBack) {
		mContext = context;
		this.tag = tg;
		this.callBack = callBack;
	}

    /**
     * 用户登陆
     */
    private void login() {
    	Log.i(tag, "login");
		DKPlatform.getInstance().invokeBDLogin((Activity) mContext, loginlistener);
    }
    /**
     * 切换账号
     */
    private void account() {
		DKPlatform.getInstance().invokeBDChangeAccount((Activity) mContext, loginlistener);
		Log.i(tag, "logout");
    }

    /**
     * 游戏暂停
     */
    private void pause() {
		DKPlatform.getInstance().bdgamePause((Activity) mContext, new IDKSDKCallBack()
			{
				@Override
				public void onResponse(String paramString) {
					Log.d("GameMainActivity","bggamePause success");}
			});
		Log.i(tag, "logout");
    }

    /**
     * 退出游戏
     */
    private void logout() {
		DKPlatform.getInstance().invokeBDChangeAccount((Activity) mContext, loginlistener);
		Log.i(tag, "logout");
    }

    /**
     * 游戏支付
     */
    private void pay(int amount, String itemName, String callbackInfo, String customParams) {

		try {

			Log.i(tag, itemName);
			JSONObject jsonObject = new JSONObject(itemName);
			Log.i(tag, itemName);
			GamePropsInfo propsFirst = new GamePropsInfo(jsonObject.getString(mPropsId), jsonObject.getString(mPrice), jsonObject.getString(mTitle),jsonObject.getString(mUserdata));
			propsFirst.setThirdPay(jsonObject.getString(thirdpay));
			DKPlatform.getInstance().invokePayCenterActivity((Activity) mContext,
					propsFirst,
					null,null, null,null,null,RechargeCallback);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.i(tag, "invokePay");
    };
    /**
     * 游戏退出
     */
    private void exit() {
		DKPlatform.getInstance().bdgameExit((Activity) mContext, new IDKSDKCallBack() {
			@Override
			public void onResponse(String paramString) {
				//是中国移动卡且咪咕已初始化
				if (PhoneUtil.isChinaMobile((Activity) mContext) && DKSingleSDKSettings.GAMEBASE_SDK_INIT_IS_OK ) {
					exitWithMigu();
				}else{
					exitGameDirectly();
				}
			}
		});
		Log.i(tag, "exit");
    }

    /**
     * 获取设备信息
     */
    private void baiduOpenApiGetDeviceInfo() {
//    	 try {
//             DeviceTools.init((Activity) mContext);
//         } catch (Exception e) {
//             Log.i(tag, "openApiInit DeviceTools.init err");
//         }
//        JSONObject params = new JSONObject();
//        try {
//			params.put("metric", "equipment");
//            //params.put("ip", NetUtils.getIp((Activity) mContext));
//            params.put("ds", SysUtil.getSysDate());
//
//            JSONObject obj = new JSONObject((Map) DeviceTools.headers) ;
//            String json = obj.toString();
//
//            params.put("jsonString", json);
//            Log.i(tag, "openApiGetDeviceInfo ok jsonString:  " + json);
//
//            String jsonResult = params.toString();
//            Log.i(tag, "openApiGetDeviceInfo ok jsonResult:  " + jsonResult);
//            Log.i(tag, "openApiGetDeviceInfo ok2");
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			 Log.e(tag, "openApiGetDeviceInfo err");
//		}
    }


 	// 初始化SDK
 	private void initSDK(){
 		//百度账号
 		DKPlatform.getInstance().invokeBDInitApplication(((Activity) mContext).getApplication());
 		Log.i("openapiunity3d", "百度账号 invokeBDInitApplication ");
 		Log.i(tag,"初始化SDK");
 		//回调函数
 		IDKSDKCallBack initcompletelistener = new IDKSDKCallBack(){
 			@Override
 			public void onResponse(String paramString) {
 				Log.d("GameMainActivity", paramString);
 				try {
 					JSONObject jsonObject = new JSONObject(paramString);
 					// 返回的操作状态码
 					int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
 					Log.d(tag, "返回的操作状态码:"+mFunctionCode);
 					//初始化完成
 					if(mFunctionCode == DkErrorCode.BDG_CROSSRECOMMEND_INIT_FINSIH){
 						initLogin();
						initAds();
						Log.d("openapiunity3d", DKPlatformSettings.SdkMode.SDK_PAY + "1111");
						callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Success, new BaiDuEntry("data", Type_Init_Success)));
 					}
 					else
 					{
						Log.d("openapiunity3d", DKPlatformSettings.SdkMode.SDK_PAY + "22222");
						callBack.sentMessage(SDKListening, initCallback, toResultData(Type_Init_Fail, new BaiDuEntry("data", Type_Init_Fail)));
 					}
 				} catch (Exception e) {
 					e.printStackTrace();
 				}
 			}
 		};

 		//参数为测试数据，接入时请填入你真实数据
 		DKCMMMData mmData = new DKCMMMData("9889988","WcdMUGCHVQfwGrOM72dygfTu");
 		DKCMGBData gbData = new DKCMGBData();
 		DKCpWoStoreData woData = new DKCpWoStoreData("001");
 		//初始化函数
		Log.d("openapiunity3d", DKPlatformSettings.SdkMode.SDK_PAY + "");
 		DKPlatform.getInstance().init((Activity) mContext, true, DKPlatformSettings.SdkMode.SDK_PAY,null,null,null,initcompletelistener);
 	}
 	//登陆初始化,接入百度账号功能的需要调用此接口
 		private void initLogin(){
 			Log.d(tag, "登陆初始化,接入百度账号功能的需要调用此接口");
 			//回调函数
 			 loginlistener = new IDKSDKCallBack(){
 				@Override
 				public void onResponse(String paramString) {
 					try {
 						JSONObject jsonObject = new JSONObject(paramString);
 						// 返回的操作状态码
 						int mFunctionCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_CODE);
 						Log.d(tag, "initLogin 返回的操作状态码:"+mFunctionCode);
 						
 						// 返回的百度uid，供cp绑定使用
 						String bduid = jsonObject.getString(DkProtocolKeys.BD_UID);
 						//dispatchEvent("IDKSDKCallBack  json:", paramString);
						com.alibaba.fastjson.JSONObject successObj = new com.alibaba.fastjson.JSONObject();
 						successObj.put("uid", bduid);
						successObj.put("plantformSdk", "baidusdk");
						successObj.put("userName", "baidusdk");
						successObj.put("customParams", "onLoginFailed");
						successObj.put("detail", mFunctionCode);
 						//登陆成功
 						if(mFunctionCode == DkErrorCode.DK_ACCOUNT_LOGIN_SUCCESS){
							callBack.sentMessage(SDKListening, loginCallback, toResultData(Type_Login_Success, new BaiDuEntry("data", successObj)));
 							//登陆失败
 						}else if(mFunctionCode == DkErrorCode.DK_ACCOUNT_LOGIN_FAIL){
							callBack.sentMessage(SDKListening, loginCallback, toResultData(Type_Login_Fail, new BaiDuEntry("data", successObj)));
 							//快速注册成功
 						}else if(mFunctionCode == DkErrorCode.DK_ACCOUNT_QUICK_REG_SUCCESS){

							callBack.sentMessage(SDKListening, loginCallback, toResultData(Type_Login_Success, new BaiDuEntry("data", successObj)));
 						}else {
 							//dispatchEvent(String.valueOf(mFunctionCode),"未处理");
 						}
 					} catch (Exception e) {
 						e.printStackTrace();
 					}
 				}
 			};
			DKPlatform.getInstance().invokeBDInit((Activity) mContext, loginlistener);
 		}

 		/**
 		 * 品宣接口初始化
 		 */
 		private void initAds(){
 			DKPlatform.getInstance().bdgameInit((Activity) mContext, new IDKSDKCallBack() {
 				@Override
 				public void onResponse(String paramString) {
 					Log.d("GameMainActivity","bggameInit success");
 				}
 			});
 		}
 		/**
 		 * 咪咕版本调用
 		 */
 		private void exitWithMigu() {
 			//咪咕基地退出接口 start
// 			try {
// 				GameInterface.exit(currentActivity, new GameInterface.GameExitCallback() {
// 					@Override
// 					public void onConfirmExit() {
// 						
// 						exitGameDirectly();
// 					}
// 					@Override
// 					public void onCancelExit() {
// 					}
// 				});
// 			}catch (Exception e){
// 				e.printStackTrace();
// 			}
 			//咪咕基地退出接口 end
 		}
 		/**
 		 * 非咪咕版本调用
 		 */
 		private void exitGameDirectly() {
			((Activity) mContext).finish();
 			android.os.Process.killProcess(android.os.Process.myPid());
 		}
 		/**
 		 * 支付处理过程的结果回调函数
 		 * */
 		IDKSDKCallBack RechargeCallback = new IDKSDKCallBack(){
 			@Override
 			public void onResponse(String paramString) {
 				// TODO Auto-generated method stub
 				Log.d("GamePropsActivity", paramString);
 				try {
 					JSONObject jsonObject = new JSONObject(paramString);
 					// 支付状态码
 					int mStatusCode = jsonObject.getInt(DkProtocolKeys.FUNCTION_STATUS_CODE);
 					
 					if(mStatusCode == DkErrorCode.BDG_RECHARGE_SUCCESS){
 						// 返回支付成功的状态码，开发者可以在此处理相应的逻辑
 						
 						// 订单ID
 						String mOrderId = null;
 						// 订单状态
 						String mOrderStatus = null;
 						// 道具ID
 						String mOrderProductId = null;
 						// 道具实际支付的价格
 						String mOrderPrice = null;
 						// 支付通道
 						String mOrderPayChannel = null;
 						//道具原始价格，若微信、支付宝未配置打折该值为空，
 						String mOrderPriceOriginal = null;
 						
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_ID)){						
 							mOrderId = jsonObject.getString(DkProtocolKeys.BD_ORDER_ID);	
 							SharedUtil.getInstance((Activity) mContext).saveString("payment_orderid", mOrderId);
 						}
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_STATUS)){
 							mOrderStatus = jsonObject.getString(DkProtocolKeys.BD_ORDER_STATUS);
 						}
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_PRODUCT_ID)){			
 							mOrderProductId = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRODUCT_ID);
 						}
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_PAY_CHANNEL)){						
 							mOrderPayChannel = jsonObject.getString(DkProtocolKeys.BD_ORDER_PAY_CHANNEL);
 						}
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_PRICE)){						
 							mOrderPrice = jsonObject.getString(DkProtocolKeys.BD_ORDER_PRICE);
 						}
 						//int mNum = Integer.valueOf(mOrderPrice) * 10;
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_PAY_ORIGINAL)){						
 							mOrderPriceOriginal = jsonObject.getString(DkProtocolKeys.BD_ORDER_PAY_ORIGINAL);
 						}
 						int mNum = 0;
 						if( "".equals(mOrderPriceOriginal) ||null==mOrderPriceOriginal){
 							mNum = Integer.valueOf(mOrderPrice) * 10;
 						}else{
 							mNum = Integer.valueOf(mOrderPriceOriginal) * 10;
 						}
 						String propsType = "1";
 						Toast.makeText((Activity) mContext, "道具购买成功!\n金额:"+mOrderPrice+"元", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Success, new BaiDuEntry("customParams", "onSuccess Pay ")));
// 						DemoRecordData data = new DemoRecordData(mOrderProductId, mOrderPrice, propsType, String.valueOf(mNum));
// 						DemoDBDao.getInstance(activity).updateRechargeRecord(data);
 						
 					}else if(mStatusCode == DkErrorCode.BDG_RECHARGE_USRERDATA_ERROR){
 						
 						Toast.makeText((Activity) mContext, "用户透传数据不合法", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new BaiDuEntry("customParams", "onFail Pay ")));
 						
 					}else if(mStatusCode == DkErrorCode.BDG_RECHARGE_ACTIVITY_CLOSED){
 						
 						// 返回玩家手动关闭支付中心的状态码，开发者可以在此处理相应的逻辑
 						Toast.makeText((Activity) mContext, "玩家关闭支付中心", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new BaiDuEntry("customParams", "onFail Pay ")));
 						
 					}else if(mStatusCode == DkErrorCode.BDG_RECHARGE_FAIL){ 
 						if(jsonObject.has(DkProtocolKeys.BD_ORDER_ID)){			
 							SharedUtil.getInstance((Activity) mContext).saveString("payment_orderid", jsonObject.getString(DkProtocolKeys.BD_ORDER_ID));
 						}
 						// 返回支付失败的状态码，开发者可以在此处理相应的逻辑
 						Toast.makeText((Activity) mContext, "购买失败", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new BaiDuEntry("customParams", "onFail Pay ")));
 						
 					} else if(mStatusCode == DkErrorCode.BDG_RECHARGE_EXCEPTION){ 
 						
 						// 返回支付出现异常的状态码，开发者可以在此处理相应的逻辑
 						Toast.makeText((Activity) mContext, "购买出现异常", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new BaiDuEntry("customParams", "onFail Pay ")));
 						
 					} else if(mStatusCode == DkErrorCode.BDG_RECHARGE_CANCEL){ 
 						
 						// 返回取消支付的状态码，开发者可以在此处理相应的逻辑
 						Toast.makeText((Activity) mContext, "玩家取消支付", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new BaiDuEntry("customParams", "onFail Pay ")));
 						
 					} else {
 						Toast.makeText((Activity) mContext, "未知情况", Toast.LENGTH_LONG).show();
						callBack.sentMessage(SDKListening, payCallback, toResultData(Type_Pay_Fail, new BaiDuEntry("customParams", "onFail Pay ")));
 						
 					}
 					
 				} catch (Exception e) {
 					// TODO: handle exception
 					e.printStackTrace();
 				}
 			}
 		};


	private String toResultData(int resultCode, BaiDuEntry entry) {
		com.alibaba.fastjson.JSONObject jo = new com.alibaba.fastjson.JSONObject();
		jo.put("resultCode", resultCode);
		if (entry != null) {
			jo.put(entry.getKey(), entry.getValue());
		}
		return jo.toJSONString();
	}



	public void doLogin(final String paramString) {
		if( paramString.length() > 0 )
		{
		}else{
		}
		login();
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
