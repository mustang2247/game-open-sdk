package com.bdsdk.openapi.bdsdk.ane;

/**
 * Created by muskong on 2017/11/2.
 */

public class BaiDuEntry {
    private String key;
    private Object value;

    public BaiDuEntry(String key, Object value) {
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
