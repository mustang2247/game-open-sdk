package com.openapi.apkdownload.tools;

import android.content.Context;
import android.util.TypedValue;

public class DensityUtil {

    public static int dp2px(Context paramContext, float paramFloat) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, paramFloat, paramContext.getResources().getDisplayMetrics());
    }

    public static float px2dp(Context paramContext, float paramFloat) {
        return paramFloat / paramContext.getResources().getDisplayMetrics().density;
    }

    public static float px2sp(Context paramContext, float paramFloat) {
        return paramFloat / paramContext.getResources().getDisplayMetrics().scaledDensity;
    }

    public static int sp2px(Context paramContext, float paramFloat) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, paramFloat, paramContext.getResources().getDisplayMetrics());
    }
}