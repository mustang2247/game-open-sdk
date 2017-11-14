package com.hoolai.fastaccesssdk.fastaccess;

/**
 * Created by muskong on 2017/11/14.
 *
 * 发送消息到unity
 */
public interface SendMessageCallBack {

    void sentMessage(String gameObject, String methodName, String msg);
}
