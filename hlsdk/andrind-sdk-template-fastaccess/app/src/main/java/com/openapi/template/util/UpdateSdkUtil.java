package com.openapi.template.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.openapi.template.Constants;
import com.openapi.template.cb.ChannelInterfaceProxy;

/**
 * Created by Administrator on 2016/12/6.
 * SDK更新
 */
public class UpdateSdkUtil {

    /**
     * SDK更新
     * @param context
     * @param currentVersionCode
     * @param initConfigResponse
     * @param appName
     */
    public static void updateSdkVersion(Context context, String currentVersionCode, String initConfigResponse, final String appName, ChannelInterfaceProxy.ApplicationInitCallback callback) {
        try {
            JSONObject dynamicConfig = JSON.parseObject(initConfigResponse);
            String update_version = dynamicConfig.getString("updateVersion");
            Integer update_type = dynamicConfig.getInteger("updateType");
            String update_url = dynamicConfig.getString("updateUrl");
            String updateMsg = dynamicConfig.getString("updateMsg");
            if (update_type == null || Strings.isNullOrEmpty(update_version) || Strings.isNullOrEmpty(update_url)) {
                Log.i(Constants.tag, "updateSdkVersion isNullOrEmpty2");
                callback.execute();
                return;
            }
            if (update_type != Constants.TIP_UPDATE && update_type != Constants.FORCE_UPDATE) {
                Log.i(Constants.tag, "updateSdkVersion 3");
                callback.execute();
                return;
            }
            checkWifiAndAlert(context, currentVersionCode, update_version, update_type, update_url, updateMsg, appName);
        } catch (Exception e) {
            Log.e("updateSdkVersion Fail ", e.getMessage());
            callback.execute();
        }
    }

    /**
     * 检查网络并提示
     * @param context
     * @param currentVersionCode
     * @param update_version
     * @param update_type
     * @param update_url
     * @param updateMsg
     * @param appName
     */
    private static void checkWifiAndAlert(final Context context, final String currentVersionCode, final String update_version, final Integer update_type, final String update_url, final String updateMsg, final String appName) {
        if (APNUtil.checkNetworkType(context) == APNUtil.TYPE_WIFI) {
            // 如果wifi直接更新
            updateStart(context, currentVersionCode, update_version, update_type, update_url, updateMsg, appName);
            return;
        }

        // 不是wifi弹出提示
        final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).create();

