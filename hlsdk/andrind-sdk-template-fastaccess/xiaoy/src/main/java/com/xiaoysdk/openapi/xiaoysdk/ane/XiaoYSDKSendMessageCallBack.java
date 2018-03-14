package com.xiaoysdk.openapi.xiaoysdk.ane;

/**
 * Created by muskong on 2017/11/14.
 *
 * 发送消息到unity
 */
public interface XiaoYSDKSendMessageCallBack {

    void sentMessage(String gameObject, String methodName, String msg);
}
