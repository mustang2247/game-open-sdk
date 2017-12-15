package com.excelliance.open;

import android.content.Context;
import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.DialogInterface.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.view.View;
import android.view.KeyEvent;
import android.content.res.Resources;
import android.app.Dialog;
import android.content.IntentFilter;
import android.content.DialogInterface;
import android.view.Window;
import android.content.Intent;

import java.lang.reflect.Method;

import com.excelliance.lbsdk.base.BaseUtil;
import com.excelliance.lbsdk.base.BwbxUtilHelper;

public class BwbxUI {
    private Context mContext;
    private boolean sIsGamePlugin;

    private static final String TAG = "BwbxUI";

    private CustomDialog exitDialog = null;
    private CustomDialog gameDialog = null;
    private CustomDialog userDialog = null;
    private Dialog progressDialog = null;
    private CustomDialog switchFull = null;
    private CustomDialog downloadFull = null;
    private CustomDialog downloadMissing = null;
    private CustomDialog downloadAll = null;
    private CustomDialog downloadAllLater = null;
    private CustomDialog downloadFullRes = null;

    private String lebian_download_all_note = null;
    private String lebian_download_all_note_al = null;
    private String lebian_download_full_res = null;
    private String lebian_download_full_res_without_wifi = null;
    private String lebian_download_all_note_switch_gnet = null;
    private String lebian_err_loadres_generic = null;
    private String lebian_err_loadres_network = null;
    private String lebian_err_loadres_timeout = null;
    private String lebian_err_loadres_outofspace = null;
    private String lebian_err_loadres_md5_mis = null;
    private String lebian_err_loadres_rename_fail = null;
    private String lebian_choose_by_user_bwbx = null;
    private String lebian_network_environment_poor = null;

    private int gameDialogType = 0;

    public BwbxUI(Context context) {
        mContext = context;
    }

