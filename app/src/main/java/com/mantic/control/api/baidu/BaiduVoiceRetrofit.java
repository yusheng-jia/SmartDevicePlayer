package com.mantic.control.api.baidu;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class BaiduVoiceRetrofit {
    private static BaiduVoiceRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private BaiduVoiceRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(BaiduVoiceUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static BaiduVoiceRetrofit getInstance() {
        if (instance == null) {
            synchronized (BaiduVoiceRetrofit.class) {
                instance = new BaiduVoiceRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
