package com.mantic.control.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.TitleBar;
import com.tencent.bugly.beta.Beta;

import me.yokeyword.swipebackfragment.SwipeBackActivity;


/**
 * Created by wujiangxia on 2017/5/3.
 */
public class SettingActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener, View.OnClickListener{

    private TextView tvTitle;
    ProgressDialog mDialog;
    private CustomDialog mCustomDialog;
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(SettingActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        mContext = this;
//        showTitle(R.string.setting);
        ((TitleBar)findViewById(R.id.settings_title_bar)).setOnButtonClickListener(this);
        LinearLayout ll_permission = (LinearLayout) findViewById(R.id.ll_permission);
        LinearLayout ll_soft = (LinearLayout) findViewById(R.id.ll_soft);
        LinearLayout ll_firmware = (LinearLayout) findViewById(R.id.ll_firmware);
        ll_permission.setOnClickListener(this);
        ll_soft.setOnClickListener(this);
        ll_firmware.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.ll_permission:
                intent =  new Intent();
                /*intent.setAction("android.intent.action.MAIN");
                intent.setClassName("com.android.settings", "com.android.settings.ManageApplications");
                startActivity(intent);*/
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", "com.mantic.control", null);
                intent.setData(uri);
                startActivity(intent);
                break;
            case R.id.ll_soft:
                Beta.checkUpgrade(true,false);
//                showLoadingProgress();
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        dismissLoadingProgress();
//                    }
//                },1000);
                break;
            case R.id.ll_firmware:
                  intent=new Intent(SettingActivity.this,FirmwareActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showLoadingProgress() {

        mDialog = new ProgressDialog(mContext);
        mDialog.setIndeterminate(true);
        mDialog.setMessage(getString(R.string.checking));
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    public void dismissLoadingProgress() {
        if (mDialog != null && mDialog.isShowing()) {
            ToastUtils.showShortSafe(R.string.apk_latest);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
