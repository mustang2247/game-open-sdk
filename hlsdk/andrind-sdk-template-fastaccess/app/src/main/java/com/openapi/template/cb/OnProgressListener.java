package com.openapi.template.cb;

/**
 * Created by hlmustang on 2017/11/24.
 */

public interface OnProgressListener {
    /**
     * 下载进度
     *
     * @param fraction 已下载/总大小
     */
    void onProgress(float fraction);
}
