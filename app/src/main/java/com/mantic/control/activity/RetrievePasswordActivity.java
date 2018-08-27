package com.mantic.control.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.bean.RegisterRsBean;
import com.mantic.control.manager.ActivityManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.LineEditText;
import com.mantic.control.widget.TitleBar;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 注册界面
 */
public class RetrievePasswordActivity extends Activity implements View.OnFocusChangeListener, View.OnClickListener, TitleBar.OnButtonClickListener{
    private static final String TAG = "RetrievePasswordActivity";
    private String COMFROM = "comfrom";

    private LineEditText edit_retrieve_username;
    private LineEditText edit_new_password;
    private LineEditText edit_retrieve_verify_code;
    private TextView tv_invalid_phone;
    private TextView tv_verification_code;
    private TextView tv_count_down;
    private LinearLayout ll_retrieve;
    private TitleBar tb_retrieve;
    private Handler handler = new Handler();
    private ProgressDialog mDialog;
    private CountDownTimer mCountDownTimer;
    private String user_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(RetrievePasswordActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
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
        if ("LoginSelectActivity".equals(getIntent().getStringExtra(COMFROM))) {
            tb_retrieve.setTitleText(getString(R.string.retrieve_password));
        } else {
            tb_retrieve.setTitleText(getString(R.string.change_password));
        }

    }

    private void initView() {
        tb_retrieve = (TitleBar) findViewById(R.id.tb_retrieve);
        edit_retrieve_username = (LineEditText) findViewById(R.id.edit_retrieve_username);
        edit_new_password = (LineEditText) findViewById(R.id.edit_new_password);
        edit_retrieve_verify_code = (LineEditText) findViewById(R.id.edit_retrieve_verify_code);
        tv_invalid_phone = (TextView) findViewById(R.id.tv_invalid_phone);
        tv_verification_code = (TextView) findViewById(R.id.tv_verification_code);
        tv_count_down = (TextView) findViewById(R.id.tv_count_down);
        ll_retrieve = (LinearLayout) findViewById(R.id.ll_retrieve);
        edit_retrieve_username.setOnFocusChangeListener(this);
        tv_verification_code.setOnClickListener(this);
        ll_retrieve.setOnClickListener(this);
        tb_retrieve.setOnButtonClickListener(this);

        edit_retrieve_username.setText(SharePreferenceUtil.getUserName(this));
        user_name=edit_retrieve_username.getText().toString().trim();

        edit_retrieve_username.setText(getDisplayStr(user_name));
        edit_retrieve_username.setTextColor(Color.parseColor("#5a000000"));
        edit_retrieve_username.setEnabled(false);
        edit_retrieve_username.setFocusable(false);
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

    private void showLoadingProgress() {
        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setMessage("正在找回密码...");
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
            if (!Util.isMobileNO(edit_retrieve_username.getText().toString().trim())) {
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
            case R.id.ll_retrieve:
//                String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
                final String regex = "^[0-9A-Za-z]{8,16}$";
                if (TextUtils.isEmpty(user_name) || TextUtils.isEmpty(edit_new_password.getText().toString())) {
                    tv_invalid_phone.setText("账户或者密码不能为空");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (edit_new_password.getText().toString().length() < 8) {
                    tv_invalid_phone.setText("密码必须为8-16位字母或者数字组成");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if(!edit_new_password.getText().toString().matches(regex)) {
                    tv_invalid_phone.setText("密码必须为8-16位字母或者数字组成");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                }  else {//
                    tv_invalid_phone.setVisibility(View.GONE);
                    showLoadingProgress();
                    String requestBodyStr = "{\"mobile\":\"" + user_name + "\", \"password\":\"" + edit_new_password.getText().toString() + "\", \"verify\":\"" + edit_retrieve_verify_code.getText().toString().trim() +"\"}";
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("Content-Type","application/json");
                    map.put("Lc",ManticApplication.channelId);

                    Glog.i(TAG, "onClick: " + requestBodyStr);
                    AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
                    Call<RegisterRsBean> registerCall = accountServiceApi.retrieve(map, requestBody);
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
                                        ToastUtils.showShortSafe("密码修改成功...");
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent mIntent = new Intent();
                                                mIntent.putExtra("username", body.getData().getMobile());
                                                // 设置结果，并进行传送
                                                setResult(LoginSelectActivity.RETRIEVE_CODE, mIntent);
                                                finish();
                                            }
                                        }, 800);
                                    } else {
                                        if ("verify code error".equals(body.getData().getError()) || "need verify code".equals(body.getData().getError())) {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("验证码错误");
                                        } else if ("mobile not register".equals(body.getData().getError())){
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("手机未注册");
                                        } else if ("Your new password cannot be the same as the old password.".equals(body.getData().getError())){
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("新密码不能与旧密码一致");
                                        } else {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("系统出错,请稍后再试");
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
                            tv_invalid_phone.setText("系统出错,请稍后再试");
                        }
                    });
                }
                break;

            case R.id.tv_verification_code:
                if (!Util.isMobileNO(user_name)) {
                    tv_invalid_phone.setText("非法手机号码");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                }

                showLoadingProgress();
                AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
                String requestBodyStr = "{\"mobile\":\""+ user_name  +"\"}";
                Map<String,String> map = new HashMap<String, String>();
                map.put("Content-Type","application/json");
                map.put("Lc", ManticApplication.channelId);
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
                Call<RegisterRsBean> verificationCodeCall = accountServiceApi.verificationCode(map, requestBody);
                verificationCodeCall.enqueue(new Callback<RegisterRsBean>() {
                    @Override
                    public void onResponse(Call<RegisterRsBean> call, Response<RegisterRsBean> response) {
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
    private String getDisplayStr(String realStr) {
       // String result = new String(realStr);
        char[] cs = realStr.toCharArray();
        for(int i = 0;i < cs.length;i++){
            if(i >= 3&& i <= 6){//把3和6区间的字符隐藏
                cs[i] = '*';
            }
        }
        return new String(cs);

    }
}
