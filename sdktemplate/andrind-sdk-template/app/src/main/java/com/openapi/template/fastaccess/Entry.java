package com.openapi.template.fastaccess;

/**
 * Created by muskong on 2017/11/2.
 */

public class Entry {
    private String key;
    private Object value;

    public Entry(String key, Object value) {
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
