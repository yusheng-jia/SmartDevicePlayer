package com.mantic.control.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.DeviceOtaInfo;
import com.baidu.iot.sdk.model.OtaPackageInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.entiy.ManticDeviceInfo;
import com.mantic.control.ui.adapter.FirmwareAdapter;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;

import me.yokeyword.swipebackfragment.SwipeBackActivity;

/**
 * Created by wujiangxia on 2017/5/3.
 */
public class FirmwareActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener{
    private final static String TAG = "FirmwareActivity";
    private Context mContext;
    private FirmwareAdapter firmwareAdapter;
    private List<ManticDeviceInfo> manticDeviceInfoList;
    private static String deviceid;
    private ProgressDialog progressBar;
    private LinearLayout latestView;
    private TextView updateText;
    private ImageView latestImage;
    RecyclerView deviceListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(FirmwareActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        mContext = this;
//        showTitle(R.string.firmware_update);
        manticDeviceInfoList = new ArrayList<>();
        deviceid = SharePreferenceUtil.getDeviceId(this);
        Glog.i(TAG,"deviceid: " + deviceid);
        latestView = (LinearLayout) findViewById(R.id.latest_view);
        updateText = (TextView)findViewById(R.id.update_text);
        latestImage = (ImageView) findViewById(R.id.image_latest);
        TitleBar firmware_titlebar = (TitleBar) findViewById(R.id.activity_firmware_titlebar);
        firmware_titlebar.setOnButtonClickListener(this);
//        initData();
        deviceListView = (RecyclerView) findViewById(R.id.list_firmware);
        firmwareAdapter = new FirmwareAdapter(this);
        deviceListView.addItemDecoration(new FirmWareItemDecoration(mContext));
        deviceListView.setLayoutManager(new LinearLayoutManager(mContext));
        deviceListView.setAdapter(firmwareAdapter);
        firmwareAdapter.refresh(manticDeviceInfoList);

        latestView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (updateText.getText().equals(getString(R.string.ota_update_failed_show))){
                    checkNewVersion();
                }
            }
        });

    }

    public static DeviceOtaInfo deviceOtaInfo = null;

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNewVersion();
            }
        }, 200);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initData(){
        ManticDeviceInfo info = new ManticDeviceInfo();
        info.setDeviceName(SharePreferenceUtil.getDeviceName(mContext));
        if (SharePreferenceUtil.getDeviceVersion(mContext) != null){
            info.setOldVersion(SharePreferenceUtil.getDeviceVersion(mContext));
        }else {
            info.setOldVersion("1.1.1");
        }
        info.setNewVersion("1.2.1");
        manticDeviceInfoList.add(info);
    }

    private void updateNewVersion(String version){
        manticDeviceInfoList.clear();
        ManticDeviceInfo info = new ManticDeviceInfo();
        info.setDeviceName(SharePreferenceUtil.getDeviceName(mContext));
        if (SharePreferenceUtil.getDeviceVersion(mContext) != null){
            info.setOldVersion(SharePreferenceUtil.getDeviceVersion(mContext));
        }else {
            info.setOldVersion("1.1.1");
        }
        info.setNewVersion(version);
        manticDeviceInfoList.add(info);
    }

    private void showLatestView(){
        updateText.setText(R.string.ota_update_success_show);
        latestView.setVisibility(View.VISIBLE);
        deviceListView.setVisibility(View.GONE);
        latestImage.setImageResource(R.drawable.firm_is_latest);
    }

    private void showFailedView(){
        updateText.setText(R.string.ota_update_failed_show);
        latestView.setVisibility(View.VISIBLE);
        deviceListView.setVisibility(View.GONE);
        latestImage.setImageResource(R.drawable.net_failed);
    }

    private void dismissProgress(){
        if (progressBar.isShowing()){
            progressBar.dismiss();
        }
    }
    public void checkNewVersion(){
        progressBar = ProgressDialog.show(mContext,null,"检测固件版本中...");
        IoTSDKManager.getInstance().createUpdateAPI().checkOta(deviceid, new IoTRequestListener<DeviceOtaInfo>() {
            @Override
            public void onSuccess(HttpStatus httpStatus, DeviceOtaInfo otaInfo, PageInfo pageInfo) {
                Glog.i(TAG,"checkOta -> onSuccess...");
                if (otaInfo != null){
                    if (otaInfo.isUpdating){
                        ToastUtils.showShortSafe("正在升级");
                    }else if (otaInfo.newOsVersion != null){
                        deviceOtaInfo = otaInfo;
                        Glog.i(TAG,"可更新到固件版本：" + otaInfo.newOsVersion);
                        getDeviceOtaInfo(String.valueOf(otaInfo.packageId));
                        progressBar.dismiss();
                        updateNewVersion(otaInfo.newOsVersion);
                        firmwareAdapter.refresh(manticDeviceInfoList);
                    }else {
//                        ToastUtils.showShortSafe("当前是最新版本");
                        dismissProgress();
                        showLatestView();

                    }
                }else {
                    dismissProgress();
                    showLatestView();
                }
            }

            @Override
            public void onFailed(HttpStatus httpStatus) {
                Glog.i(TAG,"checkOta -> onFailed: " + httpStatus);
                showFailedView();
                dismissProgress();
            }

            @Override
            public void onError(IoTException e) {
                showFailedView();
                dismissProgress();
                Glog.i(TAG,"checkOta -> onError: " + e);
            }
        });
    }



    private void getDeviceOtaInfo(String packageId){
        Glog.i(TAG,"packageId : " + packageId);
        IoTSDKManager.getInstance().createUpdateAPI().getOtaPackageInfo(packageId, new IoTRequestListener<OtaPackageInfo>() {
            @Override
            public void onSuccess(HttpStatus httpStatus, OtaPackageInfo otaPackageInfo, PageInfo pageInfo) {
                Glog.i(TAG,"otaPackageInfo: " + otaPackageInfo.httpDownloadUri + " - " + otaPackageInfo.coapDownloadUri + " - " +
                        otaPackageInfo.fileId + " - " +otaPackageInfo.projectId + " - " + otaPackageInfo.osVersionId + " - "
                        + otaPackageInfo.md5 + " - " + otaPackageInfo.updateTime + " - ");
            }

            @Override
            public void onFailed(HttpStatus httpStatus) {
                Glog.i(TAG,"getDeviceOtaInfo - > onFailed ... " + httpStatus);
            }

            @Override
            public void onError(IoTException e) {
                Glog.i(TAG,"getDeviceOtaInfo - > onError ... " + e);
            }
        });
    }


    public static void updateOta(){
        if (deviceOtaInfo != null){
            IoTSDKManager.getInstance().createUpdateAPI().createOtaTask(deviceOtaInfo, new IoTRequestListener<Boolean>() {
                @Override
                public void onSuccess(HttpStatus httpStatus, Boolean aBoolean, PageInfo pageInfo) {
                    Glog.i(TAG,"OtaTask - > onSuccess -- > aBoolean: " + aBoolean);

                }

                @Override
                public void onFailed(HttpStatus httpStatus) {
                    Glog.i(TAG,"OtaTask - > onFailed" + httpStatus);
                }

                @Override
                public void onError(IoTException e) {
                    Glog.i(TAG,"OtaTask - > onError " + e);
                }
            });
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

//    public void updateDeviceInfo(){
//        IoTSDKManager.getInstance().createDeviceAPI().getDeviceInfo(deviceid, new IoTRequestListener<DeviceInfo>() {
//            @Override
//            public void onSuccess(HttpStatus httpStatus, DeviceInfo deviceInfo, PageInfo pageInfo) {
//                Glog.i(TAG,"updateDeviceInfo -> onSuccess: " + deviceInfo);
//                if (SharePreferenceUtil.getDeviceVersion(mContext).equals("")){
//                    SharePreferenceUtil.setDeviceVersion(mContext,deviceInfo.getOsVersion());
////                    firmwareAdapter.refresh();
//                }
//            }
//
//            @Override
//            public void onFailed(HttpStatus httpStatus) {
//                Glog.i(TAG,"updateDeviceInfo -> HttpStatus: " + httpStatus);
//            }
//
//            @Override
//            public void onError(IoTException e) {
//                Glog.i(TAG,"updateDeviceInfo -> onError: " + e);
//            }
//        });
//    }



    public static class FirmWareItemDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private int divideHeight = 1;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public FirmWareItemDecoration(Context context) {
            this.ctx = context;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx, ResourceUtils.getXmlDef(this.ctx, R.dimen.channel_detail_more_list_item_left_padding));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
            divideHeight = 1;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount - 1; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + divideHeight;
                if (i == childCount - 1) {
                    c.drawRect(0, top, right, bottom, dividerPaint);
                } else {
                    c.drawRect(left, top, right, bottom, dividerPaint);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = divideHeight;
        }
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


