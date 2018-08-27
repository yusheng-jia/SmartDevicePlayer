package com.mantic.control.api.channelplay;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class ChannelPlayOperatorRetrofit {
    private static ChannelPlayOperatorRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private ChannelPlayOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(ChannelPlayOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static ChannelPlayOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (ChannelPlayOperatorRetrofit.class) {
                instance = new ChannelPlayOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
