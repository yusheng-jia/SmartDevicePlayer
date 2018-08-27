package com.mantic.control.api.netizen;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Jia on 2017/5/22.
 */

public interface NetizenOperatorServiceApi {

    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("rpc")
    Call<ResponseBody> postUploadAlbumResultQuest(@Body RequestBody requestBody);
}
