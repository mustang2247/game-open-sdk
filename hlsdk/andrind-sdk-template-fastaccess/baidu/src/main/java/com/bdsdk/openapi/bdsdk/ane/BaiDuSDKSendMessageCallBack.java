package com.bdsdk.openapi.bdsdk.ane;

/**
 * Created by muskong on 2017/11/14.
 *
 * 发送消息到unity
 */
public interface BaiDuSDKSendMessageCallBack {

    void sentMessage(String gameObject, String methodName, String msg);
}
