package com.excelliance.open;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.excelliance.lbsdk.base.BaseUtil;
import com.excelliance.lbsdk.base.BwbxUtilHelper;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class PromptActivity extends Activity {

    private Parcelable promptActivityParcel = null;
    private final String TAG = "PromptActivity";
    Handler handler = new Handler();
    private static final int MSG_INSTALL_DIALOG = 3;
    private static final int MSG_DOWNLOAD_DIALOG = 2;
    private static final int MSG_UPDATE_AND_NOTSPACE = 1;
    private static final int MSG_BWBX_FIRST = 4;
    private static final int MSG_CHOOSE_BY_USER = 5;
    private static final int MSG_MERGE_ERR_DIALOG = 6;
    private static final int MSG_RESTART_FOR_JJ = 7;
    private static final int MSG_LOADING_DIALOG = 8;
    private static final int MSG_SPACE_NOT_ENOUGH = 9;
    private static final int MSG_NETWORK_USELESS = 10;
    private static final int MSG_NOT_FORCE_UPDATE = 11;
    private static final int MSG_RES_PATCH = 12;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate savedInstanceState=" + savedInstanceState);
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            promptActivityParcel = savedInstanceState.getParcelable("promptActivityParcel");
        } else {
            try {
                Class clazz = Class.forName("com.excelliance.lbsdk.main.PromptActivityParcel");
                Constructor cons = clazz.getDeclaredConstructor(Parcel.class);
                cons.setAccessible(true);
                promptActivityParcel = (Parcelable) cons.newInstance((Parcel) null);
            } catch (Exception e) {
                Log.d(TAG, "no PromptActivityParcel");
                e.printStackTrace();
            }
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (promptActivityParcel != null) {
            callPromptActivityParcel("init", new Class[]{Activity.class, Handler.class, Bundle.class}, new Object[]{this, handler, savedInstanceState});
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        Intent intent = getIntent();
        Log.d(TAG, "onResume" + intent.getIntExtra("dialogtype", 1));
        Serializable detail = intent.getSerializableExtra("detail");

        switch (intent.getIntExtra("dialogtype", 1)) {
            case MSG_INSTALL_DIALOG:
                Log.d(TAG, "show install dialog...");
                String force = intent.getStringExtra("forceUpdate");
                String apkPath = intent.getStringExtra("apkPath");
                showInstallDialog(apkPath, force);
                break;
            case MSG_DOWNLOAD_DIALOG:
                showDownloadedDialog(detail);
                break;
            case MSG_UPDATE_AND_NOTSPACE:
                boolean dataConnection = intent.getBooleanExtra("dataConnection", false);
                String forceUpdate = intent.getStringExtra("forceUpdate");
                long size = intent.getLongExtra("size", (long) 0);
                long expSize = intent.getLongExtra("expsize",(long)0);
                String savePath = intent.getStringExtra("savePath");
                if (savePath != null) {
                    boolean alreadyDownloaded = ((Boolean) callPromptActivityParcel("alreadyDownloaded", new Class[]{Serializable.class}, new Object[]{detail})).booleanValue();
                    if (!alreadyDownloaded) {
                        boolean ncKillSelf = intent.getBooleanExtra("quitKillSelf", true);
                        boolean stopOtherDl = intent.getBooleanExtra("stopOtherDl", false);
                        showUpdateDialog(detail, dataConnection, forceUpdate, size + expSize, ncKillSelf, stopOtherDl);
                    } else {
                        callPromptActivityParcel("restartGame", new Class[]{Serializable.class}, new Object[]{detail});
                    }
                } else if (forceUpdate != null && size > 0) {
                    Log.d(TAG, "space not enough");
                    showNotEnoughSpaceDialog(forceUpdate, size + expSize);
                }
                break;
            case MSG_BWBX_FIRST:
                boolean zipExists = intent.getBooleanExtra("zipExists", false);
                dataConnection = intent.getBooleanExtra("dataConnection", false);
                Log.d(TAG, "zipExists:" + zipExists + " dataConnection:" + dataConnection);
                showDownloadAll(intent.getLongExtra("size", (long) 0), zipExists, dataConnection);
                break;
            case MSG_CHOOSE_BY_USER:
                showChooseBwbx(intent.getLongExtra("size", (long) 0), intent.getIntExtra("dlsZipType", 1));
                break;
            case MSG_MERGE_ERR_DIALOG:
                int mergeErrType = intent.getIntExtra("mergeErrType", 0);
                String errContent = intent.getStringExtra("errContent");
                int mainChId = intent.getIntExtra("MainChId",0);
                if (mergeErrType != 0)
                    showMergeErrDialog(mergeErrType, errContent, mainChId);
                break;
            case MSG_RESTART_FOR_JJ:
                showRestartDialogForJJ();
                break;
            case MSG_LOADING_DIALOG:
                int type = intent.getIntExtra("type", 0);
                boolean restart = intent.getBooleanExtra("restart", false);
                if (type == 1) {
                    if (restart)
                        callPromptActivityParcel("restartGame", new Class[]{Serializable.class}, new Object[]{null});
                } else {
                    callPromptActivityParcel("loadingPackage", null, null);
                }
                showLoadingDialog();
                break;
            case MSG_SPACE_NOT_ENOUGH:
                long requiredSize = intent.getLongExtra("requiredSize", (long) 0);
                showSpaceNotEnough(requiredSize);
                break;
            case MSG_NETWORK_USELESS:
                showNotNetworkDialog();
                break;
            case MSG_NOT_FORCE_UPDATE:
                String dealPath = intent.getStringExtra("dealpath");
                boolean fu = intent.getBooleanExtra("forceUpdate",true);
                showDloadFromWebsite(dealPath,fu);
                break;
            case MSG_RES_PATCH:
                int dlsZipType = intent.getIntExtra("dlsZipType", 1);
                Bundle bundle = intent.getExtras();
                long patchSize = bundle.getLong("rSize",0);
                boolean dataConnect = bundle.getBoolean("dataConnect",false);
                boolean forceDl = bundle.getBoolean("forceDl", false);
                showChooseResPatch(patchSize, dataConnect, dlsZipType, forceDl, bundle);
                break;
        }
        Log.e(TAG, "callPromptActivityParcel");
        callPromptActivityParcel("onResume", null, null);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (promptActivityParcel != null) {
            outState.putParcelable("promptActivityParcel", promptActivityParcel);
        }
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        callPromptActivityParcel("onPause", null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        callPromptActivityParcel("onDestroy", null, null);
    }

    // --------------------------------start----------------------------
    private void showDownloadedDialog(final Serializable detail) {
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_downloaded_message_body", "string", getPackageName());
        builder.setMessage(getResources().getString(resId));
        resId = getResources().getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));

        resId = getResources().getIdentifier("lebian_downloaded_current", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                callPromptActivityParcel("restartGame", new Class[]{Serializable.class}, new Object[]{detail});
            }
        });

        resId = getResources().getIdentifier("lebian_downloaded_next", "string", getPackageName());
        builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        Dialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            updateDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!updateDialog.isShowing() && !isFinishing()) {
            updateDialog.show();
        }
    }


    private void showInstallDialog(final String apkPath, final String forceUpdate) {
        String packageName = getPackageName();
        Dialog installDialog = null;
        final boolean fUpdate = (forceUpdate == null || forceUpdate.equals("1"));
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier(fUpdate ? "lebian_update_available_force_update_hint" : "lebian_update_available_update_hint", "string", packageName);
        builder.setMessage(getResources().getString(resId));
        resId = getResources().getIdentifier("lebian_update_available", "string", packageName);
        builder.setTitle(getResources().getString(resId));

        resId = getResources().getIdentifier("lebian_install_yes", "string", packageName);
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = (Uri) callPromptActivityParcel("getOverInstallUri", new Class[]{String.class}, new Object[]{apkPath});
                if (fUpdate) {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    callPromptActivityParcel("quitGame", null, null);
                    startActivity(intent);
                } else {
                    dialog.dismiss();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivity(intent);
                    finish();
                }
            }
        });

        resId = getResources().getIdentifier(fUpdate ? "lebian_quit_game" : "lebian_exit_dialog_no", "string", packageName);
        builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (fUpdate) {
                    callPromptActivityParcel("quitGame", null, null);
                } else {
                    finish();
                }
            }
        });

        if (!isFinishing()) {
            installDialog = builder.create();
            installDialog.setCancelable(false);
            installDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                installDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            installDialog.show();
        }

    }
    // --------------------------------end----------------------------

    private void showUpdateDialog(final Serializable detail, final boolean dataConnection, final String force, final long size, final boolean ncKillSelf, final boolean stopOtherDl) {
        Log.d(TAG, "showUpdateDialog enter detail:" + detail + " dataConnection:" + dataConnection + " ncKillSelf:" + ncKillSelf + " stopOtherDl:" + stopOtherDl);
        final boolean forceUpdate = force.equals("1");
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_dl_update_without_wifi", "string", getPackageName());
        int resId2 = getResources().getIdentifier("lebian_dl_update_wifi", "string", getPackageName());
        builder.setMessage(String.format(getResources().getString(dataConnection ? resId : resId2), ((float) (size)) / (1024.0f * 1024.0f)));

        resId = getResources().getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));
        if (!forceUpdate) {
            resId = getResources().getIdentifier("lebian_next_no_prompt", "string", getPackageName());
            builder.setCheckBox(getResources().getString(resId), false, new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    getSharedPreferences("excl_lb_prompt", MODE_PRIVATE).edit().putBoolean("gameUpdate", isChecked).commit();
                }
            });
        }
        resId = getResources().getIdentifier("lebian_button_ok", "string", getPackageName());
        resId2 = getResources().getIdentifier("lebian_download_background", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(forceUpdate ? resId : resId2), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (forceUpdate) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO)
                        getSharedPreferences("excl_lb_gameInfo", Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS).edit().remove("runningGameId").commit();
                    else
                        getSharedPreferences("excl_lb_gameInfo", Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS).edit().remove("runningGameId").apply();
                    getSharedPreferences("excl_lb_gameInfo", Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS).edit().putBoolean("selfKill", true).commit();
                    boolean alreadyDownloaded = ((Boolean) callPromptActivityParcel("alreadyDownloaded", new Class[]{Serializable.class}, new Object[]{detail})).booleanValue();
                    if (!alreadyDownloaded) {
                        callPromptActivityParcel("switchToUpdate", new Class[]{Serializable.class, boolean.class, boolean.class}, new Object[]{detail, ncKillSelf, stopOtherDl});
                    } else {
                        callPromptActivityParcel("restartGame", new Class[]{Serializable.class}, new Object[]{detail});
                    }
                } else {
                    boolean alreadyDownloaded = ((Boolean) callPromptActivityParcel("alreadyDownloaded", new Class[]{Serializable.class}, new Object[]{detail})).booleanValue();
                    if (!alreadyDownloaded) {
                        callPromptActivityParcel("downloadComponent", new Class[]{Serializable.class}, new Object[]{detail});
                        finish();
                    }
                }
                getSharedPreferences("excl_lb_prompt", MODE_PRIVATE).edit().putBoolean("gameUpdate", false).commit();
            }
        });

        resId = getResources().getIdentifier("lebian_quit_game", "string", getPackageName());
        resId2 = getResources().getIdentifier("lebian_no_update", "string", getPackageName());
        builder.setNegativeButton(getResources().getString(forceUpdate ? resId : resId2), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                boolean alreadyDownloaded = ((Boolean) callPromptActivityParcel("alreadyDownloaded", new Class[]{Serializable.class}, new Object[]{detail})).booleanValue();
                if (GlobalSettings.DOWNLOAD_AFTER_QUIT && !alreadyDownloaded && !dataConnection && forceUpdate)
                    callPromptActivityParcel("downloadComponent", new Class[]{Serializable.class, Boolean.class}, new Object[]{detail, true});
                finish();
                if (forceUpdate)
                    callPromptActivityParcel("quitGame", null, null);
            }
        });

        Dialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            updateDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!updateDialog.isShowing() && !isFinishing()) {
            updateDialog.show();
        }
    }

    private void showNotEnoughSpaceDialog(final String force, final long size) {
        boolean hasSD = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_not_enought_space", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));
        long requiredSize = size;
        if (!hasSD)
            requiredSize += (20 << 20);
        resId = getResources().getIdentifier("lebian_storage_space_requirement_dload", "string", getPackageName());
        int resId2 = getResources().getIdentifier("lebian_storage_sd", "string", getPackageName());
        int resId3 = getResources().getIdentifier("lebian_storage_phone", "string", getPackageName());
        String body = String.format(getResources().getString(resId), requiredSize / (float) (1024 * 1024), hasSD ? getResources().getString(resId2) : getResources().getString(resId3));
        builder.setMessage(body);

        resId = getResources().getIdentifier("lebian_button_ok", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                if (force.equals("1"))
                    callPromptActivityParcel("quitGame", null, null);

            }
        });
        Dialog notEnoughSpaceDialog = builder.create();
        notEnoughSpaceDialog.setCancelable(false);
        notEnoughSpaceDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            notEnoughSpaceDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!notEnoughSpaceDialog.isShowing() && !isFinishing()) {
            notEnoughSpaceDialog.show();
        }
    }

    private void showDownloadAll(long size, final boolean zipExists, final boolean dataConnection) {
        Log.d(TAG, "showDownloadAll ENTER");

        Resources res = getResources();
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = res.getIdentifier("lebian_download_all_note_no_size", "string", getPackageName());
        int resId1 = res.getIdentifier("lebian_download_all_note_no_size_al", "string", getPackageName());
        builder.setMessage(String.format(getResources().getString(dataConnection ? resId : resId1), ((float) (size)) / (1024.0f * 1024.0f)));
        resId = res.getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(res.getString(resId));
        resId = res.getIdentifier("lebian_download_background", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                if (!zipExists)
                    sendBroadcast(new Intent(getPackageName() + ".action.DOWNLOAD_ALL"));
                else
                    BaseUtil.switchToDownloadFullRes(PromptActivity.this);
                click(BwbxUtilHelper.getInt(PromptActivity.this, "DIALOG_TYPE_FIRST", 0), 1);
            }
        });

        resId = res.getIdentifier("lebian_download_later", "string", getPackageName());
        builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                sendBroadcast(new Intent(getPackageName() + ".action.DOWNLOAD_ALL_LATER"));
                click(BwbxUtilHelper.getInt(PromptActivity.this, "DIALOG_TYPE_FIRST", 0), 2);
            }
        });

        Dialog downloadAll = builder.create();
        downloadAll.setCancelable(false);
        downloadAll.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (!downloadAll.isShowing()) {
            downloadAll.show();
        }

    }

    private void showChooseBwbx(long size, final int zipType) {
        Log.d(TAG, "showChooseBwbx ENTER");

        Resources res = getResources();
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = res.getIdentifier("lebian_choose_by_user_bwbx", "string", getPackageName());
        if (zipType == 2)
            resId = res.getIdentifier("lebian_choose_by_user_twice_load", "string", getPackageName());
        builder.setMessage(String.format(getResources().getString(resId), ((float) (size)) / (1024.0f * 1024.0f)));
        resId = res.getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(res.getString(resId));
        resId = res.getIdentifier("lebian_download_now", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                click(BwbxUtilHelper.getInt(PromptActivity.this, "DIALOG_TYPE_OLD_USER_FIRST", 0), 1);
                BaseUtil.switchToDownloadFullRes(PromptActivity.this, zipType);
            }
        });

        if (!GlobalSettings.CHOOSE_BY_USER_BWBX_FORCE) {
            resId = res.getIdentifier("lebian_quit_game", "string", getPackageName());
            builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    click(BwbxUtilHelper.getInt(PromptActivity.this, "DIALOG_TYPE_OLD_USER_FIRST", 0), 2);
                    callPromptActivityParcel("quitGame", null, null);
                }
            });
        }
        Dialog chooseBwbx = builder.create();
        chooseBwbx.setCancelable(false);
        chooseBwbx.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (!chooseBwbx.isShowing()) {
            chooseBwbx.show();
        }

    }

    private void showMergeErrDialog(int mergeErrType, String errContent, int mainChId) {//mergeErrType 1 is permissionError, 2 is provider error
        Log.d(TAG, "showMergeErrDialog enter");
        CustomDialog.Builder builder = new CustomDialog.Builder(this);

        int resId = -1;
        if (mergeErrType == 1)
            resId = getResources().getIdentifier("lebian_permiss_missing", "string", getPackageName());
        if (mergeErrType == 2)
            resId = getResources().getIdentifier("lebian_authorities_error", "string", getPackageName());
        if (mergeErrType == 3)
            resId = getResources().getIdentifier("lebian_lebian_flag_missing", "string", getPackageName());
        if (mergeErrType == 4)
            resId = getResources().getIdentifier("lebian_main_chid_err", "string", getPackageName());

        if(mergeErrType == 1 || mergeErrType == 2 ||mergeErrType == 3)
            builder.setMessage(getResources().getString(resId) + errContent);
        if(mergeErrType == 4)
            builder.setMessage(String.format(getResources().getString(resId), errContent,mainChId));

        resId = getResources().getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));

        resId = getResources().getIdentifier("lebian_button_ok", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                callPromptActivityParcel("quitGame", null, null);
            }
        });

        Dialog permissionDialog = builder.create();
        permissionDialog.setCancelable(false);
        permissionDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            permissionDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!permissionDialog.isShowing() && !isFinishing()) {
            permissionDialog.show();
        }
    }

    private void showRestartDialogForJJ() {
        Log.d(TAG, "showRestartDialogForJJ enter");
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_for_jj_restart", "string", getPackageName());

        builder.setMessage(getResources().getString(resId));

        resId = getResources().getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));

        resId = getResources().getIdentifier("lebian_button_ok", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                callPromptActivityParcel("quitGame", null, null);
            }
        });

        Dialog restartDialog = builder.create();
        restartDialog.setCancelable(false);
        restartDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            restartDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!restartDialog.isShowing() && !isFinishing()) {
            restartDialog.show();
        }
    }

    private void showChooseResPatch(long size, boolean dataConnection, final int dlsZipType, final boolean forceDl, final Bundle patchInfo) {
        Log.d(TAG, "showChooseResPatch ENTER");

        Resources res = getResources();
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = res.getIdentifier(dataConnection ? "lebian_download_patch_res_without_wifi" : "lebian_download_patch_res", "string", getPackageName());
        builder.setMessage(String.format(getResources().getString(resId), ((float) (size)) / (1024.0f * 1024.0f)));
        resId = res.getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(res.getString(resId));
        resId = res.getIdentifier("lebian_download_now", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                BaseUtil.switchToDownloadFullRes(PromptActivity.this, dlsZipType, patchInfo);
            }
        });

        resId = res.getIdentifier(forceDl ? "lebian_quit_game" : "lebian_continue_game", "string", getPackageName());
        builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                if (forceDl)
                    callPromptActivityParcel("quitGame", null, null);
                else
                    callPromptActivityParcel("bwbxContinue", null, null);
            }
        });
        Dialog chooseBwbx = builder.create();
        chooseBwbx.setCancelable(false);
        chooseBwbx.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        if (!chooseBwbx.isShowing()) {
            chooseBwbx.show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        callPromptActivityParcel("onNewIntent", new Class[]{Intent.class}, new Object[]{intent});
    }

    private void showLoadingDialog() {
        Log.d(TAG, "showLoadingDialog enter");
        String packageName = getPackageName();
        Dialog loadDialog = new Dialog(this);

        this.getWindow().setBackgroundDrawableResource(getResources().getIdentifier("lebian_background", "drawable", packageName));

        Resources res = getResources();
        int resId = res.getIdentifier("lebian_progress_dialog", "layout", packageName);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(resId, null, false);

        resId = res.getIdentifier("lebian_outer_icon", "id", packageName);
        if (resId > 0) {
            ImageView outer = (ImageView) view.findViewById(resId);
            resId = res.getIdentifier("lebian_outer_circle", "drawable", packageName);
            outer.setImageDrawable(res.getDrawable(resId));
            resId = res.getIdentifier("lebian_outer_animator", "anim", packageName);
            if (resId > 0) {
                Animation outerAnim = AnimationUtils.loadAnimation(this, resId);
                outer.startAnimation(outerAnim);
            }
        }

        resId = res.getIdentifier("lebian_inner_icon", "id", packageName);
        if (resId > 0) {
            ImageView inner = (ImageView) view.findViewById(resId);
            resId = res.getIdentifier("lebian_inner_circle", "drawable", packageName);
            inner.setImageDrawable(res.getDrawable(resId));
            resId = res.getIdentifier("lebian_inner_animator", "anim", packageName);
            if (resId > 0) {
                Animation innerAnim = AnimationUtils.loadAnimation(this, resId);
                inner.startAnimation(innerAnim);
            }
        }

        resId = res.getIdentifier("lebian_center_icon", "id", packageName);
        if (resId > 0) {
            ImageView center = (ImageView) view.findViewById(resId);
            resId = res.getIdentifier("lebian_center_img", "drawable", packageName);
            center.setImageDrawable(res.getDrawable(resId));
            resId = res.getIdentifier("lebian_center_animator", "anim", packageName);
            if (resId > 0) {
                Animation centerAnim = AnimationUtils.loadAnimation(this, resId);
                center.startAnimation(centerAnim);
            }
        }

        resId = res.getIdentifier("lebian_text_progress_loading", "id", packageName);
        TextView tips = (TextView) view.findViewById(resId);
        resId = res.getIdentifier("lebian_loading_for_install", "string", packageName);
        tips.setText(resId);

        loadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        loadDialog.setContentView(view);
        loadDialog.setCancelable(false);
        loadDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            loadDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        loadDialog.show();
    }


    public void showSpaceNotEnough(long requiredSize){
        boolean hasSD = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_not_enought_space", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));

        if (!hasSD)
            requiredSize += (20 << 20);
        resId = getResources().getIdentifier("lebian_storage_space_requirement_dload", "string", getPackageName());
        int resId2 = getResources().getIdentifier("lebian_storage_sd", "string", getPackageName());
        int resId3 = getResources().getIdentifier("lebian_storage_phone", "string", getPackageName());
        String body = String.format(getResources().getString(resId), requiredSize / (float) (1024 * 1024), hasSD ? getResources().getString(resId2) : getResources().getString(resId3));
        builder.setMessage(body);

        resId = getResources().getIdentifier("lebian_quit_game", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                callPromptActivityParcel("quitGame", null, null);
            }
        });

        Dialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            updateDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!updateDialog.isShowing() && !isFinishing()) {
            updateDialog.show();
        }
    }

    public void showNotNetworkDialog(){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_network_error", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));
        resId = getResources().getIdentifier("lebian_enable_internet_access", "string", getPackageName());
        builder.setMessage(getResources().getString(resId));

        resId = getResources().getIdentifier("lebian_enable_now", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                callPromptActivityParcel("quitGame", null, null);
            }
        });

        Dialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            updateDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!updateDialog.isShowing() && !isFinishing()) {
            updateDialog.show();
        }
    }

    public void showDloadFromWebsite(final String dealPath,boolean forceUpdate){
        CustomDialog.Builder builder = new CustomDialog.Builder(this);
        int resId = getResources().getIdentifier("lebian_hint", "string", getPackageName());
        builder.setTitle(getResources().getString(resId));
        resId = getResources().getIdentifier("lebian_new_version_available", "string", getPackageName());
        builder.setMessage(getResources().getString(resId));
        resId = getResources().getIdentifier("lebian_website_download", "string", getPackageName());
        builder.setPositiveButton(getResources().getString(resId), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent dealIntent = new Intent(Intent.ACTION_VIEW);
                dealIntent.setData(Uri.parse(dealPath));
                startActivity(dealIntent);
                finish();
            }
        });

        if (forceUpdate) {
            resId = getResources().getIdentifier("lebian_quit_game", "string", getPackageName());
            builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    callPromptActivityParcel("quitGame", null, null);
                }
            });
        }else{
            resId = getResources().getIdentifier("lebian_continue_game", "string", getPackageName());
            builder.setNegativeButton(getResources().getString(resId), new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        Dialog updateDialog = builder.create();
        updateDialog.setCancelable(false);
        updateDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            updateDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        if (!updateDialog.isShowing() && !isFinishing()) {
            updateDialog.show();
        }
    }

    public Resources getResources() {
        if (promptActivityParcel != null)
            return (Resources) callPromptActivityParcel("getResources", null, null);

        return super.getResources();
    }

    private Object callPromptActivityParcel(String name, Class[] type, Object[] args) {

        Parcelable promp=promptActivityParcel;
        try {
            Class clazz = Class.forName("com.excelliance.lbsdk.main.PromptActivityParcel");
            Method method = clazz.getDeclaredMethod(name, type);
            method.setAccessible(true);
            return method.invoke(promp, args);
        } catch (Exception e) {
            Log.d(TAG, "no PromptActivityParcel");
            e.printStackTrace();
        }

        return null;
    }

    private void click(final int dType, final int cType) {
        Intent intent = new Intent(getPackageName() + BwbxUtilHelper.getString(this, "ACTION_USER_CLICK", ""));
        intent.setPackage(getPackageName());
        intent.putExtra("dtype", dType);
        intent.putExtra("ctype", cType);
        sendBroadcast(intent);
    }

}
