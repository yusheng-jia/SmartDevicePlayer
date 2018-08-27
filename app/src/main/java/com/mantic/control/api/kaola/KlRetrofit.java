package com.mantic.control.api.kaola;

import com.mantic.control.api.beiwa.BwRetrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/19.
 */

public class KlRetrofit {
    private static KlRetrofit instance;
    //    private Context mContext;
    private Retrofit retrofit;

    private KlRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(KaolaUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static KlRetrofit getInstance() {
        if (instance == null) {
            synchronized (BwRetrofit.class) {
                instance = new KlRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
