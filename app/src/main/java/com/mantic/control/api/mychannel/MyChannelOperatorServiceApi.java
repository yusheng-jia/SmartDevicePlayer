package com.mantic.control.api.mychannel;

import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelClearRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDetailRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelListPositionChangeRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelListRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelSaveRsBean;

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

public interface MyChannelOperatorServiceApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelAddRsBean> postMyChannelAddQuest(@HeaderMap Map<String,String> header,@Body RequestBody myChannelAdd);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelSaveRsBean> postMyChannelSaveQuest(@HeaderMap Map<String,String> header,@Body RequestBody myChannelAdd);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMyChannelUpdateQuest(@HeaderMap Map<String,String> header,@Body RequestBody myChannelAdd);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelDetailRsBean> postMyChannelDetailQuest(@HeaderMap Map<String,String> header,@Body RequestBody myChannelAdd);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelDeleteRsBean> postMyChannelDeleteQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelClearRsBean> postMyChannelClearQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelListRsBean> postMyChannelListQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyChannelListPositionChangeRsBean> postMyChannelPositionChangeQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);



    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postAlbumDetailQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);
}
