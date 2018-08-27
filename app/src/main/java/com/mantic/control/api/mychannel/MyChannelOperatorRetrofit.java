package com.mantic.control.api.mychannel;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class MyChannelOperatorRetrofit {
    private static MyChannelOperatorRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private MyChannelOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(MyChannelOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static MyChannelOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (MyChannelOperatorRetrofit.class) {
                instance = new MyChannelOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
