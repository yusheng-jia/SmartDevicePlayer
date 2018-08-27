package com.mantic.control.api.account;

import com.baidu.iot.sdk.model.AudioTrack;
import com.mantic.control.api.account.bean.ModifyRsBean;
import com.mantic.control.api.account.bean.RegisterRsBean;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by jayson on 2017/6/20.
 */

public interface AccountServiceApi {


    /**
     * 验证码 -- ok
     * @param body
     * @return
     */
    @POST(AccountUrl.VERIFICATIONCODE)
    Call<RegisterRsBean> verificationCode(@HeaderMap Map<String, String> header, @Body RequestBody body);

    /**
     * 注册 --ok
     * @param body
     * @return
     */
    @POST(AccountUrl.REGISTER)
    Call<RegisterRsBean> register(@HeaderMap Map<String, String> header, @Body RequestBody body);

    /**
     * 找回密码 -- ok
     * @param body
     * @return
     */
    @POST(AccountUrl.RETRIEVE)
    Call<RegisterRsBean> retrieve(@HeaderMap Map<String, String> header, @Body RequestBody body);

    /**
     * 修改密码 -- ok
     * @param body
     * @return
     */
    @POST(AccountUrl.MODIFY)
    Call<ModifyRsBean> modify(@HeaderMap Map<String, String> header, @Body RequestBody body);

    /**
     * 登录 -- ok
     * @param body
     * @return
     */
    @POST(AccountUrl.LOGIN)
    Call<ResponseBody> login(@HeaderMap Map<String, String> header,@Body RequestBody body);

    /**
     * 登出 -- ok
     * @param body
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.LOGOUT)
    Call<ResponseBody> loginout(@Body RequestBody body);

    /**
     * 刷新token -- ok
     * @param header
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.REFRESHTOKEN)
    Call<ResponseBody> refreshToken(@HeaderMap Map<String, String> header);

    /**
     * 用户信息   ---ok
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.USERINFO)
    Call<ResponseBody> userinfo(@HeaderMap Map<String,String> header);

    /**
     * 更新用户信息 -- ok
     * @param body
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.USERUPDATE)
    Call<ResponseBody> userupdate(@HeaderMap Map<String,String> header,@Body RequestBody body);


    /**
     * 配网状态查询接口
     * @param header
     * @param body
     * @return
     */
    @POST(AccountUrl.STATUSQUERY)
    Call<ResponseBody> deviceStatusQuery(@HeaderMap Map<String, String> header,@Body RequestBody body);

    /**
     * 配网状态查询接口
     * @param header
     * @param body
     * @return
     */
    @POST(AccountUrl.NETWORKSTART)
    Call<ResponseBody> deviceStartNetwork(@HeaderMap Map<String, String> header,@Body RequestBody body);

    /**
     * 配网轮询接口
     * @param header
     * @param body
     * @return
     */
    @POST(AccountUrl.NETWORKCHECK)
    Call<ResponseBody> deviceNetworkCheck(@HeaderMap Map<String, String> header,@Body RequestBody body);

    /**
     * 绑定接口
     * @param header
     * @param body
     * @return
     */
    @POST(AccountUrl.DEVICEBIND)
    Call<ResponseBody> deviceBind(@HeaderMap Map<String, String> header,@Body RequestBody body);

    /**
     * 解绑接口
     * @param header
     * @param body
     * @return
     */
    @POST(AccountUrl.DEVICEUNBIND)
    Call<ResponseBody> deviceUnbind(@HeaderMap Map<String, String> header,@Body RequestBody body);

    /**
     * 检查用户token是否合法和过期时间
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.CHECKTOKEN)
    Call<ResponseBody> checkToken(@HeaderMap Map<String,String> header);


    /**
     * 根据UUID获取app配置功能：渠道，广告机，京东技能页
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.DEVICEGETINFO)
    Call<ResponseBody> deviceGetInfo(@HeaderMap Map<String,String> header,@Body RequestBody body);

    /**
     * 设置JD开关
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.SETJDSWITCH)
    Call<ResponseBody> setJdSwitch(@HeaderMap Map<String,String> header,@Body RequestBody body);


    /**
     * 根据JD code 拿token
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST(AccountUrl.JDAUTHORIZE)
    Call<ResponseBody> jdAuthorizeFromCode(@HeaderMap Map<String,String> header,@Body RequestBody body);
}
