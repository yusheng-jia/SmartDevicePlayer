package com.mantic.control.api.mopidy;

import com.mantic.control.api.mopidy.bean.MopidyRsAlbumBean;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumMoreBean;
import com.mantic.control.api.mopidy.bean.MopidyRsAlbumPageBean;
import com.mantic.control.api.mopidy.bean.MopidyRsBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRsLiveBrowseBean;
import com.mantic.control.api.mopidy.bean.MopidyRsTrackBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.api.sound.MopidyRsAnchorBean;
import com.mantic.control.api.sound.MopidyRsSoundBgMusicBean;
import com.mantic.control.api.sound.MopidyRsSoundModalBean;
import com.mantic.control.api.sound.MopidyRsSoundProductBean;
import com.mantic.control.api.sound.ProductDeleteRsBean;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Jia on 2017/5/22.
 */

public interface MopidyServiceApi {

    /**
     * 主播请求
     * @param mopidy
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsLiveBrowseBean> postMopidyLiveQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    /**
     * Directory 获取方法
     * @param mopidy
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsBrowseBean> postMopidyQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMopidyQuestImage(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsAlbumBean> postMopidyAlbumQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsAlbumMoreBean> postMopidyAlbumMoreQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsAlbumPageBean> postMopidyAlbumPageQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsTrackBean> postMopidyTrackPageQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMopidyTrackDetails(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMopidySetRecStatus(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMopidyGetRecStatus(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMopidySetDeviceName(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMopidyGetDeviceName(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsSoundModalBean> postMopidysoundModal(@HeaderMap Map<String,String> header, @Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsAnchorBean> postMopidysoundAnchor(@HeaderMap Map<String,String> header, @Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsSoundBgMusicBean> postMopidysoundBgMusic(@HeaderMap Map<String,String> header, @Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsSoundProductBean> postMopidysoundProduct(@HeaderMap Map<String,String> header, @Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MopidyRsSoundProductBean> postMopidyaddSound(@HeaderMap Map<String,String> header, @Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ProductDeleteRsBean> postMopidyDeleteSound(@HeaderMap Map<String,String> header, @Body RequestBody mopidy);
}
