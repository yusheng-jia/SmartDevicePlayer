package com.mantic.control.data.jd;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/31.
 * desc:
 */
public class NetUtil {
    private static final String TAG = "NetUtil";

    public static void get(String url, Callback callback) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static void post(String url, String content, Callback callback) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body= RequestBody.create(MediaType.parse("text/html"),content);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    public static String getCurrentDateTime(){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());
    }
}
