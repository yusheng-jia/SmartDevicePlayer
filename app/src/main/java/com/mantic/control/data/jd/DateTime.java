package com.mantic.control.data.jd;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/31.
 * desc:
 */
public class DateTime {
    @SuppressLint("SimpleDateFormat")
    public static String jdFormatDateTime(){
        Date currentTime = new Date();
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
    }
}
