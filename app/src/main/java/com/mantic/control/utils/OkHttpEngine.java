package com.mantic.control.utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.baidu.iot.sdk.IoTSDKManager;
import com.mantic.control.ManticApplication;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpEngine {
    private static String TAG="OkHttpEngine";
    private static OkHttpEngine mInstance;
    private OkHttpClient client;
    private Handler mHandler;

    public static OkHttpEngine getInstance(Context context){
        if(mInstance==null){
            synchronized (OkHttpEngine.class){
                mInstance=new OkHttpEngine(context);
            }
        }
        return  mInstance;
    }

    private OkHttpEngine(Context context){
        File sdcache=context.getExternalCacheDir();
        int cacheSize=10*1024*1024;
        OkHttpClient.Builder builder=new OkHttpClient.Builder()
                .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)
                .cache(new Cache(sdcache.getAbsoluteFile(),cacheSize));
        client=builder.build();
        mHandler=new Handler();
    }
    public void getAsynHttp(String url, ResultCallback callback){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        final Request request=new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + accessToken)
                .addHeader("Lc", ManticApplication.channelId)
                .build();
        Call call=client.newCall(request);
        dealResult(call,callback);
    }

    public void postAsynHttp(String json,String url, ResultCallback callback){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();

        Request request=new Request.Builder()
                .url(url)
                .addHeader("Authorization","Bearer " + accessToken)
                .addHeader("Lc", ManticApplication.channelId)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json))
                .build();
        Call call=client.newCall(request);

        dealResult(call,callback);
    }

    private void dealResult(Call call,final ResultCallback callback){
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedCallback(call.request(),e,callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallback(response,callback);
            }
        });
    }

    private void sendSuccessCallback(final Response response,final ResultCallback callback){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    try{
                        callback.onResponse(response);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    private void sendFailedCallback(final Request request, final Exception ex, final ResultCallback callback){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if(callback!=null){
                    callback.onError(request,ex);
                }
            }
        });
    }


    public static class Param {

        public String key;
        public String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }
}
