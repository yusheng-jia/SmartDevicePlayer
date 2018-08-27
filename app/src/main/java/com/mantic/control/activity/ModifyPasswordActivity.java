package com.mantic.control.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.iam.AccessToken;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.bean.ModifyRsBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTAppConfigMgr;
import com.mantic.control.widget.LineEditText;
import com.mantic.control.widget.TitleBar;

import java.util.HashMap;
import java.util.Map;

import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 修改密码界面
 */
public class ModifyPasswordActivity extends SwipeBackActivity implements View.OnClickListener, TitleBar.OnButtonClickListener {
    private static final String TAG = "ModifyPasswordActivity";

    private LineEditText edit_old_password;
    private LineEditText edit_new_password;
    private LineEditText edit_confirm_new_password;
    private TextView tv_invalid_phone;
    private LinearLayout ll_modify;
    private TitleBar tb_modify;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
        initView();
    }

    private void initView() {
        tb_modify = (TitleBar) findViewById(R.id.tb_modify);
        edit_old_password = (LineEditText) findViewById(R.id.edit_old_password);
        edit_new_password = (LineEditText) findViewById(R.id.edit_new_password);
        edit_confirm_new_password = (LineEditText) findViewById(R.id.edit_confirm_new_password);
        tv_invalid_phone = (TextView) findViewById(R.id.tv_invalid_phone);

        ll_modify = (LinearLayout) findViewById(R.id.ll_modify);
        ll_modify.setOnClickListener(this);
        tb_modify.setOnButtonClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingProgress();
    }

    private void showLoadingProgress() {
        mDialog = new ProgressDialog(this);
        mDialog.setIndeterminate(true);
        mDialog.setMessage("正在修改密码..");
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
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_modify:
//                String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
                final String regex = "^[0-9A-Za-z]{8,16}$";
                if (TextUtils.isEmpty(edit_old_password.getText().toString()) || TextUtils.isEmpty(edit_new_password.getText().toString()) || TextUtils.isEmpty(edit_confirm_new_password.getText().toString())) {
                    tv_invalid_phone.setText("旧密码或者新密码不能为空");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (edit_new_password.getText().toString().length() < 8) {
                    tv_invalid_phone.setText("密码必须为8-16位字母或者数字组成");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (!edit_new_password.getText().toString().matches(regex)) {
                    tv_invalid_phone.setText("密码必须为8-16位字母或者数字组成");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (edit_old_password.getText().toString().equals(edit_new_password.getText().toString())) {
                    tv_invalid_phone.setText("新密码不能与旧密码一致");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else if (!edit_confirm_new_password.getText().toString().equals(edit_new_password.getText().toString())) {
                    tv_invalid_phone.setText("2次输入的新密码不一致");
                    tv_invalid_phone.setVisibility(View.VISIBLE);
                    return;
                } else {//
                    tv_invalid_phone.setVisibility(View.GONE);
                    showLoadingProgress();
                    String requestBodyStr = "{\"old_password\":\"" + edit_old_password.getText().toString() + "\", \"new_password\":\"" + edit_new_password.getText().toString() + "\"}";
                    String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("Content-Type", "application/json");
                    map.put("Lc", ManticApplication.channelId);
                    map.put("Authorization", "Bearer " + accessToken);

                    Glog.i(TAG, "onClick: " + requestBodyStr);
                    AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBodyStr);
                    Call<ModifyRsBean> registerCall = accountServiceApi.modify(map, requestBody);
                    registerCall.enqueue(new Callback<ModifyRsBean>() {
                        @Override
                        public void onResponse(Call<ModifyRsBean> call, Response<ModifyRsBean> response) {
                            dismissLoadingProgress();

                            if (response.isSuccessful() && null == response.errorBody()) {
                                try {
                                    final ModifyRsBean body = response.body();
                                    Glog.i(TAG, "body.toString: " + body.toString());
                                    if ("0".equals(body.getRetcode())) {
                                        tv_invalid_phone.setVisibility(View.GONE);
                                        ToastUtils.showShortSafe("密码修改成功...");
                                        IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_ACCESS_TOKEN, body.getData().getAccess_token());
                                        IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_REFRESH_TOKEN, body.getData().getRefresh_token());
                                        IoTAppConfigMgr.putString(getApplicationContext(), AccessToken.KEY_EXPIRES_IN, body.getData().getExpires_in());
                                        finish();
                                    } else {
                                        if ("Your new password cannot be the same as the old password.".equals(body.getData().getError())) {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("新密码不能与旧密码一致");
                                        } else if ("Old password is incorrect".equals(body.getData().getError())) {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("旧密码错误");
                                        } else {
                                            tv_invalid_phone.setVisibility(View.VISIBLE);
                                            tv_invalid_phone.setText("密码修改失败");
                                        }
                                    }
                                } catch (Exception e) {
                                    tv_invalid_phone.setVisibility(View.VISIBLE);
                                    tv_invalid_phone.setText("系统出错,请稍后再试");
                                }

                            }
                        }

                        @Override
                        public void onFailure(Call<ModifyRsBean> call, Throwable t) {
                            Glog.i(TAG, "onFailure: " + t.getMessage());
                            dismissLoadingProgress();
                            tv_invalid_phone.setVisibility(View.VISIBLE);
                            tv_invalid_phone.setText("系统出错,请稍后再试");
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
