package com.mantic.control.api.searchresult;

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

public interface SearchOperatorServiceApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<SearchRsBean> postSearchResultQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<AuthorSearchRsBean> postAuthorSearchResultQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<AlbumSearchRsBean> postAlbumSearchResultQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<SongSearchRsBean> postSongSearchResultQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<RadioSearchRsBean> postRadioSearchResultQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);
}
