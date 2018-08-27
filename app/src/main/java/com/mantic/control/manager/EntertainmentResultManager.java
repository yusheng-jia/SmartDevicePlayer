package com.mantic.control.manager;


import android.content.Context;

import com.google.gson.Gson;
import com.mantic.control.api.entertainment.EntertainmentOperatorRetrofit;
import com.mantic.control.api.entertainment.EntertainmentOperatorServiceApi;
import com.mantic.control.api.entertainment.bean.BannerListResultBean;
import com.mantic.control.api.entertainment.bean.NewSongListRequestBean;
import com.mantic.control.api.entertainment.bean.NewSongListRequestParams;
import com.mantic.control.api.entertainment.bean.NewSongResultBean;
import com.mantic.control.api.entertainment.bean.PopularSongListRequestBean;
import com.mantic.control.api.entertainment.bean.PopularSongListRequestParams;
import com.mantic.control.api.entertainment.bean.PopularSongResultBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/7/7.
 */

public class EntertainmentResultManager {
    private static EntertainmentResultManager instance = null;
    private EntertainmentOperatorServiceApi serviceApi;

    private EntertainmentResultManager() {
        serviceApi = EntertainmentOperatorRetrofit.getInstance().create(EntertainmentOperatorServiceApi.class);
    }

    public static EntertainmentResultManager getInstance() {
        if (instance == null) {
            synchronized (EntertainmentResultManager.class) {
                instance = new EntertainmentResultManager();
            }
        }
        return instance;
    }

    public void getBannerListResult(final Callback<BannerListResultBean> callback, Context context, int page, int pageSize) {
        Call<BannerListResultBean> getCall = serviceApi.postBannerListResultQuest(createGetBannerListRqBean(context, page, pageSize));
        getCall.enqueue(new Callback<BannerListResultBean>() {
            @Override
            public void onResponse(Call<BannerListResultBean> call, Response<BannerListResultBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<BannerListResultBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void getPopularSongListResult(final Callback<PopularSongResultBean> callback, Context context, int page, int pageSize) {
        Call<PopularSongResultBean> getCall = serviceApi.postPopularSongListResultQuest(createGetPopularSongListRqBean(context, page, pageSize));
        getCall.enqueue(new Callback<PopularSongResultBean>() {
            @Override
            public void onResponse(Call<PopularSongResultBean> call, Response<PopularSongResultBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<PopularSongResultBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getNewSongListResult(final Callback<NewSongResultBean> callback, Context context, int page, int pageSize) {
        Call<NewSongResultBean> getCall = serviceApi.postNewSongListResultQuest(createGetNewSongListRqBean(context, page, pageSize));
        getCall.enqueue(new Callback<NewSongResultBean>() {
            @Override
            public void onResponse(Call<NewSongResultBean> call, Response<NewSongResultBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<NewSongResultBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getNetzenListResult(final Callback<PopularSongResultBean> callback, Context context, int page, int pageSize) {
        Call<PopularSongResultBean> getCall = serviceApi.postNetizenListResultQuest(createGetNetizenListRqBean(context, page, pageSize));
        getCall.enqueue(new Callback<PopularSongResultBean>() {
            @Override
            public void onResponse(Call<PopularSongResultBean> call, Response<PopularSongResultBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<PopularSongResultBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    private RequestBody createGetBannerListRqBean(Context context, int page, int pageSize) {
        PopularSongListRequestBean bean = new PopularSongListRequestBean();
        bean.setMethod("core.library.browse_new");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        PopularSongListRequestParams params = new PopularSongListRequestParams();

        params.setPage(page);
        params.setPagesize(pageSize);
        params.setUri("organized:banner");
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetBannerListRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createGetPopularSongListRqBean(Context context, int page, int pageSize) {
        PopularSongListRequestBean bean = new PopularSongListRequestBean();
        bean.setMethod("core.library.browse_new");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        PopularSongListRequestParams params = new PopularSongListRequestParams();

        params.setPage(page);
        params.setPagesize(pageSize);
        params.setUri("organized:songlist:hot");
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetPopularSongListRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createGetNewSongListRqBean(Context context, int page, int pageSize) {
        NewSongListRequestBean bean = new NewSongListRequestBean();
        bean.setMethod("core.library.browse_new");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        NewSongListRequestParams params = new NewSongListRequestParams();

        params.setPage(page);
        params.setPagesize(pageSize);
        params.setUri("organized:shoufa");
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetNewSongListRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private RequestBody createGetNetizenListRqBean(Context context, int page, int pageSize) {
        PopularSongListRequestBean bean = new PopularSongListRequestBean();
        bean.setMethod("core.library.browse_new");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        PopularSongListRequestParams params = new PopularSongListRequestParams();

        params.setPage(page);
        params.setPagesize(pageSize);
        params.setUri("qingting:ondemand:categories:523:2928");
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetNetizenListRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

}
