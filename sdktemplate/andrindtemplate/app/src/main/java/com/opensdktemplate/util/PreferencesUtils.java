package com.opensdktemplate.util;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class PreferencesUtils {

    private static String PREFERENCE_NAME = "hoolaiSPInfo";

    private PreferencesUtils() {
        throw new AssertionError();
    }

    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    public static boolean getBoolean(Context context, String key, boolean paramBoolean) {
        return context.getSharedPreferences(PREFERENCE_NAME, 0).getBoolean(key, paramBoolean);
    }

    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1.0F);
    }

    public static float getFloat(Context context, String key, float paramFloat) {
        return context.getSharedPreferences(PREFERENCE_NAME, 0).getFloat(key, paramFloat);
    }

    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    public static int getInt(Context context, String key, int paramInt) {
        return context.getSharedPreferences(PREFERENCE_NAME, 0).getInt(key, paramInt);
    }

    public static long getLong(Context context, String key) {
        return getLong(context, key, -1L);
    }

    public static long getLong(Context context, String key, long paramLong) {
        return context.getSharedPreferences(PREFERENCE_NAME, 0).getLong(key, paramLong);
    }

    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    public static String getString(Context context, String key, String paramString) {
        return context.getSharedPreferences(PREFERENCE_NAME, 0).getString(key, paramString);
    }

    public static boolean putBoolean(Context context, String key, boolean paramBoolean) {
        Editor localEditor = context.getSharedPreferences(PREFERENCE_NAME, 0).edit();
        localEditor.putBoolean(key, paramBoolean);
        return localEditor.commit();
    }

    public static boolean putFloat(Context context, String key, float paramFloat) {
        Editor localEditor = context.getSharedPreferences(PREFERENCE_NAME, 0).edit();
        localEditor.putFloat(key, paramFloat);
        return localEditor.commit();
    }

    public static boolean putInt(Context context, String key, int paramInt) {
        Editor localEditor = context.getSharedPreferences(PREFERENCE_NAME, 0).edit();
        localEditor.putInt(key, paramInt);
        return localEditor.commit();
    }

    public static boolean putLong(Context context, String key, long paramLong) {
        Editor localEditor = context.getSharedPreferences(PREFERENCE_NAME, 0).edit();
        localEditor.putLong(key, paramLong);
        return localEditor.commit();
    }

    public static boolean putString(Context context, String key, String paramString) {
        Editor localEditor = context.getSharedPreferences(PREFERENCE_NAME, 0).edit();
        localEditor.putString(key, paramString);
        return localEditor.commit();
    }

    public static boolean remove(Context context, String key) {
        Editor localEditor = context.getSharedPreferences(PREFERENCE_NAME, 0).edit();
        localEditor.remove(key);
        return localEditor.commit();
    }
}