        LinearLayout linearLayoutRoot = new LinearLayout(context);
        linearLayoutRoot.setPadding(dipToPx(context, 15), dipToPx(context, 15), dipToPx(context, 15), dipToPx(context, 15));
        linearLayoutRoot.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);
        linearLayoutRoot.setBackgroundColor(0xFFffffff);
        // 标题
        TextView title = new TextView(context);
        title.setText("版本更新");
        title.setTextColor(0xFF000000);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        LinearLayout.LayoutParams titleLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutRoot.addView(title, titleLL);
        // 分割线
        View view = new View(context);
        view.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams vLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        vLL.setMargins(0, dipToPx(context, 15), 0, dipToPx(context, 20));
        linearLayoutRoot.addView(view, vLL);
        // 内容
        TextView message = new TextView(context);
        message.setText("当前不是Wifi网路环境，是否继续更新？");
        message.setTextColor(0xFF000000);
        message.setLineSpacing(0, 1.3f);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams mLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLL.bottomMargin = dipToPx(context, 20);
        linearLayoutRoot.addView(message, mLL);
        // 两个按钮
        LinearLayout btnLayout = new LinearLayout(context);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button nextBtn = new Button(context);
        nextBtn.setBackgroundColor(0xff3189ea);
        nextBtn.setText("取消");
        nextBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        nextBtn.setTextColor(0xFFffffff);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        LinearLayout.LayoutParams nextLL = new LinearLayout.LayoutParams(0, dipToPx(context, 40));
        nextLL.rightMargin = dipToPx(context, 10);
        nextLL.weight = 1;
        btnLayout.addView(nextBtn, nextLL);

        Button updateBtn = new Button(context);
        updateBtn.setBackgroundColor(0xff3189ea);
        updateBtn.setText("更新");
        updateBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        updateBtn.setTextColor(0xFFffffff);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                updateStart(context, currentVersionCode, update_version, update_type, update_url, updateMsg, appName);
            }
        });
        LinearLayout.LayoutParams updateLL = new LinearLayout.LayoutParams(0, dipToPx(context, 40));
        updateLL.weight = 1;
        btnLayout.addView(updateBtn, updateLL);

        linearLayoutRoot.addView(btnLayout);

        alertDialog.setCancelable(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        ViewGroup.LayoutParams rootParams = new ViewGroup.LayoutParams(dipToPx(context, 320), ViewGroup.LayoutParams.WRAP_CONTENT);
        // 为了控制宽度并且在window中居中
        LinearLayout rootParentLayout = new LinearLayout(context);
        rootParentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        rootParentLayout.setBackgroundColor(0xFFffff11);
        rootParentLayout.addView(linearLayoutRoot, rootParams);
        window.setContentView(rootParentLayout);
    }

    /**
     * 开始更新
     * @param context
     * @param currentVersionCode
     * @param update_version
     * @param update_type
     * @param update_url
     * @param updateMsg
     * @param appName
     */
    private static void updateStart(final Context context, String currentVersionCode, String update_version, final Integer update_type, final String update_url, String updateMsg, final String appName) {
        String msg = "将版本" + currentVersionCode + "升级到" + update_version + "\n" + updateMsg;
        updateSdkAlert(context, msg, update_type == Constants.FORCE_UPDATE, new ChannelInterfaceProxy.ApplicationInitCallback() {
            @Override
            public void execute() {
                Log.i(Constants.tag, "updateSdkVersion 下次再说");
//                callback.execute();
            }
        }, new ChannelInterfaceProxy.ApplicationInitCallback() {
            @Override
            public void execute() {
                Intent intent = new Intent();
                intent.setAction("com.openapi.template.util.UpdateService");
                intent.setPackage(context.getPackageName());
                intent.putExtra(UpdateService.DOWNLOAD_URL, update_url);
                intent.putExtra(UpdateService.DOWNLOAD_APP_NAME, appName);
                context.startService(intent);

                if (update_type == Constants.TIP_UPDATE) {// 不强制更新
                    Log.i(Constants.tag, "updateSdkVersion onInitSuccess");
//                    callback.execute();
                } else {
                    Toast.makeText(context, "正在更新中，请稍后...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static void updateSdkAlert(Context context, String msg, final boolean forceUpdate, ChannelInterfaceProxy.ApplicationInitCallback applicationInitCallback, ChannelInterfaceProxy.ApplicationInitCallback initCallback) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).create();

        LinearLayout linearLayoutRoot = new LinearLayout(context);
        linearLayoutRoot.setPadding(dipToPx(context, 15), dipToPx(context, 15), dipToPx(context, 15), dipToPx(context, 15));
        linearLayoutRoot.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);
        linearLayoutRoot.setBackgroundColor(0xFFffffff);
        // 标题
        TextView title = new TextView(context);
        title.setText("版本更新");
        title.setTextColor(0xFF000000);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        LinearLayout.LayoutParams titleLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutRoot.addView(title, titleLL);
        // 分割线
        View view = new View(context);
        view.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams vLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        vLL.setMargins(0, dipToPx(context, 15), 0, dipToPx(context, 20));
        linearLayoutRoot.addView(view, vLL);
        // 内容
        TextView message = new TextView(context);
        message.setText(msg);
        message.setTextColor(0xFF000000);
        message.setLineSpacing(0, 1.3f);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams mLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLL.bottomMargin = dipToPx(context, 20);
        linearLayoutRoot.addView(message, mLL);
        // 两个按钮
        LinearLayout btnLayout = new LinearLayout(context);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        if (!forceUpdate) {
            Button nextBtn = new Button(context);
            nextBtn.setBackgroundColor(0xff3189ea);
            nextBtn.setText("下次再说");
            nextBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            nextBtn.setTextColor(0xFFffffff);
            nextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
//                    nextCallback.execute();
                }
            });
            LinearLayout.LayoutParams nextLL = new LinearLayout.LayoutParams(0, dipToPx(context, 40));
            nextLL.rightMargin = dipToPx(context, 10);
            nextLL.weight = 1;
            btnLayout.addView(nextBtn, nextLL);
        }

        final Button updateBtn = new Button(context);
        updateBtn.setBackgroundColor(0xff3189ea);
        updateBtn.setText("更新");
        updateBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        updateBtn.setTextColor(0xFFffffff);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!forceUpdate) {
                    alertDialog.cancel();
                } else {
                    updateBtn.setText("后台更新中...");
                    updateBtn.setEnabled(false);
                }
//                updateCallback.execute();
            }
        });
        LinearLayout.LayoutParams updateLL = new LinearLayout.LayoutParams(0, dipToPx(context, 40));
        updateLL.weight = 1;
        btnLayout.addView(updateBtn, updateLL);

        linearLayoutRoot.addView(btnLayout);

        alertDialog.setCancelable(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        ViewGroup.LayoutParams rootParams = new ViewGroup.LayoutParams(dipToPx(context, 320), ViewGroup.LayoutParams.WRAP_CONTENT);
        // 为了控制宽度并且在window中居中
        LinearLayout rootParentLayout = new LinearLayout(context);
        rootParentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        rootParentLayout.setBackgroundColor(0xFFffff11);
        rootParentLayout.addView(linearLayoutRoot, rootParams);
        window.setContentView(rootParentLayout);
    }

    public static int dipToPx(Context context, float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    public static void packageNameIllegal(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).create();

        LinearLayout linearLayoutRoot = new LinearLayout(context);
        linearLayoutRoot.setPadding(dipToPx(context, 15), dipToPx(context, 15), dipToPx(context, 15), dipToPx(context, 15));
        linearLayoutRoot.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayoutRoot.setOrientation(LinearLayout.VERTICAL);
        linearLayoutRoot.setBackgroundColor(0xFFffffff);
        // 标题
        TextView title = new TextView(context);
        title.setText("提示");
        title.setTextColor(0xFF000000);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        LinearLayout.LayoutParams titleLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayoutRoot.addView(title, titleLL);
        // 分割线
        View view = new View(context);
        view.setBackgroundColor(Color.RED);
        LinearLayout.LayoutParams vLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        vLL.setMargins(0, dipToPx(context, 15), 0, dipToPx(context, 20));
        linearLayoutRoot.addView(view, vLL);
        // 内容
        TextView message = new TextView(context);
        message.setText("验证包名失败，可能非正版渠道包!");
        message.setTextColor(0xFF000000);
        message.setLineSpacing(0, 1.3f);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        LinearLayout.LayoutParams mLL = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mLL.bottomMargin = dipToPx(context, 20);
        linearLayoutRoot.addView(message, mLL);
        // 两个按钮
        LinearLayout btnLayout = new LinearLayout(context);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);

        Button nextBtn = new Button(context);
        nextBtn.setBackgroundColor(0xff3189ea);
        nextBtn.setText("确定");
        nextBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        nextBtn.setTextColor(0xFFffffff);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        LinearLayout.LayoutParams nextLL = new LinearLayout.LayoutParams(0, dipToPx(context, 40));
        nextLL.rightMargin = dipToPx(context, 10);
        nextLL.weight = 1;
        btnLayout.addView(nextBtn, nextLL);

        linearLayoutRoot.addView(btnLayout);

        alertDialog.setCancelable(false);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        ViewGroup.LayoutParams rootParams = new ViewGroup.LayoutParams(dipToPx(context, 320), ViewGroup.LayoutParams.WRAP_CONTENT);
        // 为了控制宽度并且在window中居中
        LinearLayout rootParentLayout = new LinearLayout(context);
        rootParentLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//        rootParentLayout.setBackgroundColor(0xFFffff11);
        rootParentLayout.addView(linearLayoutRoot, rootParams);
        window.setContentView(rootParentLayout);
    }
}
