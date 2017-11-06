package com.openapi;

import android.util.Log;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;

import java.nio.ByteBuffer;

/**
 * Created by muskong on 28/07/2017.
 *
 * 微信分享API
 */
public class WXShareManager {

    private static WXShareManager _instance;

    public static WXShareManager get_instance() {
        if(_instance == null){
            _instance = new WXShareManager();
        }
        return _instance;
    }

    /**
     * 微信登陆
     * @param api
     */
    public void wechatLogin(IWXAPI api){
        // send oauth request
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "none";

        api.sendReq(req);
    }

    /**
     * 分享文本
     * @param api
     * @param title
     * @param content
     */
    public void shareWXText(IWXAPI api, Integer scene, String title, String content){
        Log.i(Constants.tag, "shareWXText ok" + scene + title + content);
        if (content == null || content.length() == 0) {
            return;
        }

        // 初始化一个WXTextObject对象，添加分享内容
        WXTextObject textObject = new WXTextObject();
        textObject.text = content;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = content;
        if(title != null && !title.isEmpty() && title.length() > 0){
            msg.title = title;
        }

        // 构造一个req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction唯一标识请求
        req.message = msg;
        req.scene = scene;

        // 调用api接口发送数据到微信
        sendToWX(api, req);
        Log.i(Constants.tag, "shareWXText ok 1");

    }

    /**
     * 分享网页
     * @param api
     * @param url
     * @param title
     * @param description
     */
    public void shareWebPage(IWXAPI api, Integer scene, String url, String title, String description, ByteBuffer byteBuffer) {
        Log.i(Constants.tag, "shareWebPage ok " + url + scene + title + description);
        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = description;
//        if(byteBuffer != null && byteBuffer.hasArray() && byteBuffer.hasRemaining()){
//            msg.thumbData = byteBuffer.array();
//        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = scene;

        // 调用api接口发送数据到微信
        sendToWX(api, req);
        Log.i(Constants.tag, "shareWebPage ok 1");
    }

    /**
     * 分享到微信
     * @param api
     * @param req
     * 调用api接口发送数据到微信
     */
    private void sendToWX(IWXAPI api, SendMessageToWX.Req req){
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}

