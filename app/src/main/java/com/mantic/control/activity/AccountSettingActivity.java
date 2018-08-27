package com.mantic.control.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.iot.sdk.IoTSDKManager;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.jd.smartcloudmobilesdk.authorize.AuthorizeCallback;
import com.jd.smartcloudmobilesdk.authorize.AuthorizeManager;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.data.jd.DateTime;
import com.mantic.control.data.jd.JDClass;
import com.mantic.control.data.jd.JdDefConfResponseData;
import com.mantic.control.data.jd.Md5Util;
import com.mantic.control.data.jd.NetUtil;
import com.mantic.control.data.jd.SpUtils;
import com.mantic.control.data.jd.TokenBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.OkHttpUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/30.
 * desc:
 */
public class AccountSettingActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener {
    private static final String TAG = "AccountSettingActivity";
    private AccountServiceApi accountServiceApi;
    private static final int AUTHOR_SUCCESS = 0;
    private static final int AUTHOR_FAIL = 1;
    private static final int UN_AUTHOR_FAIL = 2;
    private static final int UN_AUTHOR_SUCCESS = 3;
    private static final int REGISTER_SUCCESS = 4;
    private static final int REGISTER_FAIL = 5;

    private Context mContext;
    private String userName = "";
    private String accessToken;

    // 账号相关 用于绑定京东账号
    private String user_id;
    private String deviceId;
    private String cmAccessToken;

