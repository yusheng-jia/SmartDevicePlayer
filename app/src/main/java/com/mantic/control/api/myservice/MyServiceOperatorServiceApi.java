package com.mantic.control.api.myservice;

import com.mantic.control.api.myservice.bean.MusicServiceBean;
import com.mantic.control.api.myservice.bean.PopularSingerResultBean;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Jia on 2017/5/22.
 */

public interface MyServiceOperatorServiceApi {

    @GET("music_service_cfg")
    Call<MusicServiceBean> getMyServiceList();

    @GET("baidu_hot_singer_json")
    Call<PopularSingerResultBean> getPopularSingers();
}
