package com.openapi.ane;

import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;

import com.adobe.air.AndroidActivityWrapper;
import com.adobe.air.LifeCycleWechat;
import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.adobe.fre.FREWrongThreadException;
import com.alibaba.fastjson.JSON;
import com.openapi.Constants;
import com.openapi.DeviceTools;
import com.openapi.ane.fun.BaseFunction;
import com.openapi.ane.wxapi.WXShareManager;
import com.openapi.util.NetUtils;
import com.openapi.util.SysUtil;
import com.openapi.util.UpdateSdkUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by mustangkong on 2016/12/30.
 * 函数映射class
 */
public class NativeExtensionContext extends FREContext implements LifeCycleWechat {

    private AndroidActivityWrapper activityWrapper;
    IWXAPI api;

    public NativeExtensionContext(String extensionName) {
        activityWrapper = AndroidActivityWrapper.GetAndroidActivityWrapper(); //ActivityWrapper
        activityWrapper.addActivityResultListener(this); // 监听onActivityResult方法
        activityWrapper.addActivityStateChangeListner(this); // 监听生命周期方法

        Log.i(Constants.tag, "NativeExtensionContext Creating context");
    }

    /**
     * 需要返回一个包含FREFunction的Map集合
     *
     * @return
     */
    @Override
    public Map<String, FREFunction> getFunctions() {
        Log.i(Constants.tag, "Creating function Map");
        Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
        functionMap.put(Constants.OPENAPI_ANE_INIT, openApiInit);
        functionMap.put(Constants.OPENAPI_ANE_GETDEVICEINFO, openApiGetDeviceInfo);
        functionMap.put(Constants.OPENAPI_ANE_DOWNLOAD, openApiGetdownload);


        functionMap.put(Constants.OPENAPI_ANE_REGWXAPI, openApiRegWXAPI);
        functionMap.put(Constants.OPENAPI_ANE_SHARETEXTWXAPI, openApiShareTextWXAPI);
        functionMap.put(Constants.OPENAPI_ANE_SHAREWEBPAGEWXAPI, openApiShareWebpageWXAPI);
        return functionMap;
    }


    /**
     * 微信分享webpage
     */
    private FREFunction openApiShareWebpageWXAPI = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            try {
                Integer scene = args[0].getAsInt();
                String url = args[1].getAsString();
                String title = args[2].getAsString();
                String description = args[3].getAsString();

                Log.i(Constants.tag, "openApiShareWebpageWXAPI ok " + scene + " : " + url + " : " + description);
//                ByteBuffer byteBuffer = this.getBitmapDataFromFREObject((FREBitmapData) args[4]);

                WXShareManager.get_instance().shareWebPage(api, scene, url, title, description, null);

            } catch (FRETypeMismatchException e) {
                e.printStackTrace();
            } catch (FREInvalidObjectException e) {
                e.printStackTrace();
            } catch (FREWrongThreadException e) {
                e.printStackTrace();
            }

            Log.i(Constants.tag, "openApiShareWebpageWXAPI ok 1");
            return null;
        }
    };

    /**
     * 微信分享text
     */
    private FREFunction openApiShareTextWXAPI = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            try {
                Integer scene = args[0].getAsInt();
                String title = args[1].getAsString();
                String content = args[2].getAsString();

                Log.i(Constants.tag, "openApiShareTextWXAPI ok " + scene + " : " + title);

                WXShareManager.get_instance().shareWXText(api, scene, title, content);

            } catch (FRETypeMismatchException e) {
                e.printStackTrace();
            } catch (FREInvalidObjectException e) {
                e.printStackTrace();
            } catch (FREWrongThreadException e) {
                e.printStackTrace();
            }

            Log.i(Constants.tag, "openApiShareTextWXAPI ok 1");
            return null;
        }
    };

    /**
     * 注册微信
     */
    private FREFunction openApiRegWXAPI = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            try {
                Constants.APP_ID = args[0].getAsString();
                Log.i(Constants.tag, "openApiRegWXAPI ok " + Constants.APP_ID);

                api = WXAPIFactory.createWXAPI(getActivity(), Constants.APP_ID, true);
                api.registerApp(Constants.APP_ID);

                Map<String, String> objectHashMap = new HashMap<>();
                objectHashMap.put("isInstall", String.valueOf(api.isWXAppInstalled()));
                objectHashMap.put("isSupport", String.valueOf(api.isWXAppSupportAPI()));

                dispatchEvent(Constants.OPENAPI_ANE_ISINSTALL_WX_CB, JSON.toJSONString(objectHashMap));
//                WXEntryActivity.getInstance().initWXAPI(getActivity(), api);

            } catch (FRETypeMismatchException e) {
                e.printStackTrace();
            } catch (FREInvalidObjectException e) {
                e.printStackTrace();
            } catch (FREWrongThreadException e) {
                e.printStackTrace();
            }

            Log.i(Constants.tag, "openApiRegWXAPI ok 1");
            return null;
        }
    };

    /**
     * Context结束后的操作
     */
    @Override
    public void dispose() {
        if (activityWrapper != null) {
            activityWrapper.removeActivityResultListener(this);
            activityWrapper.removeActivityStateChangeListner(this);
            activityWrapper = null;
        }

        Log.i(Constants.tag, "Dispose context");
    }

    /**
     * 初始化
     */
    private FREFunction openApiInit = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            Log.i(Constants.tag, "openApiInit ok");
            try {

//                DeviceTools.init(getActivity());
                openApiGetDeviceInfo.call(ctx, args);

            } catch (Exception e) {
                Log.i(Constants.tag, "openApiInit DeviceTools.init err");
            }

            return null;
        }
    };

    private FREFunction openApiGetDeviceInfo = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            Log.i(Constants.tag, "openApiGetDeviceInfo ok1");
            String url = "";
            String snid = "";
            String gameid = "";
            try {
                url = args[0].getAsString();    // 获取第一个参数
                snid = args[1].getAsString(); // 获取第二个参数 nApiInit", String.valueOf(result));
                gameid = args[2].getAsString();

            } catch (FRETypeMismatchException e) {
                e.printStackTrace();
            } catch (FREInvalidObjectException e) {
                e.printStackTrace();
            } catch (FREWrongThreadException e) {
                e.printStackTrace();
            }

            try {
                DeviceTools.init(getActivity());
            } catch (Exception e) {
                Log.i(Constants.tag, "openApiInit DeviceTools.init err");
            }

