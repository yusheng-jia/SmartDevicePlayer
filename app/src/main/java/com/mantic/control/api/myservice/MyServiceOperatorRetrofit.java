package com.mantic.control.api.myservice;

import com.mantic.control.api.mychannel.MyChannelOperatorUrl;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class MyServiceOperatorRetrofit {
    private static MyServiceOperatorRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private MyServiceOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(MyServiceOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static MyServiceOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (MyServiceOperatorRetrofit.class) {
                instance = new MyServiceOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
