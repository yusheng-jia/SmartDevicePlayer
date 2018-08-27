package com.mantic.control.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.iot.sdk.iam.AccessToken;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.manager.ActivityManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTAppConfigMgr;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.LineEditText;
import com.mantic.control.widget.TitleBar;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 多渠道登录界面
 */
public class LoginSelectActivity extends Activity implements View.OnFocusChangeListener, View.OnClickListener, TitleBar.OnButtonClickListener {
    private static final String TAG = "LoginSelectActivity";
    public static final int REGITSTER_CODE = 201;
    public static final int RETRIEVE_CODE = 202;

    private LineEditText edit_login_username;
    private LineEditText edit_login_password;
    private TextView tv_invalid_phone;
    private TextView tv_login_select_register;
    private TextView tv_login_select_retrieve_password;
    private LinearLayout ll_baidu_login;
    private LinearLayout ll_weixin_login;
    private LinearLayout ll_qq_login;
    private LinearLayout ll_login;
    private TitleBar tb_login_select;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_select);
        initView();
        ActivityManager.getAppManager().addActivity(this);
        IntentFilter filter = new IntentFilter("close_login");
        registerReceiver(finishReceiver, filter);

    }

    private void initView() {
        tb_login_select = (TitleBar) findViewById(R.id.tb_login_select);
        tv_invalid_phone = (TextView) findViewById(R.id.tv_invalid_phone);
        tv_login_select_retrieve_password = (TextView) findViewById(R.id.tv_login_select_retrieve_password);
        tv_login_select_register = (TextView) findViewById(R.id.tv_login_select_register);
        ll_baidu_login = (LinearLayout) findViewById(R.id.ll_baidu_login);
        ll_weixin_login = (LinearLayout) findViewById(R.id.ll_weixin_login);
        ll_qq_login = (LinearLayout) findViewById(R.id.ll_qq_login);
        ll_login = (LinearLayout) findViewById(R.id.ll_login);
        edit_login_username = (LineEditText) findViewById(R.id.edit_login_username);
        edit_login_password = (LineEditText) findViewById(R.id.edit_login_password);
        tv_login_select_register.setOnClickListener(this);
        tv_login_select_retrieve_password.setOnClickListener(this);
        edit_login_username.setOnFocusChangeListener(this);
        ll_baidu_login.setOnClickListener(this);
        ll_weixin_login.setOnClickListener(this);
        ll_qq_login.setOnClickListener(this);
        ll_login.setOnClickListener(this);
        tb_login_select.setOnButtonClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getAppManager().finishActivity(this);
        unregisterReceiver(finishReceiver);
        dismissLoadingProgress();
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        if (!b) {
            if (!Util.isMobileNO(edit_login_username.getText().toString().trim())) {
                tv_invalid_phone.setVisibility(View.VISIBLE);
            } else {
                tv_invalid_phone.setVisibility(View.GONE);
            }
        } else {
            tv_invalid_phone.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLeftClick() {
        finish();
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login_select_retrieve_password:
                Intent retrieveIntent = new Intent(this, RetrievePasswordActivity.class);
                retrieveIntent.putExtra("comfrom", "LoginSelectActivity");
                startActivityForResult(retrieveIntent, RETRIEVE_CODE);
                break;
            case R.id.tv_login_select_register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivityForResult(registerIntent, REGITSTER_CODE);
                break;
            case R.id.ll_baidu_login:
                Intent intent = new Intent(this, BaiduLoginActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_login:
                if (TextUtils.isEmpty(edit_login_username.getText().toString()) || TextUtils.isEmpty(edit_login_password.getText().toString())) {
                    ToastUtils.showShortSafe("用户名或者密码不能为空...");
                    return;
                }
                showLoadingProgress();
                Map<String,String> map = new HashMap<String, String>();
                map.put("Content-Type","application/json");
                map.put("Lc", ManticApplication.channelId);
                final String requestBodyStr = "{\"username\" : \"" + edit_login_username.getText().toString() + "\", \"password\" : \"" + edit_login_password.getText().toString() + "\"}";
                AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
                RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
                Call<ResponseBody> loginCall = accountServiceApi.login(map,requestBody);
                loginCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        dismissLoadingProgress();
                        if (null != response.body()) {
                            try {
                                final JSONObject mainJsonObject = new JSONObject(response.body().string());
                                JSONObject dataObject;
                                if (!AccountUrl.BASE_URL.contains("v2")){
                                    dataObject = mainJsonObject;
                                }else {// server v2 接口
                                    dataObject = mainJsonObject.getJSONObject("data");
                                }
                                String error = dataObject.optString("error");
                                if (!TextUtils.isEmpty(error)) {
                                    tv_invalid_phone.setText("密码错误");
                                    tv_invalid_phone.setVisibility(View.VISIBLE);
                                } else {
                                    tv_invalid_phone.setVisibility(View.GONE);
                                    String access_token = dataObject.optString("access_token");
                                    int expires_in = dataObject.optInt("expires_in");
                                    String refresh_token = dataObject.optString("refresh_token");
                                    Glog.i(TAG, "onResponse: access_token = " + access_token + " expires_in = " + expires_in + " refresh_token = " + refresh_token);
                                    IoTAppConfigMgr.putString(LoginSelectActivity.this, AccessToken.KEY_ACCESS_TOKEN, access_token);
                                    IoTAppConfigMgr.putString(LoginSelectActivity.this, AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                                    IoTAppConfigMgr.putString(LoginSelectActivity.this, AccessToken.KEY_EXPIRES_IN, expires_in + "");
                                    SharePreferenceUtil.setUserName(LoginSelectActivity.this, MD5Util.unicodeDecode(edit_login_username.getText().toString()));
                                    Intent intent = new Intent(LoginSelectActivity.this, LoadingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (Exception e) {
                                ToastUtils.showShortSafe("系统了点问题,请稍后再试...");
                                Glog.i(TAG,"系统了点问题,请稍后再试... Exception :" + e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        dismissLoadingProgress();
                        ToastUtils.showShortSafe("系统出错,请稍后再试...");
                    }
                });
                break;
            case R.id.ll_weixin_login:
                attemptLogin();
                break;
            default:
                break;
        }
    }

    public void showLoadingProgress() {
        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setMessage(getString(R.string.wx_loading));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }


    public void dismissLoadingProgress() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    private void attemptLogin() {
        showLoadingProgress();
        if (!((ManticApplication)getApplicationContext()).api.isWXAppInstalled()) {
            //提醒用户没有按照微信
            Toast.makeText(LoginSelectActivity.this, "没有安装微信,请先安装微信!", Toast.LENGTH_SHORT).show();
            dismissLoadingProgress();
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "mantic";//package name
        ((ManticApplication)getApplicationContext()).api.sendReq(req);

    }

    @Override
    protected void onResume() {
        dismissLoadingProgress();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REGITSTER_CODE) {
            edit_login_username.setText(data.getStringExtra("username"));
        } else if (resultCode == RETRIEVE_CODE) {
            edit_login_username.setText(data.getStringExtra("username"));
        }
    }

    BroadcastReceiver finishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("close_login")){
                finish();
            }
        }
    };
}
