package com.mantic.control.api.netizen;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class NetizenOperatorRetrofit {
    private static NetizenOperatorRetrofit instance;
    //    private Context mContext;
    private Retrofit retrofit;

    private NetizenOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(NetizenOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static NetizenOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (NetizenOperatorRetrofit.class) {
                instance = new NetizenOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
