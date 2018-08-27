package com.mantic.control.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/12/6.
 * desc:
 */

public class IoTAppConfigMgr {
    private static final String PREFS_FILE_NAME = "iotconfig";
    private static final String PREFS_APP_SIGNATURE = "app_signature";
    private static final String PREFS_USER_INFO = "user_info";
    private static final String PREFS_IAM_COOKIE = "iam_cookie";

    private static SharedPreferences sPrefs;

    private static void initSharePrefences(Context ctx) {
        if (sPrefs == null) {
            sPrefs = ctx.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void setAppSignature(Context context, String value) {
        initSharePrefences(context);
        sPrefs.edit().putString(PREFS_APP_SIGNATURE, value).commit();
    }

    public static String getAppSignature(Context context) {
        initSharePrefences(context);
        return sPrefs.getString(PREFS_APP_SIGNATURE, "");
    }

    public static void setUserInfo(Context context, String value) {
        initSharePrefences(context);
        sPrefs.edit().putString(PREFS_USER_INFO, value).commit();
    }

    public static String getUserInfo(Context context) {
        initSharePrefences(context);
        return sPrefs.getString(PREFS_USER_INFO, "");
    }

    public static void setIAMCookie(Context context, String value) {
        initSharePrefences(context);
        sPrefs.edit().putString(PREFS_IAM_COOKIE, value).commit();
    }

    public static String getIAMCookie(Context context) {
        initSharePrefences(context);
        return sPrefs.getString(PREFS_IAM_COOKIE, "");
    }

    public static String getString(Context context, String key, String def) {
        initSharePrefences(context);
        return sPrefs.getString(key, def);
    }

    public static void putString(Context context, String key, String value) {
        initSharePrefences(context);
        sPrefs.edit().putString(key, value).commit();
    }

    public static long getLong(Context context, String key, long def) {
        initSharePrefences(context);
        return sPrefs.getLong(key, def);
    }

    public static void putLong(Context context, String key, long value) {
        initSharePrefences(context);
        sPrefs.edit().putLong(key, value).commit();
    }
}