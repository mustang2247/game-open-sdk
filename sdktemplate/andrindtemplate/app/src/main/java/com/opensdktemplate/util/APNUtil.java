package com.opensdktemplate.util;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * 网络APN工具类。用于网络状态判断。
 *
 * @author chenzh
 */
public class APNUtil {

    /**
     * 获取Http代理host。使用手机2G/3G网络时需要用到。
     *
     * @param ctx
     * @return
     */
//    public static HttpHost getHttpProxy(Context ctx) {
//        ConnectivityManager connMgr = (ConnectivityManager) ctx
//                .getSystemService("connectivity");
//        NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
//        if ((netInfo != null) && (netInfo.isAvailable())
//                && (netInfo.getType() == 0)) {
//            String str = android.net.Proxy.getDefaultHost();
//            int i = android.net.Proxy.getDefaultPort();
//            if (str != null) {
//                return new HttpHost(str, i);
//            }
//        }
//        return null;
//    }


    /**
     * Called when the activity is first created.
     */
    public static final String CTWAP = "ctwap";
    public static final String CTNET = "ctnet";
    public static final String CMWAP = "cmwap";
    public static final String CMNET = "cmnet";
    public static final String NET_3G = "3gnet";
    public static final String WAP_3G = "3gwap";
    public static final String UNIWAP = "uniwap";
    public static final String UNINET = "uninet";

    public static final int TYPE_CT_WAP = 5;
    public static final int TYPE_CT_NET = 6;
    public static final int TYPE_CT_WAP_2G = 7;
    public static final int TYPE_CT_NET_2G = 8;

    public static final int TYPE_CM_WAP = 9;
    public static final int TYPE_CM_NET = 10;
    public static final int TYPE_CM_WAP_2G = 11;
    public static final int TYPE_CM_NET_2G = 12;

    public static final int TYPE_CU_WAP = 13;
    public static final int TYPE_CU_NET = 14;
    public static final int TYPE_CU_WAP_2G = 15;
    public static final int TYPE_CU_NET_2G = 16;

    public static final int TYPE_OTHER = 17;

    public static Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");

    /**
     * 没有网络
     */
    public static final int TYPE_NET_WORK_DISABLED = 0;

    /**
     * wifi网络
     */
    public static final int TYPE_WIFI = 4;

    /**
     * 检测是否有网络
     *
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }

    public static int checkNetworkType(Context mContext) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo mobNetInfoActivity = connectivityManager
                    .getActiveNetworkInfo();
            if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {
                // 注意一：  
                // NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，  
                // 但是有些电信机器，仍可以正常联网，  
                // 所以当成net网络处理依然尝试连接网络。  
                // （然后在socket中捕捉异常，进行二次判断与用户提示）。  
                return TYPE_NET_WORK_DISABLED;
            } else {
                // NetworkInfo不为null开始判断是网络类型  
                int netType = mobNetInfoActivity.getType();
                if (netType == ConnectivityManager.TYPE_WIFI) {
                    // wifi net处理  
                    return TYPE_WIFI;
                } else if (netType == ConnectivityManager.TYPE_MOBILE) {
                    // 注意二：  
                    // 判断是否电信wap:  
                    // 不要通过getExtraInfo获取接入点名称来判断类型，  
                    // 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，  
                    // 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,  
                    // 所以可以通过这个进行判断！  

                    boolean is3G = isFastMobileNetwork(mContext);

                    final Cursor c = mContext.getContentResolver().query(
                            PREFERRED_APN_URI, null, null, null, null);
                    if (c != null) {
                        c.moveToFirst();
                        final String user = c.getString(c
                                .getColumnIndex("user"));
                        if (!TextUtils.isEmpty(user)) {
                            if (user.startsWith(CTWAP)) {
                                return is3G ? TYPE_CT_WAP : TYPE_CT_WAP_2G;
                            } else if (user.startsWith(CTNET)) {
                                return is3G ? TYPE_CT_NET : TYPE_CT_NET_2G;
                            }
                        }
                    }
                    c.close();

                    // 注意三：  
                    // 判断是移动联通wap:  
                    // 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip  
                    // 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在  
                    // 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...  
                    // 所以采用getExtraInfo获取接入点名字进行判断  

                    String netMode = mobNetInfoActivity.getExtraInfo();
                    Log.i("", "==================netmode:" + netMode);
                    if (netMode != null) {
                        // 通过apn名称判断是否是联通和移动wap  
                        netMode = netMode.toLowerCase();

                        if (netMode.equals(CMWAP)) {
                            return is3G ? TYPE_CM_WAP : TYPE_CM_WAP_2G;
                        } else if (netMode.equals(CMNET)) {
                            return is3G ? TYPE_CM_NET : TYPE_CM_NET_2G;
                        } else if (netMode.equals(NET_3G)
                                || netMode.equals(UNINET)) {
                            return is3G ? TYPE_CU_NET : TYPE_CU_NET_2G;
                        } else if (netMode.equals(WAP_3G)
                                || netMode.equals(UNIWAP)) {
                            return is3G ? TYPE_CU_WAP : TYPE_CU_WAP_2G;
                        }
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return TYPE_OTHER;
        }

        return TYPE_OTHER;

    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;

        }
    }

    public static String getNetworkType(Context context) {
        return convert(checkNetworkType(context));
    }

    private static String convert(int type) {


        switch (type) {
            case TYPE_WIFI:
                return "wifi";
            case TYPE_NET_WORK_DISABLED:
                return ("no network");
            case TYPE_CT_WAP:
                return ("ctwap");

            case TYPE_CT_WAP_2G:
                return ("ctwap_2g");

            case TYPE_CT_NET:
                return ("ctnet");

            case TYPE_CT_NET_2G:
                return ("ctnet_2g");

            case TYPE_CM_WAP:
                return ("cmwap");

            case TYPE_CM_WAP_2G:
                return ("cmwap_2g");

            case TYPE_CM_NET:
                return ("cmnet");

            case TYPE_CM_NET_2G:
                return ("cmnet_2g");

            case TYPE_CU_NET:
                return ("cunet");

            case TYPE_CU_NET_2G:
                return ("cunet_2g");

            case TYPE_CU_WAP:
                return ("cuwap");

            case TYPE_CU_WAP_2G:
                return ("cuwap_2g");

            case TYPE_OTHER:
                return ("other");

            default:
                return "unknow";
        }

    }

    /**
     * 获取ip
     *
     * @param context
     * @return
     */
    public static String getIp(Context context) {
        String ip = "";
        ConnectivityManager conMann = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetworkInfo = conMann.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mobileNetworkInfo.isConnected()) {
            ip = getLocalIpAddress();
            System.out.println("本地ip-----" + ip);
        } else if (wifiNetworkInfo.isConnected()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
            System.out.println("wifi_ip地址为------" + ip);
        }

        return ip;
    }

    public static String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni : nilist) {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address : ialist) {
                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = address.getHostAddress())) {
                        return ipv4;
                    }
                }

            }

        } catch (SocketException ex) {
            Log.e("localip", ex.toString());
        }
        return null;
    }

    public static String intToIp(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }


}  

