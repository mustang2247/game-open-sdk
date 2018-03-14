package com.xiaoysdk.openapi.xiaoysdk.ane;

/**
 * Created by muskong on 2017/11/2.
 */

public class XiaoYEntry {
    private String key;
    private Object value;

    public XiaoYEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
