package com.openapi.template.service;

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

import com.openapi.template.Constants;
import com.openapi.template.cb.OnProgressListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 用DownloadManager来实现版本更新
 */
public class UpdateService extends Service {
    public static final int HANDLE_DOWNLOAD = 0x001;
    public static final String BUNDLE_KEY_DOWNLOAD_URL = "download_url";
    public static final String DOWNLOAD_APP_NAME = "download_app_name";
    public static final float UNBIND_SERVICE = 2.0F;

    /**
     * 绑定器
     */
    private DownloadBinder binder;
    /**
     * 下载管理器
     */
    private DownloadManager downloadManager;
    /**
     * 下载进度观察者
     */
    private DownloadChangeObserver downloadChangeObserver;
    /**
     * 下载完成监听器
     */
    private BroadcastReceiver downLoadBroadcast;
    /**
     * 任务管理器
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * 下载任务ID
     */
    private long downloadId;
    /**
     * 下载URL
     */
    private String downloadUrl;
    private String downloadAppName;
    /**
     * 进度回调
     */
    private OnProgressListener onProgressListener;
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

    private Runnable progressRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        binder = new DownloadBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        downloadUrl = intent.getStringExtra(BUNDLE_KEY_DOWNLOAD_URL);
        downloadAppName = intent.getStringExtra(DOWNLOAD_APP_NAME);
        Log.i(Constants.tag, "Apk下载路径传递成功：" + downloadUrl + "  downloadAppName:  " + downloadAppName);
        downloadApk(downloadUrl);
        return binder;
    }

    /**
     * 下载最新APK
     */
    private void downloadApk(String url) {
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        // 注册下载进度监听
        downloadChangeObserver = new DownloadChangeObserver();
        registerContentObserver();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        /**设置用于下载时的网络状态*/
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        /**设置通知栏是否可见*/
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        /**设置漫游状态下是否可以下载*/
//        request.setAllowedOverRoaming(false);
        /**如果我们希望下载的文件可以被系统的Downloads应用扫描到并管理，
         我们需要调用Request对象的setVisibleInDownloadsUi方法，传递参数true.*/
        request.setVisibleInDownloadsUi(true);
        /**设置文件保存路径*/
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, downloadAppName + ".apk");
        /**将下载请求放入队列， return下载任务的ID*/
        downloadId = downloadManager.enqueue(request);

        // 注册service 广播 1.任务完成时 2.进行中的任务被点击
        registerBroadcast();
    }

    /**
     * 注册广播
     */
    private void registerBroadcast() {
        /**注册service 广播 1.任务完成时 2.进行中的任务被点击*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        registerReceiver(downLoadBroadcast = new DownLoadBroadcast(), intentFilter);
    }

    /**
     * 注销广播
     */
    private void unregisterBroadcast() {
        if (downLoadBroadcast != null) {
            unregisterReceiver(downLoadBroadcast);
            downLoadBroadcast = null;
        }
    }

    /**
     * 注册ContentObserver
     */
    private void registerContentObserver() {
        /** observer download change **/
        if (downloadChangeObserver != null) {
            getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), false, downloadChangeObserver);
        }
    }

    /**
     * 注销ContentObserver
     */
    private void unregisterContentObserver() {
        if (downloadChangeObserver != null) {
            getContentResolver().unregisterContentObserver(downloadChangeObserver);
        }
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

    /**
     * 接受下载完成广播
     */
    private class DownLoadBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            switch (intent.getAction()) {
                case DownloadManager.ACTION_DOWNLOAD_COMPLETE:
                    if (downloadId == downId && downId != -1 && downloadManager != null) {
                        Uri downIdUri = downloadManager.getUriForDownloadedFile(downloadId);

                        close();

                        if (downIdUri != null) {
                            Log.i(Constants.tag, "广播监听下载完成，APK存储路径为 ：" + downIdUri.getPath());
//                            SPUtil.put(Constant.SP_DOWNLOAD_PATH, downIdUri.getPath());
//                            APPUtil.installApk(context, downIdUri);
                            installAPK(downIdUri);
                        }
                        if (onProgressListener != null) {
                            onProgressListener.onProgress(UNBIND_SERVICE);
                        }
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void installAPK(Uri apk) {
        Intent intents;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intents = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intents.addCategory("android.intent.category.DEFAULT");
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
            // 如果不加上这句的话在apk安装完成之后点击单开会崩溃
            try {
                startActivity(intents);
            } catch (Exception e) {
                Log.e("UpdataService", "installAPK", e);
            }
        }
    }

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
            scheduledExecutorService.scheduleAtFixedRate(progressRunnable, 0, 1, TimeUnit.SECONDS);
        }
    }

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

    /**
     * 对外开发的方法
     *
     * @param onProgressListener
     */
    public void setOnProgressListener(OnProgressListener onProgressListener) {
        this.onProgressListener = onProgressListener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterBroadcast();
        unregisterContentObserver();
        Log.i(Constants.tag, "下载任务服务销毁");
    }
}