    public void init() {
        Log.d(TAG, "init");
        IntentFilter filter = new IntentFilter();
        filter.addAction(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_QUERY_USER", ""));
        filter.addAction(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_QUERY_RETRY", ""));
        filter.addAction(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_FINISHED", ""));
        filter.addAction(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_PROMPT_ACTIVITY", ""));
        mContext.registerReceiver(bwbxbr, filter);
    }

    private BroadcastReceiver bwbxbr = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action="+action);
            String topActivity = BaseUtil.getTopActivity(mContext);
            Log.d(TAG, "topActivity="+topActivity);
            int receiveType = intent.getIntExtra("type", 0);
            if ((!action.equals(getPackageName() + BwbxUtilHelper.getString(mContext, "ACTION_QUERY_USER", "")) || (receiveType != BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_PRELOAD_DIALOG", 0) && receiveType != BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_PROGRESS", 0))) && (!BaseUtil.isForeground(mContext) || (topActivity != null && (topActivity.contains("NextChapter") || topActivity.equals("com.excelliance.open.PromptActivity"))))) {
                Log.d(TAG, "has dialog");
                return;
            }

            if (action.equals(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_QUERY_RETRY", ""))) {
                showGameDialog(intent, true);
                platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CHECK_FOREGROUND", 0));
            }
            else if (action.equals(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_QUERY_USER", ""))) {
                int type = intent.getIntExtra("type", 0);
                Log.d(TAG, "acti="+action+", type="+type);
                if(type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_QUERY", 0)){
                    int size = intent.getIntExtra(BwbxUtilHelper.getString(mContext, "ACTION_SIZE", ""), 0);
                    showUserDialog(size);
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CHECK_FOREGROUND", 0));
                }else if(type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_PROGRESS", 0)){
                    //show downloading progress bar
                    if(GlobalSettings.SHOW_LOADING_PROGRESS_BWBX){
                        int progress = intent.getIntExtra("progress", -1);
                        showProgressDialog(progress, true);
                    }
                }
                else if (type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_DOWNLOAD_FULL", 0)) {
                    Log.d(TAG, "ACTION_TYPE_DOWNLOAD_FULL");
                    showDownloadFull(true);
                }
                else if (type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_DOWNLOAD_MISSING", 0)) {
                    Log.d(TAG, "ACTION_TYPE_DOWNLOAD_MISSING");
                    showDownloadMissing(true);
                }
                else if (type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_SWITCH_FULL", 0)) {
                    showSwitchFull(true);
                }
                else if(type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_DOWNLOAD_ALL_WITHOUT_WIFI", 0)) {
                    long size = intent.getLongExtra(BwbxUtilHelper.getString(mContext, "ACTION_SIZE", ""), 0);
                    Log.d(TAG, "size:" + size);
                    showDownloadAll(size, true);
                }
                else if(type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_DOWNLOAD_ALL_WITHOUT_WIFI_LATER", 0)) {
                    long size = intent.getLongExtra("size", 0);
                    boolean zipExists = intent.getBooleanExtra("zipExists", false);
                    boolean dataConnection = intent.getBooleanExtra("dataConnection", false);
                    boolean downloadZip = intent.getBooleanExtra("downloadZip", false);
                    Log.d(TAG, "size:" + size + " zipExists:"+zipExists+" downloadZip:"+downloadZip+" dataConnection:"+dataConnection);
                    showDownloadAllLater(size, zipExists, downloadZip, dataConnection, true);
                }
                else if(type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_DOWNLOAD_ALL_RES", 0)) {
                    long size = intent.getLongExtra(BwbxUtilHelper.getString(mContext, "ACTION_SIZE", ""), 0);
                    int triggerReason = intent.getIntExtra("triggerReason", 0);
                    Log.d(TAG, "size:" + size + " triggerReason:"+triggerReason);
                    showDownloadFullRes(size, true, triggerReason);
                }
                else if(type == BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_PRELOAD_DIALOG", 0)) {
                    final long size = intent.getLongExtra("size", 0);
                    final boolean zipExists = intent.getBooleanExtra("zipExists", false);
                    final boolean downloadZip = intent.getBooleanExtra("downloadZip", false);
                    Log.d(TAG, "reload dialog size:" + size + " zipExists:"+zipExists);
                    showDownloadAllLater(size, zipExists, downloadZip, false, false);
                    showSwitchFull(false);
                    showGameDialog(intent, false);
                    showProgressDialog(1, false);
                    showDownloadFull(false);
                    showDownloadMissing(false);
                    showDownloadAll(size, false);
                    showDownloadFullRes(size, false);
                }
            }
            else if (action.equals(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_PROMPT_ACTIVITY", ""))) {
                Intent activityIntent = new Intent("com.excelliance.open.action.startPromptActivity");
                activityIntent.setPackage(mContext.getPackageName());
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activityIntent.putExtra("size", intent.getLongExtra("size", 0));
                activityIntent.putExtra("zipExists", intent.getBooleanExtra("zipExists", false));
                activityIntent.putExtra("dataConnection", intent.getBooleanExtra("dataConnection", false));
                activityIntent.putExtra("dialogtype", intent.getIntExtra("dialogtype", 1));
                activityIntent.putExtra("requiredSize",intent.getLongExtra("requiredSize",0));
                mContext.startActivity(activityIntent);
            }
            /*
			else if (action.equals(getPackageName()+BwbxUtilHelper.getString(mContext, "ACTION_FINISHED", ""))) {
				String libName = intent.getStringExtra("gamelib");
				if (getPackageName().equals(libName)) {
					BwbxUtil.setFull(intent.getStringExtra("version"), intent.getStringExtra("savePath"));
				}
			}
			*/
        }
    };

    private void platDo(int type) {
        Log.d(TAG, "platDo type="+type);
        Intent intent = new Intent(BwbxUtilHelper.getString(mContext, "ACTION_PLAT_DO", ""));
        intent.setPackage(getPackageName());
        intent.putExtra(BwbxUtilHelper.getString(mContext, "ACTION_TYPE", ""), type);
        mContext.startService(intent);
    }

    private void showSwitchFull(final boolean show) {
        Log.d(TAG, "showSwitchFull ENTER");

        if (switchFull == null) {
            Resources res = mContext.getResources();
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showSwitchFull no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            int resId = res.getIdentifier("lebian_switch_to_full", "string", getPackageName());
            builder.setMessage(res.getString(resId));
            resId = res.getIdentifier("lebian_hint", "string", getPackageName());
            builder.setTitle(res.getString(resId));
            resId = res.getIdentifier("lebian_button_ok", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent nextIntent = new Intent("com.excelliance.open.NEXT_CHAPTER");
                    nextIntent.setPackage(getPackageName());
                    mContext.startService(nextIntent);
                    dialog.dismiss();
                    switchFull = null;
                }
            });

            resId = res.getIdentifier("lebian_button_cancel", "string", getPackageName());
            builder.setNegativeButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    switchFull = null;
                }
            });

            switchFull = builder.create();
            switchFull.setCancelable(false);
            switchFull.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        }
        if(!switchFull.isShowing() && show) {
            switchFull.show();
        }

    }

    private void showGameDialog(Intent intent, final boolean show) {
        Log.d(TAG, "showGameDialog ENTER");

        if (gameDialog == null) {
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showGameDialog no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            final Context sInstance = mContext;
            final String packageName = sInstance.getPackageName();

            String desc = "lebian_err_loadres_generic";
            int resId = sInstance.getResources().getIdentifier(desc, "string", packageName);
            lebian_err_loadres_generic = sInstance.getResources().getString(sInstance.getResources().getIdentifier("lebian_err_loadres_generic", "string", packageName));
            lebian_err_loadres_network = sInstance.getResources().getString(sInstance.getResources().getIdentifier("lebian_err_loadres_network", "string", packageName));
            lebian_err_loadres_timeout = sInstance.getResources().getString(sInstance.getResources().getIdentifier("lebian_err_loadres_timeout", "string", packageName));
            lebian_err_loadres_outofspace = sInstance.getResources().getString(sInstance.getResources().getIdentifier("lebian_err_loadres_outofspace", "string", packageName));
            lebian_err_loadres_md5_mis = sInstance.getResources().getString(sInstance.getResources().getIdentifier("lebian_err_loadres_md5_mis", "string", packageName));
            lebian_err_loadres_rename_fail = sInstance.getResources().getString(sInstance.getResources().getIdentifier("lebian_err_loadres_rename_fail", "string", packageName));
            builder.setMessage(sInstance.getResources().getString(resId));
            resId = sInstance.getResources().getIdentifier("lebian_hint", "string", sInstance.getPackageName());
            builder.setTitle(sInstance.getResources().getString(resId));
            //int retryId = sInstance.getResources().getIdentifier("button_ok", "string", packageName);
            int retryId = sInstance.getResources().getIdentifier("lebian_retry", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(retryId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BwbxUtilHelper.userRetry(mContext);
                    dialog.dismiss();
                    gameDialog = null;
                    BwbxUtilHelper.userClick(mContext, gameDialogType, 1);
                }
            });
            int resIdNeg = sInstance.getResources().getIdentifier("lebian_quit_game", "string", getPackageName());
            builder.setNegativeButton(mContext.getResources().getString(resIdNeg), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    BaseUtil.getTopActivity().finish();
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CANCEL_CHECK_FOREGROUND", 0));
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CLEAN_EXIT", 0));
                    gameDialog = null;
                    //android.os.Process.sendSignal(android.os.Process.myPid(), 1000);
                    BwbxUtilHelper.userClick(mContext, gameDialogType, 2);
                }
            });


            gameDialog = builder.create();
            gameDialog.setCancelable(false);
            gameDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
                {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
            });
        }

        if (!gameDialog.isShowing() && show) {
            long errcode = intent.getLongExtra("errcode", -1);
            String exception = intent.getStringExtra("exceptionInfo");
            String desc = lebian_err_loadres_generic;
            int type = BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_DOWNLOAD_FAILED_UNKNOWN", 0);
            if (errcode == BwbxUtilHelper.getInt(mContext, "ERR_NETWORK", 0)) {
                desc = lebian_err_loadres_network;
                type = BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_DOWNLOAD_FAILED_ERR_NETWORK", 0);
            }
            else if (errcode == BwbxUtilHelper.getInt(mContext, "ERR_TIMEOUT", 0)){
                desc = lebian_err_loadres_timeout;
                type = BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_DOWNLOAD_FAILED_TIMOUT", 0);
            }
            else if (errcode == BwbxUtilHelper.getInt(mContext, "ERR_SPACE_NOT_ENOUGH", 0)){
                desc = lebian_err_loadres_outofspace;
                type = BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_DOWNLOAD_FAILED_SPACE_NOT_ENOUGH", 0);
            }
            else if (errcode == BwbxUtilHelper.getInt(mContext, "ERR_MD5_MIS", 0)){
                desc = lebian_err_loadres_md5_mis;
                type = BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_DOWNLOAD_FAILED_MD5_MIS", 0);
            }
            else if (errcode == BwbxUtilHelper.getInt(mContext, "ERR_RENAME_FAIL", 0)){
                desc = lebian_err_loadres_rename_fail;
                type = BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_DOWNLOAD_FAILED_RENAME_FAIL", 0);
            }
            gameDialogType = type;
            String msg = (errcode == -1 && exception != null && exception.length()>0) ? (desc+"("+exception+")") : desc;
            gameDialog.setMessage(msg);
            gameDialog.show();
        }
    }

    private void showUserDialog(int size) {
        Log.d(TAG, "showUserDialog ENTER");
        if (switchFull!=null)
            return;

        if (userDialog == null) {
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showUserDialog no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            final Context sInstance = mContext;
            int resId = sInstance.getResources().getIdentifier("lebian_download_note", "string", sInstance.getPackageName());
            int resIdPos = 0;
            int resIdNeg = 0;
            String sizeStr = "";
            int sizeM = size/(1024*1024);
            int sizeK = size/1024;
            if(sizeM > 0){
                sizeStr += (sizeM+"."+((size%(1024*1024))/103))+"MB";
            }else{
                if(size%1024 != 0){
                    sizeK++;
                }
                sizeStr += (sizeK)+"KB";
            }
            final int dialogType;
            final String packageName = sInstance.getPackageName();
            boolean packageReady = false;
            boolean patchDownloaded = false;
            try {
                Class versionManager = Class.forName("com.excelliance.open.VersionManager");
                Method getInstance = versionManager.getDeclaredMethod("getInstance", new Class[] {});
                getInstance.setAccessible(true);
                Object vm = getInstance.invoke(null, new Object[] {});
                Method setContext = versionManager.getDeclaredMethod("setContext", Context.class);
                setContext.setAccessible(true);
                setContext.invoke(vm, mContext);

                Method pkgReady = versionManager.getDeclaredMethod("packageReady", new Class[] {});
                pkgReady.setAccessible(true);
                Boolean result = (Boolean)pkgReady.invoke(vm, new Object[] {});
                packageReady = result.booleanValue();

                Method patchDled = versionManager.getDeclaredMethod("patchDownloaded", new Class[] {});
                patchDled.setAccessible(true);
                result = (Boolean)patchDled.invoke(vm, new Object[] {});
                patchDownloaded = result.booleanValue();
            }
            catch (Exception e) {
                Log.d(TAG, "no versionManager");
                e.printStackTrace();
            }
            Log.d(TAG, "packageReady:"+packageReady+" patchDownloaded"+patchDownloaded);
            if (packageReady) {
                // if the whole package is ready for use, or whole patch downloaded, switch to the whole package
                resId = sInstance.getResources().getIdentifier("lebian_restart_required", "string", packageName);
                dialogType = 1;
                resIdPos = sInstance.getResources().getIdentifier("lebian_button_ok", "string", packageName);
            }else if(patchDownloaded){
                dialogType = 2;
                resId = sInstance.getResources().getIdentifier("lebian_err_loadres_outofspace", "string", packageName);
                resIdPos = sInstance.getResources().getIdentifier("lebian_clean_now", "string", packageName);
            }else{
                dialogType = 0;
                resId = sInstance.getResources().getIdentifier("lebian_download_missing_note", "string", packageName);
                resIdPos = sInstance.getResources().getIdentifier("lebian_continue_game", "string", packageName);
                resIdNeg = sInstance.getResources().getIdentifier("lebian_enable_now", "string", packageName);
            }
            builder.setMessage(sInstance.getResources().getString(resId));
            resId = sInstance.getResources().getIdentifier("lebian_hint", "string", packageName);
            builder.setTitle(sInstance.getResources().getString(resId));
            if(resIdPos > 0){
                builder.setPositiveButton(mContext.getResources().getString(resIdPos), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CANCEL_CHECK_FOREGROUND", 0));
                        if(dialogType == 1){
                            Intent intent = new Intent("com.excelliance.open.NEXT_CHAPTER");
                            intent.setPackage(getPackageName());
                            sInstance.startService(intent);
                        }else if(dialogType == 2){
                            BaseUtil.getTopActivity().finish();
                            platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CLEAN_EXIT", 0));
                        }
                        BwbxUtilHelper.userAction(mContext, 1, 1);
                        userDialog = null;
                    }
                });
            }
            if(resIdNeg > 0){
                builder.setNegativeButton(mContext.getResources().getString(resIdNeg), new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        BaseUtil.getTopActivity().finish();
                        platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CANCEL_CHECK_FOREGROUND", 0));
                        BwbxUtilHelper.userAction(mContext, 1, 2);
                        platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CLEAN_EXIT", 0));
                        userDialog = null;
                    }
                });
            }

            userDialog = builder.create();
            userDialog.setCancelable(false);
            userDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                        case KeyEvent.KEYCODE_SEARCH:{
                            return true;
                        }

                        default:
                            return false;
                    }
                }
            });
        }

        if(!userDialog.isShowing()) {
            userDialog.show();
        }

    }

    private void showProgressDialog(int progress, final boolean show) {
        Log.d(TAG, "showProgressDialog ENTER, progress="+progress);

        if(progress == -1){
            if(progressDialog != null){
                progressDialog.dismiss();
                progressDialog = null;
            }

            return;
        }

        if (switchFull!=null)
            return;

        if (progressDialog == null) {
            final LayoutInflater dialogInflater = LayoutInflater.from(mContext);
            Context sInstance = mContext;
            String pkgName = getPackageName();
            Resources res = mContext.getResources();
            int resId = res.getIdentifier("lebian_progress_dialog", "layout", pkgName);
            final View view = dialogInflater.inflate(resId, null, false);

            resId = res.getIdentifier("lebian_outer_icon", "id", pkgName);
            if(resId > 0){
                ImageView outer = (ImageView)view.findViewById(resId);
                resId = res.getIdentifier("lebian_outer_circle", "drawable", pkgName);
                outer.setImageDrawable(res.getDrawable(resId));
                resId = res.getIdentifier("lebian_outer_animator", "anim", pkgName);
                if(resId > 0){
                    Animation outerAnim = AnimationUtils.loadAnimation(sInstance, resId);
                    outer.startAnimation(outerAnim);
                }
            }

            resId = res.getIdentifier("lebian_inner_icon", "id", pkgName);
            if(resId > 0){
                ImageView inner = (ImageView)view.findViewById(resId);
                resId = res.getIdentifier("lebian_inner_circle", "drawable", pkgName);
                inner.setImageDrawable(res.getDrawable(resId));
                resId = res.getIdentifier("lebian_inner_animator", "anim", pkgName);
                if(resId > 0){
                    Animation innerAnim = AnimationUtils.loadAnimation(sInstance, resId);
                    inner.startAnimation(innerAnim);
                }
            }

            resId = res.getIdentifier("lebian_center_icon", "id", pkgName);
            if(resId > 0){
                ImageView center = (ImageView)view.findViewById(resId);
                resId = res.getIdentifier("lebian_center_img", "drawable", pkgName);
                center.setImageDrawable(res.getDrawable(resId));
                resId = res.getIdentifier("lebian_center_animator", "anim", pkgName);
                if(resId > 0){
                    Animation centerAnim = AnimationUtils.loadAnimation(sInstance, resId);
                    center.startAnimation(centerAnim);
                }
            }

            //resId = sTargetResources.getIdentifier("custom_progress_dialog_theme", "style", pkgName);
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showProgressDialog no topActivity");
                return;
            }
            progressDialog = new Dialog(BaseUtil.getTopActivity());
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            progressDialog.setContentView(view);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                        case KeyEvent.KEYCODE_SEARCH:{
                            return true;
                        }

                        default:
                            return false;
                    }
                }
            });
        }

        if (!progressDialog.isShowing() && show) {
            progressDialog.show();
        }

        if(progress >= 100){
            progressDialog.dismiss();
            progressDialog = null;
        }

    }

    private void showDownloadFull(final boolean show) {
        Log.d(TAG, "showDownloadFull ENTER");

        if (downloadFull == null) {
            Resources res = mContext.getResources();
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showDownloadFull no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            int resId = res.getIdentifier("lebian_resource_missing", "string", getPackageName());
            builder.setMessage(res.getString(resId));
            resId = res.getIdentifier("lebian_hint", "string", getPackageName());
            builder.setTitle(res.getString(resId));
            resId = res.getIdentifier("lebian_download_now", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent nextIntent = new Intent("com.excelliance.open.NEXT_CHAPTER");
                    nextIntent.setPackage(getPackageName());
                    mContext.startService(nextIntent);
                    dialog.dismiss();
                    downloadFull = null;
                }
            });

            downloadFull = builder.create();
            downloadFull.setCancelable(false);
            downloadFull.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        }

        if(!downloadFull.isShowing() && show) {
            downloadFull.show();
        }

    }

    private void showDownloadMissing(final boolean show) {
        Log.d(TAG, "showDownloadMissing ENTER");

        if (downloadMissing == null) {
            Resources res = mContext.getResources();
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showDownloadMissing no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            int resId = res.getIdentifier("lebian_download_missing_note", "string", getPackageName());
            builder.setMessage(res.getString(resId));
            resId = res.getIdentifier("lebian_hint", "string", getPackageName());
            builder.setTitle(res.getString(resId));
            resId = res.getIdentifier("lebian_continue_game", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadMissing = null;
                }
            });

            resId = res.getIdentifier("lebian_enable_now", "string", getPackageName());
            builder.setNegativeButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    BaseUtil.getTopActivity().finish();
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CANCEL_CHECK_FOREGROUND", 0));
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CLEAN_EXIT", 0));
                    downloadMissing = null;
                }
            });

            downloadMissing = builder.create();
            downloadMissing.setCancelable(false);
            downloadMissing.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        }

        if(!downloadMissing.isShowing() && show) {
            downloadMissing.show();
        }

    }

    private void showDownloadAll(long size, final boolean show) {
        Log.d(TAG, "showDownloadAll ENTER");

        if (downloadAll == null) {
            Resources res = mContext.getResources();
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showDownloadAll no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            int resId = res.getIdentifier("lebian_download_all_note_switch_gnet", "string", getPackageName());
            lebian_download_all_note_switch_gnet = res.getString(resId);
            builder.setMessage(String.format(res.getString(resId),((float)(size))/(1024.0f * 1024.0f)));
            resId = res.getIdentifier("lebian_hint", "string", getPackageName());
            builder.setTitle(res.getString(resId));
            resId = res.getIdentifier("lebian_continue_game", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadAll = null;
                    BwbxUtilHelper.downloadAll(mContext, false);
                    BwbxUtilHelper.userClick(mContext, BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_SWITCH_MOBILE", 0), 1);
                }
            });

            resId = res.getIdentifier("lebian_download_not_now", "string", getPackageName());
            builder.setNegativeButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadAll = null;
                    mContext.sendBroadcast(new Intent(getPackageName() + ".action.DOWNLOAD_ALL_LATER"));
                    BwbxUtilHelper.userClick(mContext, BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_SWITCH_MOBILE", 0), 2);
                }
            });

            downloadAll = builder.create();
            downloadAll.setCancelable(false);
            downloadAll.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        }

        if(!downloadAll.isShowing() && show) {
            downloadAll.setMessage(String.format(lebian_download_all_note_switch_gnet,((float)(size))/(1024.0f * 1024.0f)));
            downloadAll.show();
        }

    }

    private void showDownloadAllLater(final long size, final boolean zipExists, final boolean downloadZip, final boolean dataConnection, final boolean show) {
        Log.d(TAG, "showDownloadAllLater ENTER");

        if (downloadAllLater == null) {
            Resources res = mContext.getResources();
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showDownloadAllLater no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            int resId = res.getIdentifier("lebian_download_all_note", "string", getPackageName());
            int resId1 = res.getIdentifier("lebian_download_all_note_al", "string", getPackageName());
            lebian_download_all_note = res.getString(resId);
            lebian_download_all_note_al = res.getString(resId1);
            builder.setMessage(String.format(lebian_download_all_note,((float)(size))/(1024.0f * 1024.0f)));
            resId = res.getIdentifier("lebian_hint", "string", getPackageName());
            builder.setTitle(res.getString(resId));
            resId = res.getIdentifier("lebian_download_now", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadAllLater = null;
                    if(!zipExists && !downloadZip)
                        BwbxUtilHelper.userDownload(mContext);
                    else
                        BaseUtil.switchToDownloadFullRes(mContext);
                    BwbxUtilHelper.userClick(mContext, BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_SECOND", 0), 1);
                }
            });
            resId = res.getIdentifier("lebian_quit_game", "string", getPackageName());
            builder.setNegativeButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    BaseUtil.getTopActivity().finish();
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CANCEL_CHECK_FOREGROUND", 0));
                    platDo(BwbxUtilHelper.getInt(mContext, "ACTION_TYPE_CLEAN_EXIT", 0));
                    downloadAllLater = null;
                    BwbxUtilHelper.userClick(mContext, BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_SECOND", 0), 2);
                }
            });
            downloadAllLater = builder.create();
            downloadAllLater.setCancelable(false);
            downloadAllLater.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        }
        if(!downloadAllLater.isShowing() && show) {
            downloadAllLater.setMessage(String.format(dataConnection?lebian_download_all_note:lebian_download_all_note_al,((float)(size))/(1024.0f * 1024.0f)));
            downloadAllLater.show();
        }

    }

    private void showDownloadFullRes(long size, final boolean show) {
        showDownloadFullRes(size, show, 0);
    }

    private void showDownloadFullRes(long size, final boolean show, int triggerReason) {
        Log.d(TAG, "showDownloadFullRes ENTER");

        if (downloadFullRes == null) {
            Resources res = mContext.getResources();
            if(BaseUtil.getTopActivity() == null){
                Log.d(TAG, "showDownloadFullRes no topActivity");
                return;
            }
            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, BaseUtil.getTopActivity());
            int resId = res.getIdentifier("lebian_choose_by_user_bwbx", "string", getPackageName());
            if (triggerReason == 1)
                resId = res.getIdentifier("lebian_network_environment_poor", "string", getPackageName());
            lebian_choose_by_user_bwbx = res.getString(res.getIdentifier("lebian_choose_by_user_bwbx", "string", getPackageName()));
            lebian_network_environment_poor = res.getString(res.getIdentifier("lebian_network_environment_poor", "string", getPackageName()));
            builder.setMessage(String.format(res.getString(resId),((float)(size))/(1024.0f * 1024.0f)));
            resId = res.getIdentifier("lebian_hint", "string", getPackageName());
            builder.setTitle(res.getString(resId));
            resId = res.getIdentifier("lebian_exit_dialog_yes", "string", getPackageName());
            builder.setPositiveButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadFullRes = null;
                    BwbxUtilHelper.userClick(mContext, BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_OLD_USER", 0), 1);
                    BaseUtil.switchToDownloadFullRes(mContext);
                }
            });

            resId = res.getIdentifier("lebian_exit_dialog_no", "string", getPackageName());
            builder.setNegativeButton(mContext.getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    downloadFullRes = null;
                    BwbxUtilHelper.userClick(mContext, BwbxUtilHelper.getInt(mContext, "DIALOG_TYPE_OLD_USER", 0), 2);
                }
            });

            downloadFullRes = builder.create();
            downloadFullRes.setCancelable(false);
            downloadFullRes.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                    if (keyCode == KeyEvent.KEYCODE_SEARCH){
                        return true;
                    }else{
                        return false;
                    }
                }
            });
        }

        if(!downloadFullRes.isShowing() && show) {
            downloadFullRes.setMessage(String.format(triggerReason != 1 ? lebian_choose_by_user_bwbx : lebian_network_environment_poor, ((float) (size)) / (1024.0f * 1024.0f)));
            downloadFullRes.show();
        }

    }

    private String getPackageName() {
        //android.app.Application app = (android.app.Application)mContext;
        return mContext.getPackageName();
    }
}

