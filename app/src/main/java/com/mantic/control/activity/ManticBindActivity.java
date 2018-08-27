package com.mantic.control.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.baidu.iot.sdk.IoTSDKManager;
import com.google.gson.Gson;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.qrcode.QrCodeActivity;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTStringUtils;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;


/**
 * Created by Jia on 2017/6/15.
 */

public class ManticBindActivity extends Activity {
    private static final String TAG = "ManticBindActivity";
    public static final int REQUEST_QR_SCAN_CODE = 2001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        findViewById(R.id.mancit_bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQrCode();
            }
        });

        findViewById(R.id.tvLege2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoginOut();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_bind_activity");
        registerReceiver(closeReceiver,intentFilter);

        Glog.i("jys","time： "  + System.currentTimeMillis());
        sendBroadcast(new Intent("close_login"));
    }

    @Override
    protected void onResume() {
        updateUserInfo();
        super.onResume();
    }

    private BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("close_bind_activity")){
                finish();
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_QR_SCAN_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    Toast.makeText(this, getString(R.string.device_qr_err), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bundle.getInt(QrCodeActivity.RESULT_TYPE) == QrCodeActivity.RESULT_SUCCESS) {
                    String result = bundle.getString(QrCodeActivity.RESULT_STRING);
                    Glog.i(TAG, "QR scanner result:" + result);
                    Map<String, String> deviceInfo = IoTStringUtils.parseQRCode(result);
                    String token = deviceInfo.get("token");
                    String deviceUuid = deviceInfo.get("deviceUuid");
//                    String virtualId = deviceInfo.get("virtualId");

//                        //启动虚拟设备
//                        if (virtualId != null) {
//                            Intent virtualIntent = new Intent(this, VirtualDeviceControlActivity.class);
//                            virtualIntent.putExtra("virtualId", virtualId);
//                            startActivity(virtualIntent);
//                            return;
//                        }

                    if (token != null && deviceUuid != null) {
                        SharePreferenceUtil.setDeviceId(this,deviceUuid);
//                        DeviceManager.getInstance().bindDevice(deviceUuid,token,new BaiduBindRequest(ManticBindActivity.this));
                    } else {
                        Toast.makeText(this, getString(R.string.device_qr_err), Toast.LENGTH_SHORT).show();
                    }
                } else if (bundle.getInt(QrCodeActivity.RESULT_TYPE) == QrCodeActivity.RESULT_FAILED) {
                    Toast.makeText(this, getString(R.string.device_qr_err), Toast.LENGTH_SHORT).show();
                }else if (bundle.getInt(QrCodeActivity.RESULT_TYPE) == QrCodeActivity.RESULT_FINISH){ //结束当前Activity
                    finish();
                }
            }
        }
    }


    private void startQrCode(){
        Intent intent = new Intent(ManticBindActivity.this, QrCodeActivity.class);
        startActivityForResult(intent,REQUEST_QR_SCAN_CODE);
    }

    private void startMain(){
        Intent intent = new Intent(ManticBindActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoginOut(){
        CustomDialog.Builder mBuilder = mBuilder = new CustomDialog.Builder(this);
        mBuilder.setTitle(this.getString(R.string.dialog_btn_prompt));
        mBuilder.setMessage(this.getString(R.string.change_account));
        mBuilder.setPositiveButton(getString(R.string.ok), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
                SharePreferenceUtil.clearUserData(getApplicationContext());
                IoTSDKManager.getInstance().logout();
                dialog.dismiss();
                restartLoading();
            }
        });
        mBuilder.setNegativeButton(getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                dialog.dismiss();
            }
        });
        mBuilder.create().show();
    }

    private void restartLoading(){
        startActivity(new Intent(this,LoadingActivity.class));
        this.finish();

    }


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

                    JSONObject mainObject; //json 主体
                    try {
                        mainObject = new JSONObject(response.body().string());
                        JSONObject dataObject;
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            dataObject = mainObject;
                        }else {// server v2 接口
                            dataObject = mainObject.getJSONObject("data");
                        }
                        String id = dataObject.getString("id");
//                        String username = mainObject.getString("username");
//                        String name = mainObject.getString("name");
//                        String email = mainObject.getString("email");

                        SharePreferenceUtil.setUserId(getApplicationContext(),id);
                        Glog.i(TAG,"updateUserInfo: " + new Gson().toJson(dataObject));

                        getRecStatus(getApplicationContext());

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

    /**
     * 获取是否添加过兴趣列表
     */
    private void getRecStatus(final Context context) {
        RequestBody body = MopidyTools.createGetRecStatus(context);
        MopidyServiceApi mopidyServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        Call<ResponseBody> recStatusCall = mopidyServiceApi.postMopidyGetRecStatus(MopidyTools.getHeaders(),body);
        recStatusCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                        boolean result = mainObject.optBoolean("result");// result json 主体
                        if (result) {
                            ((ManticApplication)context.getApplicationContext()).setAreadyAddInterestData(true);
                        } else {
                            ((ManticApplication)context.getApplicationContext()).setAreadyAddInterestData(false);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(closeReceiver);
        super.onDestroy();
    }

}
