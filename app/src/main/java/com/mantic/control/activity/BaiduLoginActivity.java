package com.mantic.control.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.model.DeviceInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.R;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.BIMWebview;

import java.util.List;

public class BaiduLoginActivity extends Activity {
    private static final String TAG = "BaiduLoginActivity";

    private Handler handler = new Handler();
    private ProgressDialog progressBar;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context = this;
        BIMWebview webView = (BIMWebview) findViewById(R.id.webview);

//        ActivityManager.getAppManager().finishAllActivity();

//        ActivityManager.getAppManager().addActivity(this);

        webView.login(new BIMWebview.LoginCallback() {

            @Override
            public void onFinish(boolean isSuccess) {
                if (isSuccess) {
                    Intent intent = new Intent(BaiduLoginActivity.this,
                            ManticBindActivity.class);
                    if (context!=null){
                        progressBar = ProgressDialog.show(context, null, getResources().getString(R.string.authorization));
                    }
//                    SharePreferenceUtil.setLoginTime(context,System.currentTimeMillis());
//                    refreshDeviceList();
                    startLoading();
//                    startActivity(intent);
//                    finish();
                }
            }
        }, getResources().getString(R.string.login_waitings));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshDeviceList() {
        DeviceManager.getInstance().deviceApi.getUserAllDevices(new IoTRequestListener<List<DeviceInfo>>() {

            @Override
            public void onSuccess(HttpStatus code, List<DeviceInfo> obj, PageInfo info) {
                Glog.i(TAG,"onSuccess()... + code: " + code + "obj: " + obj + "info: " + info);
                if (obj != null && !obj.isEmpty()) {
//                    saveDeviceInfo(obj);
//                    mConfigDeviceList.clear();
//                    mConfigDeviceList.add("可连接设备");
                    for (int i = 0; i<obj.size(); i++){
                        DeviceInfo deviceInfo = obj.get(i);
                        String deviceId = deviceInfo.getDeviceUuid();
                        SharePreferenceUtil.setDeviceId(context,deviceId);
                        //获取用户设备信息 直接进入Main 用户信息 进入更新 后续处理
                        startMain();
//                        Realm mRealm=Realm.getDefaultInstance();
//                        Device device = mRealm.where(Device.class).equalTo("uuid",deviceInfo.getDeviceUuid()).findFirst();
//                        if (device != null){
//                            String name = device.getDeviceName();
//                            if (name.isEmpty()){
//                                mConfigDeviceList.add(deviceInfo.getDeviceUuid());
//                            }else {
//                                mConfigDeviceList.add(name);
//                            }
//                        }else {
//                            mConfigDeviceList.add(deviceInfo.getDeviceUuid());
//                        }

                    }
//                    mDevicelistAdapter.notifyDataSetChanged();
                } else {
                    //账号下没有设备，做第一次处理
                    delDeviceInformation();
//                    emptyView.setVisibility(View.VISIBLE);
//                    deviceListView.setVisibility(View.GONE);
//                    setProgressBarDismiss();
                    startLoading();
                }

            }

            @Override
            public void onFailed(HttpStatus code) {
                Glog.i(TAG,"onFailed ->code: " + code);
//                setProgressBarDismiss();
                startLoading();

            }

            @Override
            public void onError(IoTException error) {
                Glog.i(TAG,"onFailed ->error: " + error);
//                setProgressBarDismiss();
                startLoading();
            }
        }, null);
    }

    private void delDeviceInformation(){
        SharePreferenceUtil.clearDeviceData(this);
        SharePreferenceUtil.clearSettingsData(this);
    }

    private void startLoading(){
        Intent intent = new Intent(BaiduLoginActivity.this, LoadingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        setProgressBarDismiss();
        finish();
    }

    private void startMain(){
        Intent intent = new Intent(BaiduLoginActivity.this, MainActivity.class);
        startActivity(intent);
        setProgressBarDismiss();
        finish();
    }

    private void setProgressBarDismiss() {
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
    }

    private void startBind(){
        Intent intent = new Intent(BaiduLoginActivity.this, ManticBindActivity.class);
        startActivity(intent);
        setProgressBarDismiss();
        finish();
    }
}
