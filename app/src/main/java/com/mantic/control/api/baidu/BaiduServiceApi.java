package com.mantic.control.api.baidu;

import com.mantic.control.api.baidu.bean.BaiduTrackList;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


/**
 * Created by Jia on 2017/5/18.
 */

public interface BaiduServiceApi {
    /**
     * 根据tag获取列表
     * @return
     */
    @GET("tag")
    Call<BaiduTrackList> getTrackListByTagName(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> paramsMap);

    /**
     * 根据id获取歌曲的详情   http://s.xiaodu.baidu.com/v20161223/resource/musicdetail?song_id=1304253395
     * @return
     */
    @GET
    Call<ResponseBody> getTrackInfoBySongId(@Url String url);

    /**
     * 根据id获取有声类资源的详情   http://xiaodu.baidu.com/unicast/api/track?trackid==1304253395
     * @return
     */
    @GET
    Call<ResponseBody> getTrackInfoByAudioId(@Url String url);


    /**
     * 根据top获取列表
     * @return
     */
    @GET("top")
    Call<BaiduTrackList> getTrackListByTopName(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> paramsMap);

    /**
     * 根据歌手获取列表
     * @return
     */
    @GET("singer")
    Call<BaiduTrackList> getTrackListBySingerName(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> paramsMap);
}
