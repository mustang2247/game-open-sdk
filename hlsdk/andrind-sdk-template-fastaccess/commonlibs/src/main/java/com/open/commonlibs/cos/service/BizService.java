package com.open.commonlibs.cos.service;

import android.content.Context;
import android.util.Log;

import com.open.commonlibs.cos.utils.LocalCredentialProvider;
import com.tencent.cos.COSClient;
import com.tencent.cos.COSConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by bradyxiao on 2017/4/11.
 * author bradyxiao
 */
public class BizService {
    /**
     * cos的appid
     */
    public String appid = "10034783";//"1251810956";
    /**
     * appid的一个空间名称
     */
    public String bucket = "huleshikongres";//"crashreport";
    /**
     * cos sdk 的用户接口
     */
    public COSClient cosClient;

    /**
     * 设置园区；根据创建的cos空间时选择的园区
     * 华南园区：gz 或 COSEndPoint.COS_GZ(已上线)
     * 华北园区：tj 或 COSEndPoint.COS_TJ(已上线)
     * 华东园区：sh 或 COSEndPoint.COS_SH
     */
    public String region = "sh";
    /**
     * cos sdk 配置设置; 根据需要设置
     */
    private COSConfig config;

    private static BizService bizService;

    private static String baseUrl = "huleshikongres-10034783.file.myqcloud.com/logs/fishing?";
//    private static String baseUrl = "http://crashreport-1251810956.cosbj.myqcloud.com/logs/fishing?";
    private static String secretId = "AKIDb6UmttXMpGXcYKxIZfcRDsXGtCRZ2PBo";//"AKID41nOn1z1mLitthtvPPM9kCSWp16DmoV1";
    private static String secretKey = "azHOJ2a0Oj4HJ9oHYdWCgy4xiJivwlaF";//"Sjp4DAvBLMBoFYmpiT7ZouoW2Regv3zj";

    private BizService() {
    }

    private static byte[] syncObj = new byte[0];

    public static BizService instance() {
        synchronized (syncObj) {
            if (bizService == null) {
                bizService = new BizService();
            }
            return bizService;
        }
    }

    //    APPID: 1251810956
    //                SecretId: AKID41nOn1z1mLitthtvPPM9kCSWp16DmoV1
//                SecretKey: Sjp4DAvBLMBoFYmpiT7ZouoW2Regv3zj
    public void init(Context context) {
        synchronized (this) {
            if (cosClient == null) {
                config = new COSConfig();
//                region = "bj";
                config.setEndPoint(region);
//                appid = "1251810956";
//                bucket = "crashreport";
                cosClient = new COSClient(context, appid, config, null);
//                cosClient = new COSClient(context,appid,config,"xxxx");
            }
        }
    }


    /**
     * 本地签名
     *
     * @return
     */
    public String getLocalSign() {
//        String secretId = "AKID41nOn1z1mLitthtvPPM9kCSWp16DmoV1";
//        String secretKey = "Sjp4DAvBLMBoFYmpiT7ZouoW2Regv3zj";
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(secretKey);
        return localCredentialProvider.getSign(appid, bucket, secretId, null, 60 * 60);
    }

    public String getLocalOnceSign(String fileId) {
//        String secretId = "AKID41nOn1z1mLitthtvPPM9kCSWp16DmoV1";
//        String secretKey = "Sjp4DAvBLMBoFYmpiT7ZouoW2Regv3zj";
        LocalCredentialProvider localCredentialProvider = new LocalCredentialProvider(secretKey);
        return localCredentialProvider.getSign(appid, bucket, secretId, fileId, 60 * 60);
    }
    /***
     *  签名， 参考官网的签名方式，自己搭建签名服务器；
     *  demo中的签名服务器（http://203.195.194.28）只是适合此demo！
     *  签名类型：多次签名 ，单次签名
     */
    /**
     * @return 返回多次签名串
     */
    public String getSign() {
        String sign = null;
        String cgi = baseUrl + "bucket=" + bucket + "&service=video";
        try {
            URL url = new URL(cgi);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream in = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = bufferedReader.readLine();
            if (line == null) return null;
            JSONObject json = new JSONObject(line);
            if (json.has("sign")) {
                sign = json.getString("sign");
            }
            Log.w("XIAO", "sign=" + sign);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * @return 返回单次签名串
     */
    public String getSignOnce(String fileId) {
        urlEncoder(fileId);
        String onceSign = null;
        String cgi = baseUrl + "bucket=" + bucket + "&service=cos&expired=0&path=" + fileId;
        try {
            URL url = new URL(cgi);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream in = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = bufferedReader.readLine();
            if (line == null) return null;
            JSONObject json = new JSONObject(line);
            if (json.has("sign")) {
                onceSign = json.getString("sign");
            }
            Log.w("XIAO", "onceSign =" + onceSign);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return onceSign;
    }

    private String urlEncoder(String fileID) {
        if (fileID == null) {
            return fileID;
        }
        StringBuilder stringBuilder = new StringBuilder();
        String[] strFiled = fileID.trim().split("/");
        int length = strFiled.length;
        for (int i = 0; i < length; i++) {
            try {
                String str = URLEncoder.encode(strFiled[i], "utf-8").replace("+", "%20");
                stringBuilder.append(str).append("/");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (fileID.startsWith("/")) {
            fileID = "/" + stringBuilder.toString();
        } else {
            fileID = stringBuilder.toString();
        }
        return fileID;
    }
}
