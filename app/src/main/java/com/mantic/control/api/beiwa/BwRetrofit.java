package com.mantic.control.api.beiwa;

import android.content.Context;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class BwRetrofit {
    private static BwRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private BwRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(BwUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static BwRetrofit getInstance() {
        if (instance == null) {
            synchronized (BwRetrofit.class) {
                instance = new BwRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
