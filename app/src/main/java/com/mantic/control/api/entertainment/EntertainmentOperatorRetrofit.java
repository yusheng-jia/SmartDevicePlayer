package com.mantic.control.api.entertainment;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class EntertainmentOperatorRetrofit {
    private static EntertainmentOperatorRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private EntertainmentOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(EntertainmentOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static EntertainmentOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (EntertainmentOperatorRetrofit.class) {
                instance = new EntertainmentOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
