package com.mantic.control.api.mopidy;

import com.mantic.control.api.beiwa.BwRetrofit;
import com.mantic.control.api.kaola.KaolaUrl;
import com.mantic.control.api.kaola.KlRetrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/22.
 */

public class MopidyRetrofit {
    private static MopidyRetrofit instance;
    //    private Context mContext;
    private Retrofit retrofit;

    private MopidyRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(MopidyUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static MopidyRetrofit getInstance() {
        if (instance == null) {
            synchronized (BwRetrofit.class) {
                instance = new MopidyRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
