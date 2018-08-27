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
import com.mantic.control.data.jd.JDClass;
import com.mantic.control.data.jd.Md5Util;
import com.mantic.control.data.jd.NetUtil;
import com.mantic.control.data.jd.SpUtils;
import com.mantic.control.data.jd.TokenBean;
import com.mantic.control.utils.Glog;
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
 * time: 2018/5/31.
 * desc:
 */
public class JdShoppingActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener {
    private static final String TAG = JdShoppingActivity.class.getSimpleName();
    private static final int AUTHOR_SUCCESS = 120;
    private AccountServiceApi accountServiceApi;
    private Context mContext;
    private String userName = "";
    private String accessToken;

    // 账号相关 用于绑定京东账号
    private String user_id;
    private String deviceId;
    private String cmAccessToken;

    @BindView(R.id.open_button)
    TextView openButton;
    @BindView(R.id.shopping_button)
    TextView deviceButton;
    @BindView(R.id.jd_shopping_titlebar)
    TitleBar titleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jd_shopping);
        ButterKnife.bind(this);
        mContext = this;
        accountServiceApi =  AccountRetrofit.getInstance().create(AccountServiceApi.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(JdShoppingActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        user_id = SharePreferenceUtil.getUserId(this);
        deviceId = SharePreferenceUtil.getDeviceId(this);
        cmAccessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        titleBar.setOnButtonClickListener(this);
        updateStatus();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case AUTHOR_SUCCESS:
                    updateStatus();
                    break;
                default:
                    break;
            }
        }
    };

    @OnClick(R.id.open_button)
    public void openOrColse(){
        if (isAuthorize()){
            openDevice();
        }else {
            CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
            mBuilder.setTitle("启动语音");
            mBuilder.setMessage("启动后，可通过语音控制京东微联设备，是否要绑定京东账号以获取设备列表？");
            if (isAuthorize()){
                mBuilder.setPositiveButton(getString(R.string.ok), new CustomDialog.Builder.DialogPositiveClickListener() {
                    @Override
                    public void onPositiveClick(final CustomDialog dialog) {
                        dialog.dismiss();
                        openDevice();
                    }
                });
            }else {
                mBuilder.setPositiveButton(getString(R.string.goto_bind), new CustomDialog.Builder.DialogPositiveClickListener() {
                    @Override
                    public void onPositiveClick(final CustomDialog dialog) {
                        dialog.dismiss();
                        gotoAuthor();
                    }
                });
            }

            mBuilder.setNegativeButton(getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                @Override
                public void onNegativeClick(CustomDialog dialog) {
                    dialog.dismiss();
                }
            });
            mBuilder.create().show();
        }
    }

    @OnClick(R.id.shopping_button)
    public void deviceClick(){
        Intent intent = new Intent(this,JdShoppingInfoActivity.class);
        startActivity(intent);
    }

    private void openDevice(){
        boolean status = SharePreferenceUtil.getVoiceShoppingOpen(this);
        if (status){
            setJdSwitch(false);
        }else {
            setJdSwitch(true);
        }
        updateStatus();
    }

    private void gotoAuthor(){
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
            }
        });
    }

    private boolean isAuthorize() {
        String name = Md5Util.md5(JDClass.appKey + "");
        String accessToken = SpUtils.getFromLocal(mContext, name, "access_token", "");
        Glog.i(TAG, "isAuthorize accessToken: " + accessToken);
        return  !TextUtils.isEmpty(accessToken);
    }

    private void updateStatus(){
        if (isAuthorize()){
            if (SharePreferenceUtil.getVoiceShoppingOpen(mContext)){
                deviceButton.setEnabled(true);
                openButton.setText("关闭");
                openButton.setTextColor(getResources().getColor(R.color.mainTabIndicatorColor));
                openButton.setBackgroundResource(R.drawable.jd_button_purple);
                deviceButton.setBackgroundResource(R.drawable.jd_button_purple);
                deviceButton.setTextColor(getResources().getColor(R.color.mainTabIndicatorColor));
            }else {
                openButton.setText("开启");
                openButton.setTextColor(getResources().getColor(R.color.black));
                openButton.setBackgroundResource(R.drawable.jd_button_normal);
                deviceButton.setBackgroundResource(R.drawable.jd_button_disabled);
                deviceButton.setTextColor(getResources().getColor(R.color.text_color_grey));
                deviceButton.setEnabled(false);
            }
        }else {
            openButton.setText("开启");
            openButton.setTextColor(getResources().getColor(R.color.black));
            openButton.setBackgroundResource(R.drawable.jd_button_normal);
            deviceButton.setBackgroundResource(R.drawable.jd_button_disabled);
            deviceButton.setTextColor(getResources().getColor(R.color.text_color_grey));
            deviceButton.setEnabled(false);
        }

    }


    private void setJdSwitch(Boolean value){
        String user_id = SharePreferenceUtil.getUserId(this);
        String deviceId = SharePreferenceUtil.getDeviceId(this);
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Boolean smartValue = SharePreferenceUtil.getSmartHomeOpen(this);
        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ deviceId+"\",\"jd_skill_smart_home_switch\":"+smartValue+",\"jd_skill_shopping_switch\":"+value+"}";
        Glog.i(TAG,"request: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);
        Map<String,String> map = new HashMap<>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc", ManticApplication.channelId);

        retrofit2.Call<ResponseBody> call = accountServiceApi.setJdSwitch(map,body);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response!=null && response.body() != null){
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject = mainObject.getJSONObject("data");
                        if (mainObject.getString("retcode").equals("0")){//设置成功
                            Boolean shoppingSwitch = dataObject.getBoolean("jd_skill_shopping_switch");
                            Boolean smartSwitch = dataObject.getBoolean("jd_skill_smart_home_switch");
                            SharePreferenceUtil.setVoiceShoppingOpen(mContext,shoppingSwitch);
                            if (shoppingSwitch){
                                ToastUtils.showShortSafe("已开启");
                            }else {
                                ToastUtils.showShortSafe("已关闭");
                            }
                            updateStatus();
                            Glog.i(TAG,"shoppingSwitch: " + shoppingSwitch);
                            Glog.i(TAG,"smartSwitch: " + smartSwitch);
                        }
                    } catch (JSONException | IOException e) {
                        ToastUtils.showShortSafe("请检查网络后重试");
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                ToastUtils.showShortSafe("请检查网络后重试");
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
