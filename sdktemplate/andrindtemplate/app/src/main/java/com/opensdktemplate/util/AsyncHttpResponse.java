package com.opensdktemplate.util;

public interface AsyncHttpResponse {
    
    public static final int STATUS_SUCCESS = 1;
    
    public static final int STATUS_CONNECT_TIMEOUT = 2;
    
    public static final int STATUS_READ_TIMEOUT = 3;
    
    public static final int STATUS_OTHER_ERRORS = 4;

    public void getMsg(int status, String msg);

}
