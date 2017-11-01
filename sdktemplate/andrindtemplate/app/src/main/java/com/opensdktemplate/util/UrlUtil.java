package com.opensdktemplate.util;

import android.util.Log;

import com.openapi.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

public class UrlUtil {
	public static void putAndUrlEncode(Map<String, String> params, String key, String value) {
        try {
            params.put(key, URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            Log.i(Constants.tag, e.getMessage());
        }
    }

    public static String getCompleteUrl(String url, Map<String, String> params) {
        StringBuilder postData = new StringBuilder();
        for (Entry<String, String> entry : params.entrySet()) {
            if (postData.length() != 0) {
                postData.append("&");
            }
            postData.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return url + postData.toString();
    }
}
