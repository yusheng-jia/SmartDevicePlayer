package com.mantic.control.api.account;

import com.mantic.control.api.beiwa.BwRetrofit;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyUrl;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by jayson on 2017/6/20.
 */

public class AccountRetrofit {
    private static AccountRetrofit instance;
    //    private Context mContext;
    private Retrofit retrofit;
    private static final int DEFAULT_TIMEOUT = 10;

    private AccountRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder().baseUrl(AccountUrl.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static AccountRetrofit getInstance() {
        if (instance == null) {
            synchronized (BwRetrofit.class) {
                instance = new AccountRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }
}
