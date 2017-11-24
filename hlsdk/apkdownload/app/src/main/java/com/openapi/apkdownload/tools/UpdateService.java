package com.openapi.apkdownload.tools;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.openapi.apkdownload.Constants;
import com.openapi.apkdownload.cb.OnProgressListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 检测安装更新文件的助手类
 */
public class UpdateService extends Service {

    public static final String DOWNLOAD_URL = "downloadUrl";
    public static final String DOWNLOAD_APP_NAME = "download_app_name";
//    public static final String DOWNLOAD_PROGRESS_LISTENER = "download_progress_listener";
    /**
     * 安卓系统下载类
     **/
    private DownloadManager downloadManager;

    /**
     * 接收下载完的广播
     **/
    private DownloadCompleteReceiver receiver;


    private DownloadBinder binder;
    public static final int HANDLE_DOWNLOAD = 0x001;
    public static final float UNBIND_SERVICE = 2.0F;
    //下载任务ID
    private long downloadId;
    private String downloadUrl;
    private String downloadName;
    /**
     * 监听下载进度
     */
    private OnProgressListener onProgressListener;
    private ScheduledExecutorService scheduledExecutorService;
    private DownloadChangeObserver downloadObserver;

    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        Log.i(Constants.tag, "initDownManager 初始化下载器  " + downloadUrl);

        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadObserver = new DownloadChangeObserver();
        receiver = new DownloadCompleteReceiver();

        //设置下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 下载时，通知栏显示途中
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        /**设置漫游状态下是否可以下载*/
//        down.setAllowedOverRoaming(false);
        /**如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
         我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true.*/
        request.setVisibleInDownloadsUi(true);
        // 显示下载界面
        request.setVisibleInDownloadsUi(true);
        /**设置文件保存路径*/
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, downloadName + ".apk");
        /**将下载请求放入队列， return下载任务的ID*/
        downloadId = downloadManager.enqueue(request);

        //注册下载广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            downloadUrl = intent.getStringExtra(DOWNLOAD_URL);
            downloadName = intent.getStringExtra(DOWNLOAD_APP_NAME);
            // 调用下载
            initDownManager();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        // 注销下载广播
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();

        Log.i(Constants.tag, "下载任务服务销毁");
    }

    // 接受下载完成后的intent
    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(Constants.tag, "onReceive 接受下载完成后的自动安装apk  ");

            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                close();
//                SPUtil.put(Constant.SP_DOWNLOAD_PATH, downIdUri.getPath());
                //自动安装apk
//                APPUtil.installApk(context, downloadManager.getUriForDownloadedFile(downId));
                installAPK(downloadManager.getUriForDownloadedFile(downId));
                //停止服务并关闭广播
                UpdateService.this.stopSelf();

                if (onProgressListener != null) {
                    onProgressListener.onProgress(UNBIND_SERVICE);
                }
            }
        }
    }

    /**
     * 安装apk文件
     */
    private void installAPK(Uri apk) {
        Intent intents;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intents = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intents.addCategory("android.intent.category.DEFAULT");
//                intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intents);
        } else {
            intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            android.os.Process.killProcess(android.os.Process.myPid());
            // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
            try {
                startActivity(intents);
            } catch (Exception e) {
                Log.e("UpdataService", "installAPK", e);
            }
        }
    }

    public class DownloadBinder extends Binder {
        /**
         * 返回当前服务的实例
         *
         * @return
         */
        public UpdateService getService() {
            return UpdateService.this;
        }

    }

    public Handler downLoadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (onProgressListener != null && HANDLE_DOWNLOAD == msg.what) {
                //被除数可以为0，除数必须大于0
                if (msg.arg1 >= 0 && msg.arg2 > 0) {
                    onProgressListener.onProgress(msg.arg1 / (float) msg.arg2);
                }
            }
        }
    };
    /**
     * 监听下载进度
     */
    private class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver() {
            super(downLoadHandler);
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        }

        /**
         * 当所监听的Uri发生改变时，就会回调此方法
         *
         * @param selfChange 此值意义不大, 一般情况下该回调值false
         */
        @Override
        public void onChange(boolean selfChange) {
            scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 2, TimeUnit.SECONDS);
        }
    }

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    /**
     * 发送Handler消息更新进度和状态
     */
    private void updateProgress() {
        int[] bytesAndStatus = getBytesAndStatus(downloadId);
        downLoadHandler.sendMessage(downLoadHandler.obtainMessage(HANDLE_DOWNLOAD, bytesAndStatus[0], bytesAndStatus[1], bytesAndStatus[2]));
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    private int[] getBytesAndStatus(long downloadId) {
        int[] bytesAndStatus = new int[]{
                -1, -1, 0
        };
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bytesAndStatus;
    }

    /**
     * 关闭定时器，线程等操作
     */
    private void close() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }

        if (downLoadHandler != null) {
            downLoadHandler.removeCallbacksAndMessages(null);
        }
    }

    //===========================

    /**
     * 对外开发的方法
     *
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }



}