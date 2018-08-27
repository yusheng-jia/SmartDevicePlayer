package com.mantic.antservice.util;

import android.util.Log;

/**
 * Created by Jia on 2017/5/4.
 */

public class Logger {
    private static final boolean DEBUG = true;
    public static final String TAG = "antservice";

    public static void i(String secTag, String message){
        if (DEBUG){
            Log.i(TAG,secTag +":-->" +message);
        }
    }
}
