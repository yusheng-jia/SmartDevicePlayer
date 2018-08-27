package com.mantic.control.api.channelplay;

import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayInsertRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListMoveRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayModeGetRsBean;
import com.mantic.control.api.channelplay.bean.GetCurrentChannelPlayRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;

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

public interface ChannelPlayOperatorServiceApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayAddRsBean> postChannelPlayAddQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayAddRsBean> postChannelPlayUpdateQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postChannelPlayMoveUpdateQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayInsertRsBean> postChannelPlayInsertQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayListMoveRsBean> postChannelPlayChangeQuest(@HeaderMap Map<String,String> header,@Body RequestBody requestBody);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayListRsBean> postChannelPlayListQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayListDeleteRsBean> postChannelPlayDeleteListQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<SetCurrentChannelPlayRsBean> postChannelPlaySetCurrentQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<GetCurrentChannelPlayRsBean> postChannelPlayGetCurrentQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postChannelInfoListQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postChannelPlaySetModeQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ChannelPlayModeGetRsBean> postChannelPlayGetModeQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postChannelPlayClearQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postChannelPlayCodeQuest(@Body RequestBody requestBody);
}
