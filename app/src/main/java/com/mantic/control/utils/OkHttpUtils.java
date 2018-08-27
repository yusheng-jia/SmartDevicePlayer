package com.mantic.control.utils;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.internal.$Gson$Types;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wujiangxia on 2017/3/7.
 */
public class OkHttpUtils {

    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private Handler mDelivery;
    private Gson mGson;

    private OkHttpUtils()  {
        //创建okHttpClient对象
        //    mOkHttpClient=new OkHttpClient();
        mOkHttpClient =new OkHttpClient.Builder().cookieJar(new CookieJar() {
            private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<HttpUrl, List<Cookie>>();

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                cookieStore.put(url, cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        })
                .build();
        mDelivery = new Handler(Looper.getMainLooper());
        mGson = new Gson();
    }

    public synchronized static OkHttpUtils  getmInstance() {
        if (mInstance == null) {
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }
    public void deliveryResult(final ResultCallback callback, Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Glog.v("wujx","deliveryResult onFailure:"+e);
                sendFailCallback(  callback,e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final String string = response.body().string();
                    Glog.v("wujx","deliveryResult onResponse callback.mType:"+callback.mType);
                    if (callback.mType == String.class) {
                        sendSuccessCallBack(callback,string );

                    } else {
//                        Object o = mGson.fromJson(string, callback.mType);
                        sendSuccessCallBack(callback,response );
                    }


                } catch (com.google.gson.JsonParseException e)//Json解析的错误
                {
                    Glog.v("wujx","deliveryResult onFailure catch JsonParseException:"+e);
                    sendFailCallback(callback, e);
                }
            }

        });
    }

    private void sendFailCallback(final ResultCallback callback, final Exception e) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    private void sendSuccessCallBack(final ResultCallback callback, final Object obj) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    Glog.v("wujx","sendSuccessCallBack:"+obj);
                    callback.onSuccess(obj);
                }
            }
        });
    }

    public static abstract class ResultCallback<T>
    {
        Type mType;

        public ResultCallback()
        {
            mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass)
        {
            Type superclass = subclass.getGenericSuperclass();
            if (superclass instanceof Class)
            {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        public abstract void onFailure( Exception e);

        public abstract void onSuccess(T response);
    }
    public static void get(String url, ResultCallback callback) {
        getmInstance().getRequest(url, callback);
    }
    public  void getRequest(String url, ResultCallback callback){

        //创建请求对象
        Request request=new Request.Builder().url(url).build();
        deliveryResult(callback,request);

    };
    private Request buildPostRequest(String url, List<Param> params) {
        FormBody.Builder para=new FormBody.Builder();
        for (Param param : params) {
            para.add(param.key, param.value);
        }
        RequestBody requestBody = para.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }




    /**
     * post请求
     * @param url       请求url
     * @param callback  请求回调
     * @param params    请求参数
     */
    public static void post(String url, List<Param> params, final ResultCallback callback) {
        getmInstance().postRequest(url, callback, params);
    }


    private void postRequest(String url, final ResultCallback callback, List<Param> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }


    public  void postRequestFile(String url, List<Param> params, File file) {


        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "head_image", fileBody)
 /*               .addFormDataPart("imagetype", imageType)
                .addFormDataPart("userphone", userPhone)*/
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call=mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }




    /**
     * post请求参数类
     */
    public static class Param {

        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }
}
