package com.open.commonlibs.cos;

import android.content.Context;

import com.open.commonlibs.cos.service.BizService;
import com.open.commonlibs.cos.service.PutObjectSample;
import com.tencent.cos.utils.FileUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hlmustang on 2017/12/1.
 * 上传应用崩溃log
 */
public class FileUploadUtils {

    static BizService bizService;
    // 用于格式化日期,作为日志文件名的一部分
    private static DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public static void init(Context context){
        //初始化 cosClient
        bizService = BizService.instance();
        bizService.init(context);
    }

    /**
     * 上传log文件
     */
    public static void updateLog(final String currentPath){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String time = formatter.format(new Date());

                String filename = FileUtils.getFileName(currentPath);
                String cosPath =  "/logs/fishing/" + time + "/" + filename; //cos 上的路径
                PutObjectSample.putObjectForSamllFile(bizService,cosPath,currentPath);
            }
        }).start();


    }

}
