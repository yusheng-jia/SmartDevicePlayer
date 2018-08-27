package com.mantic.control.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.DeviceInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.manager.ActivityManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import java.util.List;


public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "StartActivity";
    private static final int ANIMATION_DURATION = 2000;
    private static final float SCALE_END = 1.13F;


    private ImageView iv_start_background;
    private ProgressDialog mDialog;
    private IoTSDKManager mIoTManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        iv_start_background = (ImageView) findViewById(R.id.iv_start_background);
        findViewById(R.id.llLogin).setOnClickListener(this);
        findViewById(R.id.tvLegel).setOnClickListener(this);
        findViewById(R.id.agreement).setOnClickListener(this);
        findViewById(R.id.privacy).setOnClickListener(this);
        final ScaleAnimation animation =new ScaleAnimation(1.0f, 1.08f, 1.0f, 1.08f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(4500);//设置动画持续时间
        animation.setFillAfter(true);
        iv_start_background.startAnimation(animation);
        ActivityManager.getAppManager().addActivity(this);
        /*
        *有三种情况
        * 1.没有被连接，处于待入网配置的
        * 2.只有一个已经被连接的，则直接进入频道界面
        * 3.有多个设备，其中还分未被配置的和配置过的两种
        *
        *
        * */
//        mIoTManager = IoTSDKManager.getInstance();

//        ActivityManager.getAppManager().addActivity(this);

//        if (!TextUtils.isEmpty(AccountSettings.getInstance(this).getAutoKey())) {//登录过的
//
//            if(mIoTManager.isLogin()) { //百度登录
//                if (SharePreferenceUtil.isFirstSetup(this)) {//暂且使用这个，应该判断ToolUtil.getCurrentDeviceName(this)的值
//                    Intent intent1 = new Intent(StartActivity.this, ManticBindActivity.class);
//                    startActivity(intent1);
//                    finish();
//                } else {
//                    Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                }
//            }else{
//                if(DeviceManager.idRda){
//                    Intent intent = new Intent(StartActivity.this, BaiduLoginActivity.class);
//                    startActivity(intent);
//                    finish();
//                }else {
//                    Intent intent1 = new Intent(StartActivity.this, ManticBindActivity.class);
//                    startActivity(intent1);
//                    finish();
//                }
//            }
//        }


//        start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getAppManager().finishActivity(this);
    }

    private void  gotoLayoutLogin(){
        setContentView(R.layout.activity_start);
        findViewById(R.id.llLogin).setOnClickListener(this);
        findViewById(R.id.tvLegel).setOnClickListener(this);
    }

    private void startSecond() {
        Glog.i(TAG,"startSecond......");
        if (SharePreferenceUtil.IsBind(this)){ // 绑定验证
            if (SharePreferenceUtil.IsNetworkSet(this)){ //验证是否设置网络
                startInterest();
                finish();
            }else {
                startSetNetwork();
                finish();
            }
        }else {
            startBind();
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.llLogin:
//                Intent intent = new Intent(StartActivity.this, DispalyDeviceActivity.class);
//                startActivity(intent);
//                finish();
//                attemptLogin();
//                startBaiduLogin();
//                finish();

                Intent intent = new Intent(this, LoginSelectActivity.class);
                startActivity(intent);
//                finish();
                break;
            case R.id.tvLegel:
//                Intent intent1 = new Intent(StartActivity.this, MainActivity.class);
//                startActivity(intent1);
//                finish();
                break;
            case R.id.agreement:
                Intent agreement = new Intent(StartActivity.this, UserAgreementActivity.class);
                startActivity(agreement);
                break;
            case R.id.privacy:
                Intent privacy = new Intent(StartActivity.this, PrivacyPolicyActivity.class);
                startActivity(privacy);
                break;
        }
    }

    private void start(){
        if(mIoTManager.isLogin()){ //百度登录
            if (SharePreferenceUtil.isFirstSetup(this)){ //第一次登录
                DeviceManager.getInstance().deviceApi.getUserAllDevices(new IoTRequestListener<List<DeviceInfo>>() {
                    @Override
                    public void onSuccess(HttpStatus code, List<DeviceInfo> obj, PageInfo info) {
                        Glog.i(TAG, "onSuccess()... + code: " + code + "obj: " + obj + "info: " + info);
                        if (obj != null && !obj.isEmpty()) {
                            for (int i = 0; i < obj.size(); i++) {
//                        DeviceInfo deviceInfo = obj.get(i);
//                        String deviceId = deviceInfo.getDeviceUuid();
//                        SharePreferenceUtil.setDeviceId(context,deviceId);
                                //获取用户设备信息 直接进入Main 用户信息 进入更新 后续处理
                                //在主界面获取到设备 不做任何处理
                                Glog.i(TAG,"account has devices goto main......");
                                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            //账号下没有设备，做第一次处理
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


            }else {
                Glog.i(TAG,"not first in ........... start main().....");
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }else {
            gotoLayoutLogin();
        }
    }

    private void attemptLogin() {
        showLoadingProgress();
        if (!((ManticApplication)getApplicationContext()).api.isWXAppInstalled()) {
            //提醒用户没有按照微信
            Toast.makeText(StartActivity.this, "没有安装微信,请先安装微信!", Toast.LENGTH_SHORT).show();
            dismissLoadingProgress();
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "mantic";//package name
        ((ManticApplication)getApplicationContext()).api.sendReq(req);

    }

    private void startBaiduLogin(){
        Intent intent = new Intent(StartActivity.this, BaiduLoginActivity.class);
        startActivity(intent);
    }

    private void startSetNetwork(){
        Intent intent = new Intent(StartActivity.this, NetworkConfigActivity.class);
        startActivity(intent);
    }

    private void startBind(){
        Intent intent = new Intent(StartActivity.this, ManticBindActivity.class);
        startActivity(intent);
    }

    private void startNameDev(){
        Intent intent = new Intent(StartActivity.this, EditManticNameActivity.class);
        startActivity(intent);
    }

    private void startMain(){
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void startInterest(){
        if (((ManticApplication) getApplicationContext()).isAreadyAddInterestData()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, SelectInterestActivity.class);
            startActivity(intent);
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

    @Override
    protected void onResume() {
        super.onResume();
        Glog.v("wujx", "start onResume" );
        dismissLoadingProgress();
    }

}