    @BindView(R.id.jd_account_view)
    RelativeLayout jdView;
    @BindView(R.id.modify_password)
    RelativeLayout pwdView;
    @BindView(R.id.jd_account_status)
    TextView authorText;
    @BindView(R.id.account_setting_titlebar)
    TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_account_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        ButterKnife.bind(this);
        accountServiceApi =  AccountRetrofit.getInstance().create(AccountServiceApi.class);
        user_id = SharePreferenceUtil.getUserId(this);
        deviceId = SharePreferenceUtil.getDeviceId(this);
        cmAccessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        titleBar.setOnButtonClickListener(this);
        if (SharePreferenceUtil.getJdSkillSwitch(this)){
            jdView.setVisibility(View.VISIBLE);
        }else {
            jdView.setVisibility(View.GONE);
        }
        initState();
    }

    private void initState(){
        if (isAuthorize()){
            authorText.setText("已授权");
            getJDAccessToken();
        }else {
            authorText.setText("未授权");
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case AUTHOR_SUCCESS:
                    authorText.setText("已授权");
                    break;
                case AUTHOR_FAIL:
                    authorText.setText("未授权");
                    break;
                case UN_AUTHOR_FAIL:
                    ToastUtils.showShortSafe("解除失败");
                    break;
                case UN_AUTHOR_SUCCESS:
                    authorText.setText("未授权");
                    ToastUtils.showShortSafe("解除成功");
                    break;

            }
        }
    };

    @OnClick(R.id.jd_account_view)
    public void jdClick(){
        Glog.i(TAG,"jdClick...");
        if (isAuthorize()){//解除授权
            CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
            mBuilder.setTitle("解绑");
            mBuilder.setMessage("解除京东账号后，将无法再操作使用语音购物与智能家居技能，您确定要解绑吗？");
            mBuilder.setPositiveButton("解绑", new CustomDialog.Builder.DialogPositiveClickListener() {
                @Override
                public void onPositiveClick(final CustomDialog dialog) {
                    dialog.dismiss();
                    unAuthorize();
                }
            });
            mBuilder.setNegativeButton(getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                @Override
                public void onNegativeClick(CustomDialog dialog) {
                    dialog.dismiss();
                }
            });
            mBuilder.create().show();
        }else {//授权
            AuthorizeManager.getInstance().authorize(JDClass.appKey, JDClass.redirectUri, JDClass.state, new AuthorizeCallback() {
                @Override
                public void onResponse(String code, String state) {
                    Glog.i(TAG, "authorize code = " + code + " state = " + state);
                    Map<String,String> map = new HashMap<>();
                    map.put("Content-Type","application/json");
                    map.put("Authorization","Bearer " + cmAccessToken);
                    map.put("Lc", ManticApplication.channelId);
                    String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ deviceId+"\",\"code\":\""+code+"\",\"test\":"+1+"}";
                    Glog.i(TAG,"request: " + request);
                    RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
                    retrofit2.Call<ResponseBody> call = accountServiceApi.jdAuthorizeFromCode(map,body);
                    call.enqueue(new retrofit2.Callback<ResponseBody>() {
                        @Override
                        public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                            if (response!=null && response.body() != null){
                                try {
                                    JSONObject mainObject = new JSONObject(response.body().string());
                                    accessToken = mainObject.getJSONObject("data").getString("access_token");
                                    if (TextUtils.isEmpty(accessToken)) {
                                        return;
                                    }

                                    Glog.i(TAG,"注册Token：" + accessToken);
                                    AuthorizeManager.getInstance().registerAccessToken(accessToken);

                                    String name = Md5Util.md5(JDClass.appKey + userName);
                                    SpUtils.saveToLocal(mContext, name, "access_token", accessToken);
                                    mHandler.sendEmptyMessage(AUTHOR_SUCCESS);

                                } catch (JSONException | IOException e) {
                                    e.printStackTrace();

                                }
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                        }
                    });

//                    String url = JDClass.urlToken.replace("CODE", code);
//                    NetUtil.get(url, new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            Glog.i(TAG, "get access_token onFailure response = " + e.getMessage());
//                            mHandler.sendEmptyMessage(AUTHOR_FAIL);
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) {
//
//                            try {
//                                String body = response.body().string();
//                                Glog.i(TAG, "get access_token onSuccess response = " + body);
//                                TokenBean tokenBean = new Gson().fromJson(body, TokenBean.class);
//                                accessToken = tokenBean.getAccess_token();
//                                if (TextUtils.isEmpty(accessToken)) {
//                                    mHandler.sendEmptyMessage(AUTHOR_FAIL);
//                                    return;
//                                }
//
//                                Glog.i(TAG,"注册Token：" + accessToken);
//                                AuthorizeManager.getInstance().registerAccessToken(accessToken);
//
//                                String name = Md5Util.md5(JDClass.appKey + userName);
//                                SpUtils.saveToLocal(mContext, name, "access_token", accessToken);
//                                mHandler.sendEmptyMessage(AUTHOR_SUCCESS);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            });
        }
    }


    private void registerJdDevice(){
        String url = JDClass.AddressUrl + "sign=" + getRegisterSign() +
                "&timestamp=" + NetUtil.getCurrentDateTime() +"&v=2.0&app_key="+JDClass.appKey +
                "&access_token=" + accessToken +"&method=jd.smart.open.alpha.device.register" + "&360buy_param_json={\"device_id\":\"" + deviceId + "\",\"client_ip\":\"172.34.56.78\"}";
        NetUtil.post(url, " ", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(REGISTER_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Glog.i(TAG, "onResponse body: "+body );
                JSONObject json;
                try {
                    json = new JSONObject(body);
                    JSONObject resultObject = json.getJSONObject("jd_smart_open_alpha_device_register_response");
                    String code = resultObject.getString("code");
                    Glog.i(TAG,"register -> code: " + code);
//                    JSONObject dataOjbect = resultObject.getJSONObject("result");
//                    confResponseData =   new Gson().fromJson(dataOjbect.getJSONObject("data").toString(), JdDefConfResponseData.class);
//                    Glog.i(TAG,"confResponseData: " + confResponseData.toString());
                    mHandler.sendEmptyMessage(REGISTER_SUCCESS);
                } catch (JSONException e) {
                    mHandler.sendEmptyMessage(REGISTER_FAIL);
                    e.printStackTrace();
                }
            }
        });
    }

    private String getRegisterSign(){
        String tempSign = JDClass.appSecret + JDClass.signContentdefConf(deviceId)
                +"access_token"+ accessToken + "app_keyHUR698I6I5GK5WRYP3SRBRIJ8CS6UWYKmethodjd.smart.open.alpha.device.registertimestamp"+
                NetUtil.getCurrentDateTime()+"v2.0" + JDClass.appSecret;
        Glog.i(TAG,"getConfSign -> 加密前子串：" + tempSign);
        return Md5Util.md5Up(tempSign);
    }


    @OnClick(R.id.modify_password)
    public void pwdClick(){
        Intent intent = new Intent(this, RetrievePasswordActivity.class);
        intent.putExtra("comfrom", "MainActivity");
        startActivity(intent);
    }

    private boolean isAuthorize() {
        String name = Md5Util.md5(JDClass.appKey + userName);
        accessToken = SpUtils.getFromLocal(mContext, name, "access_token", "");
        Glog.i(TAG, "isAuthorize accessToken: " + accessToken);
        return  !TextUtils.isEmpty(accessToken);
    }

    public void getJDAccessToken(){
        String name = Md5Util.md5(JDClass.appKey + userName);
        accessToken = SpUtils.getFromLocal(mContext, name, "access_token", "");
    }

    private void removeToken(){
        String name = Md5Util.md5(JDClass.appKey + userName);
        SpUtils.saveToLocal(mContext, name, "access_token", "");
        //关闭智能家居和语音购物
//        SharePreferenceUtil.setSmartHomeOpen(this,false);
//        SharePreferenceUtil.setVoiceShoppingOpen(this,false);
    }
    private void unAuthorize(){
        String signStr = JDClass.appSecret + "360buy_param_json{\"access_token\":\""+accessToken+"\"}"+
                "access_token"+accessToken+"app_key"+JDClass.appKey+"methodjingdong.smart.api.auth.release"+"timestamp"+DateTime.jdFormatDateTime()+
                "v2.0" + JDClass.appSecret;
        Glog.i(TAG,"signStr:" + signStr);
        String sign = Md5Util.md5Up(signStr);
        Glog.i(TAG,"sign:" + sign);
        String url = "https://smartopentest.jd.com/routerjson?"+
                "sign="+sign+
                "&access_token=" + accessToken+
                "&timestamp=" + DateTime.jdFormatDateTime()+
                "&v=2.0&app_key="+JDClass.appKey+
                "&method=jingdong.smart.api.auth.release"+
                "&360buy_param_json={\"access_token\":\""+accessToken+"\"}";
        Glog.i(TAG,"url:" + url);
        NetUtil.post(url, "", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Glog.i(TAG, "unAuthorize onFailure response = " + e.getMessage());
                mHandler.sendEmptyMessage(UN_AUTHOR_FAIL);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                Glog.i(TAG,"body: " + body);
                try {
                    JSONObject json = new JSONObject(body);
                    JSONObject codeJson = json.getJSONObject("jingdong_smart_api_auth_release_response");
                    int code = codeJson.getInt("code");
                    if (code == 0){
                        removeToken();
                        mHandler.sendEmptyMessage(UN_AUTHOR_SUCCESS);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }
}
