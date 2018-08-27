package com.mantic.control.api.entertainment;

import com.mantic.control.api.entertainment.bean.BannerListResultBean;
import com.mantic.control.api.entertainment.bean.NewSongResultBean;
import com.mantic.control.api.entertainment.bean.PopularSongResultBean;
import com.mantic.control.api.searchresult.bean.AlbumSearchRsBean;
import com.mantic.control.api.searchresult.bean.AuthorSearchRsBean;
import com.mantic.control.api.searchresult.bean.RadioSearchRsBean;
import com.mantic.control.api.searchresult.bean.SearchRsBean;
import com.mantic.control.api.searchresult.bean.SongSearchRsBean;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Jia on 2017/5/22.
 */

public interface EntertainmentOperatorServiceApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<PopularSongResultBean> postPopularSongListResultQuest(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<NewSongResultBean> postNewSongListResultQuest(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<PopularSongResultBean> postNetizenListResultQuest(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<BannerListResultBean> postBannerListResultQuest(@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<RadioSearchRsBean> postRadioSearchResultQuest(@HeaderMap Map<String, String> header, @Body RequestBody requestBody);
}
