package com.mantic.control.utils;

import android.content.Context;
import android.widget.Toast;

public class IoTToastUtils {
    public static Toast toast = null;

    public static void showShort(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(text);
        }
        toast.show();
    }

    public static void showLong(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(text);
        }
        toast.show();
    }

    public static void showShort(Context context, int resourseId) {
        if (toast == null) {
            toast = Toast.makeText(context, resourseId, Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setText(resourseId);
        }
        toast.show();
    }

    public static void showLong(Context context, int resourseId) {
        if (toast == null) {
            toast = Toast.makeText(context, resourseId, Toast.LENGTH_LONG);
        } else {
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(resourseId);
        }
        toast.show();
    }

}
