package com.mantic.control.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.Url;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.api.account.bean.RegisterRsBean;
import com.mantic.control.manager.ActivityManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.LineEditText;
import com.mantic.control.widget.TitleBar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册界面
 */
public class RegisterActivity extends Activity implements View.OnFocusChangeListener, View.OnClickListener, TitleBar.OnButtonClickListener{
    private static final String TAG = "RegisterActivity";

    private LineEditText edit_register_username;
    private LineEditText edit_register_password;
    private LineEditText edit_register_confirm_password;
    private LineEditText edit_register_verify_code;
    private TextView tv_invalid_phone;
    private TextView tv_verification_code;
    private TextView tv_count_down;
    private LinearLayout ll_register;
    private TitleBar tb_register;
    private Handler handler = new Handler();
    private ProgressDialog mDialog;
    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        ActivityManager.getAppManager().addActivity(this);
        mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long l) {
                tv_count_down.setText((int)(Math.ceil(l / 1000)) + "秒");
            }

            @Override
            public void onFinish() {
                tv_count_down.setVisibility(View.GONE);
                tv_verification_code.setVisibility(View.VISIBLE);
            }
        };
    }

    private void initView() {
        tb_register = (TitleBar) findViewById(R.id.tb_register);
        edit_register_username = (LineEditText) findViewById(R.id.edit_register_username);
        edit_register_password = (LineEditText) findViewById(R.id.edit_register_password);
        edit_register_confirm_password = (LineEditText) findViewById(R.id.edit_register_confirm_password);
        edit_register_verify_code = (LineEditText) findViewById(R.id.edit_register_verify_code);
        tv_invalid_phone = (TextView) findViewById(R.id.tv_invalid_phone);
        tv_verification_code = (TextView) findViewById(R.id.tv_verification_code);
        tv_count_down = (TextView) findViewById(R.id.tv_count_down);
        ll_register = (LinearLayout) findViewById(R.id.ll_register);
        edit_register_username.setOnFocusChangeListener(this);
        tv_verification_code.setOnClickListener(this);
        ll_register.setOnClickListener(this);
        tb_register.setOnButtonClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getAppManager().finishActivity(this);
        if (null != mCountDownTimer) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
        dismissLoadingProgress();
    }

    private void showLoadingProgress(String message) {
        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setMessage(message);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void dismissLoadingProgress() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
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
    public void onFocusChange(View view, boolean b) {
        if (!b) {
            if (!Util.isMobileNO(edit_register_username.getText().toString().trim())) {
                tv_invalid_phone.setVisibility(View.VISIBLE);
            } else {
                tv_invalid_phone.setVisibility(View.GONE);
            }
        } else {
            tv_invalid_phone.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_register:
//                String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
                final String regex = "^[0-9A-Za-z]{8,16}$";
                if (TextUtils.isEmpty(edit_register_username.getText().toString()) || TextUtils.isEmpty(edit_register_password.getText().toString()) || TextUtils.isEmpty(edit_register_confirm_password.getText().toString())) {
                    tv_invalid_phone.setText("账户或者密码不能为空");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (edit_register_password.getText().toString().length() < 8) {
                    tv_invalid_phone.setText("密码必须为8-16位字母或者数字组成");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if(!edit_register_password.getText().toString().matches(regex)) {
                    tv_invalid_phone.setText("密码必须为8-16位字母或者数字组成");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (!edit_register_password.getText().toString().equals(edit_register_confirm_password.getText().toString())) {
                    tv_invalid_phone.setText("两次输入的密码不一致");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else {//
                    tv_invalid_phone.setVisibility(View.GONE);
                    showLoadingProgress("正在注册中...");
                    String requestBodyStr = "{\"mobile\":\"" + edit_register_username.getText().toString() + "\", \"password\":\"" + edit_register_password.getText().toString() + "\", \"verify\":\"" + edit_register_verify_code.getText().toString().trim() +"\"}";
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("Content-Type","application/json");
                    map.put("Lc",ManticApplication.channelId);

                    Glog.i(TAG, "onClick: " + requestBodyStr);
                    AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
                    RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
                    Call<RegisterRsBean> registerCall = accountServiceApi.register(map, requestBody);
                    registerCall.enqueue(new Callback<RegisterRsBean>() {
                        @Override
                        public void onResponse(Call<RegisterRsBean> call, Response<RegisterRsBean> response) {
                            dismissLoadingProgress();

                            if (response.isSuccessful() && null == response.errorBody()) {
                                try {
                                    final RegisterRsBean body = response.body();
                                    Glog.i(TAG, "body.toString: " + body.toString());
                                    if ("0".equals(body.getRetcode())) {
                                        tv_invalid_phone.setVisibility(View.GONE);
                                        ToastUtils.showShortSafe("注册成功...");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent mIntent = new Intent();
                                                mIntent.putExtra("username", body.getData().getMobile());
                                                // 设置结果，并进行传送
                                                setResult(LoginSelectActivity.REGITSTER_CODE, mIntent);
                                                finish();
                                            }
                                        }, 800);
                                    } else {
                                        if ("verify code error".equals(body.getData().getError()) || "need verify code".equals(body.getData().getError())) {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("验证码错误");
                                        } else {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("手机号码已经注册");
                                        }
                                    }
                                } catch (Exception e) {
                                    tv_invalid_phone.setVisibility(View.VISIBLE);
                                    tv_invalid_phone.setText("系统出错,请稍后再试");
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<RegisterRsBean> call, Throwable t) {
                            Glog.i(TAG, "onFailure: " + t.getMessage());
                            dismissLoadingProgress();
                            tv_invalid_phone.setVisibility(View.VISIBLE);
                            tv_invalid_phone.setText("系统出错,请稍后再试" + t.getMessage());
                        }
                    });
                }
                break;

            case R.id.tv_verification_code:
                if (!Util.isMobileNO(edit_register_username.getText().toString().trim())) {
                    tv_invalid_phone.setText("非法手机号码");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                }

                showLoadingProgress("正在发送验证码...");
                AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
                String requestBodyStr = "{\"mobile\":\""+ edit_register_username.getText().toString()  +"\"}";
                Map<String,String> map = new HashMap<String, String>();
                map.put("Content-Type","application/json");
                map.put("Lc", ManticApplication.channelId);
                Glog.i(TAG,"verificationCode url: " + AccountUrl.VERIFICATIONCODE);
                Glog.i(TAG,"verificationCode map: " + map);
                RequestBody requestBody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
                Call<RegisterRsBean> verificationCodeCall = accountServiceApi.verificationCode(map, requestBody);
                verificationCodeCall.enqueue(new Callback<RegisterRsBean>() {
                    @Override
                    public void onResponse(Call<RegisterRsBean> call, Response<RegisterRsBean> response) {
                        Glog.i(TAG,"response: " + response.body());
                        dismissLoadingProgress();
                        if (response.isSuccessful() || null == response.errorBody()) {
                            try {
                                RegisterRsBean registerRsBean = response.body();
                                if ("0".equals(registerRsBean.getRetcode())) {
                                    tv_invalid_phone.setVisibility(View.GONE);
                                    tv_verification_code.setVisibility(View.GONE);
                                    tv_count_down.setVisibility(View.VISIBLE);
                                    mCountDownTimer.start();
                                } else {
                                    if ("isv.BUSINESS_LIMIT_CONTROL".equals(registerRsBean.getData().getError())) {
                                        tv_invalid_phone.setText("操作太频繁了,请稍后再试");
                                    } else if ("isv.OUT_OF_SERVICE".equals(registerRsBean.getData().getError())) {
                                        tv_invalid_phone.setText("系统出错,请稍后再试");
                                    } else {
                                        tv_invalid_phone.setText("非法手机号码");
                                    }

                                    tv_invalid_phone.setVisibility(View.VISIBLE);
                                    tv_verification_code.setVisibility(View.VISIBLE);
                                    tv_count_down.setVisibility(View.GONE);
                                }
                            } catch (Exception e) {
                                Glog.i(TAG, "onResponse: " + e.getMessage());
                                tv_invalid_phone.setText("系统出错,请稍后再试");
                                tv_invalid_phone.setVisibility(View.VISIBLE);
                                tv_verification_code.setVisibility(View.VISIBLE);
                                tv_count_down.setVisibility(View.GONE);
                            }
                        } else {
                            tv_invalid_phone.setVisibility(View.VISIBLE);
                            tv_invalid_phone.setText("系统出错,请稍后再试");
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterRsBean> call, Throwable t) {
                        Glog.i(TAG,"onFailure..............." + t.toString());
                        dismissLoadingProgress();
                        tv_invalid_phone.setVisibility(View.VISIBLE);
                        tv_invalid_phone.setText("系统出错,请稍后再试");
                    }
                });
                break;
            default:
                break;
        }
    }
}