//            creative  分为 baidu  和  hoolaiysdk
            //http://127.0.0.1:80/tracking/?snid=999&gameid=999&metric=equipment&clientid=123&ip=192.168.31.241&ds=2016-09-22&jsonString={"userid":"023343455","udid":"9405aeb8d496e47b","equipment":"early","kingdom":"open_app","phylum":"1","classfield":"start","family":"abc","genus":"def","value":"45","simulator":"bluestacks2 v2.0.1.5622 官方版","creative":"100","client_version":"1.76","ratio":"1920*1080","phone":"SM-G9250","system":"5.1.1","eqDate":"2011-06-12","eqTime":"12:21:23","extra":"ram:128GB,CPU:810,download_from:yyb"}
            Map<String, String> params = new HashMap<String, String>();

            params.put("metric", "equipment");
            params.put("snid", snid);
            params.put("url", url);
            params.put("gameid", gameid);
            params.put("ip", NetUtils.getIp(getActivity()));
            params.put("ds", SysUtil.getSysDate());

            String json = JSON.toJSONString(DeviceTools.headers);
            params.put("jsonString", json);

            Log.i(Constants.tag, "openApiGetDeviceInfo ok jsonString:  " + json);

            String jsonResult = JSON.toJSONString(params);

            Log.i(Constants.tag, "openApiGetDeviceInfo ok jsonResult:  " + jsonResult);

            dispatchEvent(Constants.OPENAPI_ANE_GETDEVICEINFO_CB, jsonResult);
            Log.i(Constants.tag, "openApiGetDeviceInfo ok2");
//            HttpService httpService = new HttpService(20000, 12000, 3);
//            httpService.doGet(url, params);
            return null;
        }
    };

    /**
     * apk 下载
     */
    private FREFunction openApiGetdownload = new BaseFunction() {
        @Override
        public FREObject call(FREContext ctx, FREObject[] args) {
            Log.i(Constants.tag, "openApiGetdownload ok");
            try {
                // 版本更新处理
                UpdateSdkUtil.updateSdkVersion(getActivity(), "currentVersionCode2", "res", "appname");
            } catch (Exception e) {
                Log.i(Constants.tag, "openApiGetdownload err");
            }
            return null;
        }
    };

    // =============================生命周期==========================================

    /**
     * adrind与as通讯
     *
     * @param code
     * @param level
     */
    public void dispatchEvent(String code, String level) {

        try {
            dispatchStatusEventAsync(code, "" + level);
        } catch (Exception exception) {
            Log.e(Constants.tag, "dispatchStatusEventAsync:  ", exception);
        }
    }

    /**
     * onActivityResult方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(Constants.tag, "onActivityResult ######################uu");
    }

    /**
     * 生命周期变化
     *
     * @param activityState
     */
    @Override
    public void onActivityStateChanged(AndroidActivityWrapper.ActivityState activityState) {
        switch (activityState) {
            case RESUMED: // 生命周期onResume
                Log.i(Constants.tag, "onResume");
                break;
            case STARTED: // 生命周期onStart
                Log.i(Constants.tag, "onStart");
                break;
            case RESTARTED: // 生命周期onRestart
                Log.i(Constants.tag, "onRestart");
                break;
            case PAUSED: // 生命周期onPause
                Log.i(Constants.tag, "onPause");
                break;
            case STOPPED: // 生命周期onStop
                Log.i(Constants.tag, "onStop");
                break;
            case DESTROYED: // 生命周期onDestory
                Log.i(Constants.tag, "onDestory");
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        Log.i(Constants.tag, "onConfigurationChanged");
    }
}
