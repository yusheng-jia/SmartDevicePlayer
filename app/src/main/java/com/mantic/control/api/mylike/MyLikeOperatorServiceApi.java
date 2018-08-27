package com.mantic.control.api.mylike;

import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelListPositionChangeRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelListRsBean;
import com.mantic.control.api.mylike.bean.MyLikeAddRsBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.api.mylike.bean.MyLikeGetRsBean;

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

public interface MyLikeOperatorServiceApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyLikeAddRsBean> postMyLikeAddQuest(@HeaderMap Map<String,String> header,@Body RequestBody myChannelAdd);

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyLikeDeleteRsBean> postMyLikeDeleteQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<MyLikeGetRsBean> postMyLikeListQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);


    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postMyLikeUpdateQuest(@HeaderMap Map<String,String> header,@Body RequestBody mopidy);
}
