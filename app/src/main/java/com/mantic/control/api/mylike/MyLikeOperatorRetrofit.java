package com.mantic.control.api.mylike;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class MyLikeOperatorRetrofit {
    private static MyLikeOperatorRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private MyLikeOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(MyLikeOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static MyLikeOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (MyLikeOperatorRetrofit.class) {
                instance = new MyLikeOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
