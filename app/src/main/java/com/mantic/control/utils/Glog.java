package com.mantic.control.utils;

import android.util.Log;

/**
 * Created by root on 17-4-8.
 */
public class Glog {
    // 锁，是否关闭Log日志输出
    public static boolean DEBUG = true;
    private static final String TAG = "Mantic";
    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(TAG, tag + "---" + msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(TAG, tag + "---" + msg);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(TAG, tag + "---" + msg);
        }
    }

    public static void d(String tag, String msg, Throwable t) {
        if (DEBUG) {
            Log.d(TAG, tag + "---" + msg, t);
        }
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(TAG, tag + "---" + msg);
        }
    }

    public static void w(String tag, String msg, Throwable t) {
        if (DEBUG) {
            Log.w(TAG, tag + "---" + msg, t);
        }
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(TAG, tag + "---" + msg);
        }
    }
}
