package com.mantic.control.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.iam.AccessToken;
import com.baidu.iot.sdk.model.DeviceInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.google.gson.Gson;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.Url;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTAppConfigMgr;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jia on 2017/6/15.
 */

public class LoadingActivity extends Activity {
    private static final String TAG = "LoadingActivity";
    private Context mContext;
    private static final boolean isText = false;
    private IoTSDKManager mIoTManager;
    private AccountServiceApi accountServiceApi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        mContext = this;
        mIoTManager = IoTSDKManager.getInstance();
        Glog.i(TAG, "isBaiduLogin: " + isBaiduLogin());
        Glog.i(TAG, "isNetworkAvalible: " + isNetworkAvalible());
        accountServiceApi =  AccountRetrofit.getInstance().create(AccountServiceApi.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAccessToken();
    }


    /**
     * 启动登录界面
     */
    private void startLogin() {
        Intent intent = new Intent(LoadingActivity.this, StartActivity.class);
        startActivity(intent);
    }

    /**
     * 启动主界面
     */
    private void startMain() {
        String user_id = SharePreferenceUtil.getUserId(this);
        String deviceId = SharePreferenceUtil.getDeviceId(this);
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ deviceId+"\"}";
        Glog.i(TAG,"request: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        Map<String,String> map = new HashMap<>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc",ManticApplication.channelId);

        Call<ResponseBody> call = accountServiceApi.deviceGetInfo(map,body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response!=null && response.body() != null){
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.getJSONObject("data");
                        Glog.i(TAG,"dataObject: " + dataObject.toString());
                        Boolean is_advert = dataObject.getBoolean("is_advert");
                        Boolean is_jd_skill = dataObject.getBoolean("is_jd_skill");
                        String device_lc = dataObject.getString("device_lc");
                        String user_id = dataObject.getString("user_id");
                        Boolean jd_skill_smart_home_switch = dataObject.getBoolean("jd_skill_smart_home_switch");
                        String device_uuid = dataObject.getString("device_uuid");
                        Boolean jd_skill_shopping_switch = dataObject.getBoolean("jd_skill_shopping_switch");
//                        Glog.i(TAG,"is_advert: " + is_advert);
//                        Glog.i(TAG,"device_lc: " + device_lc);
//                        Glog.i(TAG,"user_id: " + user_id);
//                        Glog.i(TAG,"jd_skill_smart_home_switch: " + jd_skill_smart_home_switch);
//                        Glog.i(TAG,"device_uuid: " + device_uuid);
//                        Glog.i(TAG,"jd_skill_shopping_switch: " + jd_skill_shopping_switch);
                        SharePreferenceUtil.setAdvertSwitch(mContext,is_advert);
                        SharePreferenceUtil.setSmartHomeOpen(mContext,jd_skill_smart_home_switch);
                        SharePreferenceUtil.setVoiceShoppingOpen(mContext,jd_skill_shopping_switch);
                        SharePreferenceUtil.setJdSkillSwitch(mContext,is_jd_skill);
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                        Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                SharePreferenceUtil.clearUserData(getApplicationContext());
                IoTSDKManager.getInstance().logout();
                Intent intent = new Intent(LoadingActivity.this, LoadingActivity.class);
                startActivity(intent);
//                finish();
            }
        });

    }

    /**
     * 启动配网界面
     */
    private void startSetNet() {
        Intent intent = new Intent(LoadingActivity.this, CheckNetworkActivity.class);
        startActivity(intent);
    }

    /**
     * 刷新token
     * {
     * "access_token": "69da9ce5d63c4049ba54547ef08c90de",
     * "expires_in": 43199,
     * "refresh_token": "b417075981244898b0134cc5a659e82a-r"
     * }
     */
    private void checkAccessToken() {
        String user_id = SharePreferenceUtil.getUserId(getApplicationContext());

        if (user_id == "") { // 第一次启动
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (isNetworkAvalible()) {
                        start();
                    } else { //无网络 设置结束后返回Loading
                        startSetNet();
                    }
                }
            }, 1000);
        } else {
            // server v1 接口暂时打开
            if (!AccountUrl.BASE_URL.contains("v2")){
                refreshToken();
            }else {// server v2 接口
                Call<ResponseBody> call = accountServiceApi.checkToken(MopidyTools.getHeaders());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Glog.i(TAG, "post -> checkToken sucess: ");
                        try {
                            JSONObject mainObject = new JSONObject(response.body().string());
                            JSONObject dataObject = mainObject.getJSONObject("data");
                            Glog.i(TAG,"checkToken -> dataObject:" + dataObject.toString());
                            Glog.i(TAG,"checkToken -> retcode:" + mainObject.getString("retcode"));
                            if (Objects.equals(mainObject.getString("retcode"), "1")){ // 2小时以内就更新Token 和 app2小时同步对应
                                refreshToken();
                            }else if(Objects.equals(mainObject.getString("retcode"), "0")){
                                if (dataObject.getInt("count_down")<=7560){
                                    refreshToken();
                                }else {
                                    start();
                                }
                            }else {
                                start();
                            }
                        } catch (JSONException | IOException e) {
                            start();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Glog.i(TAG,"checkToken -> onFailure" + t.getMessage());
                        startSetNet();
                    }
                });
            }
        }
    }

    private void refreshToken(){
        Glog.i(TAG,"refreshToken.........");
        String refreshToken = IoTSDKManager.getInstance().getAccessToken().getRefreshToken(); //使用refreshToken更新AccessToken
        String header = "Bearer " + refreshToken;
        Map<String,String> map = new HashMap<String, String>();
        map.put("Authorization",header);
        map.put("Lc", ManticApplication.channelId);
        Call<ResponseBody> call = accountServiceApi.refreshToken(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Glog.i(TAG, "post -> refreshToken sucess: ");
                    JSONObject mainObject = null; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            String access_token = mainObject.getString("access_token");
                            String expires_in = mainObject.getString("expires_in");
                            String refresh_token = mainObject.getString("refresh_token");
                            Glog.i(TAG, "post -> refreshToken: " + "access_token:" + access_token + "  expires_in:" + expires_in + "  refresh_token" + refresh_token);
                            if (access_token != null && expires_in != null && refresh_token != null) {
                                IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_ACCESS_TOKEN, access_token);
                                IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                                IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_EXPIRES_IN, expires_in);
                            }
                        }else {// server v2 接口
                            JSONObject dataObject = mainObject.getJSONObject("data");
                            if (Objects.equals(mainObject.getString("retcode"), "1")){ // refreshtoken失败 需要登录
                                startLogin();
                                finish();
                            }else {
                                String access_token = dataObject.getString("access_token");
                                String expires_in = dataObject.getString("expires_in");
                                String refresh_token = dataObject.getString("refresh_token");
                                Glog.i(TAG, "post -> refreshToken: " + "access_token:" + access_token + "  expires_in:" + expires_in + "  refresh_token" + refresh_token);
                                if (access_token != null && expires_in != null && refresh_token != null) {
                                    IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_ACCESS_TOKEN, access_token);
                                    IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                                    IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_EXPIRES_IN, expires_in);
                                }
                            }

                        }
                        start();

                    } catch (JSONException | IOException e) {
                        start();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG, "post -> refreshToken fail: " + t.getMessage());
                startSetNet();
            }
        });
    }
    /**
     * 判断百度是否登录，后续可能需要做成授权方式Auth
     */
    private boolean isBaiduLogin() {
        return IoTSDKManager.getInstance().isLogin();
    }

    private boolean isNetworkAvalible() {
        return NetworkUtils.isConnected(this);
    }

    private void start() {
        Glog.i(TAG,"start......" + Url.ACCOUNT_URL);
        if (mIoTManager.isLogin()) { //百度登录
            if (SharePreferenceUtil.isFirstSetup(this)) { //第一次登录
                DeviceManager.getInstance().deviceApi.getUserAllDevices(new IoTRequestListener<List<DeviceInfo>>() {
                    @Override
                    public void onSuccess(HttpStatus code, List<DeviceInfo> obj, PageInfo info) {
                        Glog.i(TAG, "onSuccess()... + code: " + code + "obj: " + obj + "info: " + info);
                        if (obj != null && !obj.isEmpty()) {
                            for (int i = 0; i < obj.size(); i++) {


                                DeviceInfo deviceInfo = obj.get(i);
                                String deviceId = deviceInfo.getDeviceUuid();
                                String bindToken = deviceInfo.getToken();
                                SharePreferenceUtil.setDeviceId(getApplicationContext(), deviceId);
                                SharePreferenceUtil.setDeviceToken(getApplicationContext(), bindToken);

                                //获取用户设备信息 直接进入Main 用户信息 进入更新 后续处理
                                //在主界面获取到设备 不做任何处理
                                Glog.i(TAG, "account has devices goto main......" + deviceId);
                                updateUserInfo();
                            }
                        } else {
                            //账号下没有设备，做第一次处理
                            SharePreferenceUtil.setDeviceId(getApplicationContext(), "");
                            SharePreferenceUtil.setDeviceToken(getApplicationContext(), "");
                            startSecond();
                        }

                    }

                    @Override
                    public void onFailed(HttpStatus code) {
                        Glog.i(TAG, "onFailed ->code: " + code);
                        startSecond();
                    }

                    @Override
                    public void onError(IoTException error) {
                        Glog.i(TAG, "onFailed ->error: " + error);
                        startSecond();

                    }
                }, null);


            } else {
                Glog.i(TAG, "not first in ........... start main().....");
                startMain();
            }
        } else {
            startLogin();
            finish();
        }
    }

    /** 第一次登录，但是查询到该账号下有设备*/
    private void  updateUserInfo(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Glog.i(TAG,"accessToken: " + accessToken);
        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        String header = "Bearer " + accessToken;
        Map<String,String> map = new HashMap<String, String>();
        map.put("Authorization",header);
        map.put("Lc", ManticApplication.channelId);
        Call<ResponseBody> call = accountServiceApi.userinfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Glog.i(TAG,"updateUserInfo success: " + response.body().toString());
                if (response.isSuccessful() && null == response.errorBody()){

                    JSONObject mainObject = null; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            String id = mainObject.getString("id");
                            SharePreferenceUtil.setUserId(getApplicationContext(),id);
                            Glog.i(TAG,"updateUserInfo: " + new Gson().toJson(mainObject));
                        }else {// server v2 接口
                            JSONObject dataObject = mainObject.getJSONObject("data");
                            String id = dataObject.getString("id");
                            SharePreferenceUtil.setUserId(getApplicationContext(),id);
                            Glog.i(TAG,"updateUserInfo: " + new Gson().toJson(dataObject));
                        }
//                        String username = mainObject.getString("username");
//                        String name = mainObject.getString("name");
//                        String email = mainObject.getString("email");
                        startMain();
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG,"updateUserInfo fail: ");
            }
        });
    }

    private void startSecond() {
        Glog.i(TAG, "startSecond......");
        if (!isText){
            if (SharePreferenceUtil.IsBind(this)) { // 绑定验证
                if (SharePreferenceUtil.IsNetworkSet(this)) { //验证是否设置网络
                    startInterest();
                    finish();
                } else {
                    startSetNetwork();
                    finish();
                }
            } else {
                startBind();
                finish();
            }
        }else {
            startMain();
        }

    }

    private void startSetNetwork() {
        Intent intent = new Intent(this, NetworkConfigActivity.class);
        startActivity(intent);
    }

    private void startBind() {
        Intent intent = new Intent(this, ManticBindActivity.class);
        startActivity(intent);
    }

//    private void startNameDev(){
//        Intent intent = new Intent(this, EditManticNameActivity.class);
//        startActivity(intent);
//    }

    private void startInterest() {
        if (((ManticApplication) getApplicationContext()).isAreadyAddInterestData()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SelectInterestActivity.class);
            startActivity(intent);
        }
    }

}
