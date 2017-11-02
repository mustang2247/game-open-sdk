package com.openapi;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Created by muskong on 2017/7/24.
 */

public class Constants {
    // 1.不更新 2.非强制更新  3.强制更新
    public final static int NOT_UPDATE = 1;
    public final static int TIP_UPDATE = 2;
    public final static int FORCE_UPDATE = 3;

    public static final String tag = "openapiANE";

    //============as3 to java api=================

    public static final String OPENAPI_ANE_INIT = "openApiInit";
    public static final String OPENAPI_ANE_GETDEVICEINFO = "openApiGetDeviceInfo";
    public static final String OPENAPI_ANE_DOWNLOAD = "openApiGetdownload";

    public static final String OPENAPI_ANE_REGWXAPI = "openApiRegWXAPI";
    public static final String OPENAPI_ANE_ISINSTALL_WX = "openApiIsInstallWX";
    public static final String OPENAPI_ANE_SHARETEXTWXAPI = "openApiShareTextWXAPI";
    public static final String OPENAPI_ANE_SHAREWEBPAGEWXAPI = "openApiShareWebpageWXAPI";


    //=============java to as3 callback==============

    public static final String OPENAPI_ANE_INIT_CB = "OPENAPI_ANE_INIT_CB";
    public static final String OPENAPI_ANE_GETDEVICEINFO_CB = "OPENAPI_ANE_GETDEVICEINFO_CB";
    public static final String OPENAPI_ANE_DOWNLOAD_CB = "OPENAPI_ANE_DOWNLOAD_CB";

//    public static final String OPENAPI_ANE_REGWXAPI_CB = "OPENAPI_ANE_REGWXAPI_CB";
    public static final String OPENAPI_ANE_ISINSTALL_WX_CB = "OPENAPI_ANE_ISINSTALL_WX_CB";
    public static final String OPENAPI_ANE_SHARETEXTWXAPI_CB = "OPENAPI_ANE_SHARETEXTWXAPI_CB";
    public static final String OPENAPI_ANE_SHAREWEBPAGEWXAPI_CB = "OPENAPI_ANE_SHAREWEBPAGEWXAPI_CB";

    public static final String OPENAPI_ANE_WXAPI_REQ_CB = "OPENAPI_ANE_WXAPI_REQ_CB";//微信发送请求到第三方应用时，会回调到该方法
    public static final String OPENAPI_ANE_WXAPI_RESP_CB = "OPENAPI_ANE_WXAPI_RESP_CB";//第三方应用发送到微信的请求处理后的响应结果，会回调到该方法

    //===================================================

    public static String APP_ID = "wxd930ea5d5a258f4f";

    public static final int WXSceneSession = 0;     //发送到聊天
    public static final int WXSceneTimeline = 1;    //发送到朋友圈
    public static final int WXSceneFavorite = 2;    //添加到微信收藏





    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
