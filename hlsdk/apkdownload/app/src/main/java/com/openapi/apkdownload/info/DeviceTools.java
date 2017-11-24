package com.openapi.apkdownload.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.openapi.apkdownload.Constants;
import com.openapi.apkdownload.tools.APNUtil;
import com.openapi.apkdownload.tools.SysUtil;

import org.openudid.OpenUdidManager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * http 服务工具类
 */
public class DeviceTools {

    public static Map<String, String> headers = new HashMap<String, String>();
    private static Boolean isInit = false;

    private static long differTime;

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
        params.put("ip", APNUtil.getIp(context));
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