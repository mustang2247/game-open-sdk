package com.openapi.template.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.open.commonlibs.okhttp.CommonOkHttpClient;
import com.open.commonlibs.okhttp.listener.DisposeDataHandle;
import com.open.commonlibs.okhttp.listener.DisposeDataListener;
import com.open.commonlibs.okhttp.request.CommonRequest;
import com.open.commonlibs.okhttp.request.RequestParams;
import com.openapi.template.Constants;
import com.openapi.template.utils.APNUtil;
import com.openapi.template.utils.SysUtil;

import org.openudid.OpenUdidManager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Request;

/**
 * http 服务工具类
 */
public class DeviceTools {

    public static Map<String, String> headers = new HashMap<String, String>();
    private static Boolean isInit = false;

    public static void reportDeviceInfo(Context context) {
        String data = openApiGetDeviceInfo(context);

        JSONObject pd = JSON.parseObject(data);

        JSONObject j1 = JSON.parseObject((String) pd.get("jsonString"));

        JSONObject ct = new JSONObject();
        ct.put("ip", pd.get("ip"));

        ct.put("channelid", "_default_");
        ct.put("deviceid", j1.get("imei"));
        ct.put("androidid", j1.get("udid"));
        ct.put("network", j1.get("networkType"));
        ct.put("os", j1.get("system"));


        ct.put("resolution", j1.get("ratio"));
        ct.put("extra", j1.get("extra"));
        ct.put("devicetype", j1.get("phone"));
        ct.put("clientV", j1.get("clientVersion"));

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appid", "asdfa");
        jsonObject.put("context", ct);

        RequestParams params = new RequestParams();
        params.put("data",jsonObject.toJSONString());

        Request request = CommonRequest.createGetRequest("http://bi.mybi.top:9000/track/install", params);
//        Log.i(Constants.tag, request.toString());
//        Log.i(Constants.tag, request.url().toString());

        CommonOkHttpClient.get(request, new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.i(Constants.tag, "openApiGetDeviceInfo ok " + responseObj);
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.i(Constants.tag, "openApiGetDeviceInfo ok " + reasonObj);
            }
        }));


        Request request1 = CommonRequest.createGetRequest("http://bi.mybi.top:9000/track/startup", params);
        CommonOkHttpClient.get(request1, new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.i(Constants.tag, "openApiGetDeviceInfo ok " + responseObj);
            }

            @Override
            public void onFailure(Object reasonObj) {
                Log.i(Constants.tag, "openApiGetDeviceInfo ok " + reasonObj);
            }
        }));
    }

    /**
     * 获取设备信息
     *
     * @return
     */
    public static String openApiGetDeviceInfo(Context context) {
        Log.i(Constants.tag, "openApiGetDeviceInfo ok1");

        try {
            init(context);
        } catch (Exception e) {
            Log.i(Constants.tag, "openApiInit HttpServiceTools.init err");
        }

        Map<String, String> params = new HashMap<String, String>();

        params.put("metric", "equipment");
        params.put("ip", "");//解决部分低端手机无法获取IP问题
        params.put("ds", SysUtil.getSysDate());

        String json = JSON.toJSONString(DeviceTools.headers);
        params.put("jsonString", json);

        Log.i(Constants.tag, "openApiGetDeviceInfo ok jsonString:  " + json);

        String jsonResult = JSON.toJSONString(params);

        Log.i(Constants.tag, "unity3d openApiGetDeviceInfo: " + jsonResult);
        return jsonResult;

    }

    /**
     * 初始化
     *
     * @param context
     */
    @SuppressLint("MissingPermission")
    private static void init(Context context) {
        Log.i(Constants.tag, "DeviceTools  init 0");
        if (isInit) {
            return;
        }
        isInit = true;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            OpenUdidManager.sync(context);
            String udid = null;
            if (OpenUdidManager.isInitialized()) {
                udid = OpenUdidManager.getOpenUDID();
            }
            if (udid == null) {
                udid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
                if (udid == null) {
                    udid = tm.getDeviceId();
                }
            }

            headers.put("udid", udid);
            headers.put("clientid", "0");
            headers.put("imei", tm.getDeviceId());

            headers.put("equipment", "early");//设备
            headers.put("kingdom", "open_app");//步骤名称
            headers.put("phylum", "1");//步骤顺序号
            headers.put("family", "");
            headers.put("genus", "");
            headers.put("value", "");//累计到此步骤次数
            headers.put("simulator", Build.MODEL);//模拟器版本
            headers.put("creative", "0");//渠道id

            headers.put("clientVersion", getVersionCode(context) + "");//应用版本
            headers.put("ratio", SysUtil.getRatio((Activity) context));//分辨率
            headers.put("phone", Build.MODEL);//手机型号
            headers.put("system", String.valueOf(Build.VERSION.SDK_INT));//系统版本

            headers.put("eqDate", SysUtil.getSysDate());//日期
            headers.put("eqTime", SysUtil.getSysTime());//时间

            headers.put("extra", getExtra((Activity) context));//其他
            //其他
            headers.put("os", "android: " + Build.VERSION.SDK_INT);
            headers.put("operator", URLEncoder.encode(tm.getNetworkOperatorName(), "UTF-8"));
            headers.put("runtimeId", UUID.randomUUID().toString());

            headers.put("packageName", context.getPackageName());
            try {
                headers.put("networkType", APNUtil.getNetworkType(context));
            } catch (Exception e) {
                isInit = false;
                Log.w(Constants.tag, "APNUtil.getNetworkType失败," + e.getMessage());
            }
//			try {
//				headers.put("tz", LocationUtils.getCurrentTimeZone());
//			}catch (Exception e){
//				e.fillInStackTrace();
//			}
            Log.i(Constants.tag, "DeviceTools  init");

        } catch (Exception e) {
            isInit = false;
            Log.e(Constants.tag, "AccessHttpService初始化失败", e);
        }
        Log.i(Constants.tag, "DeviceTools  init 1");
    }


    private static String getExtra(Activity activity) {
        StringBuilder sb = new StringBuilder();
        sb.append("phone_ratio:").append(SysUtil.getRatio(activity)).append(",");
        sb.append("phone_mem:").append(SysUtil.getMemoryInfo(activity)[0]).append(",");
        sb.append("phone_cpu:").append(SysUtil.getCpuInfo()).append(",");
        sb.append("phone_maxdisk:").append(SysUtil.getSDCardMemory(activity, 0));
        return sb.toString();
    }

    public static String getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknow";
        }
    }

    public static String getUDID() {
        return headers.get("udid");
    }

    public static String getIMEI() {
        return headers.get("imei");
    }


    public static Map<String, String> getHeaders() {
        return headers;
    }

};