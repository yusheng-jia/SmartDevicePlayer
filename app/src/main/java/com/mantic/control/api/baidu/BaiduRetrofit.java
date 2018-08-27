package com.mantic.control.api.baidu;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class BaiduRetrofit {
    private static BaiduRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private BaiduRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(BaiduUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static BaiduRetrofit getInstance() {
        if (instance == null) {
            synchronized (BaiduRetrofit.class) {
                instance = new BaiduRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
