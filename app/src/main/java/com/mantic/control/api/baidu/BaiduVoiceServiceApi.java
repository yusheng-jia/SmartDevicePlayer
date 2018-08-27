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

public interface BaiduVoiceServiceApi {
    /**
     * 获取正在语音点播播放歌曲的信息
     */
    @GET("history")
    Call<ResponseBody> getCurrentPlayingVoiceInfo(@HeaderMap Map<String, String> headers, @QueryMap Map<String, String> paramsMap);
}
