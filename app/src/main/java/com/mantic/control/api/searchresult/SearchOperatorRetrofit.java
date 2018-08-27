package com.mantic.control.api.searchresult;

import com.mantic.control.api.mylike.MyLikeOperatorUrl;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jia on 2017/5/18.
 */

public class SearchOperatorRetrofit {
    private static SearchOperatorRetrofit instance;
//    private Context mContext;
    private Retrofit retrofit;

    private SearchOperatorRetrofit() {
        retrofit = new Retrofit.Builder().baseUrl(SearchOperatorUrl.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static SearchOperatorRetrofit getInstance() {
        if (instance == null) {
            synchronized (SearchOperatorRetrofit.class) {
                instance = new SearchOperatorRetrofit();
            }
        }
        return instance;
    }

    public <T> T create(Class<T> service) {
        return retrofit.create(service);
    }

}
