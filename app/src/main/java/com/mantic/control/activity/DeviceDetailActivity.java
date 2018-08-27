package com.mantic.control.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.DeviceOtaInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.baidu.iot.sdk.net.RequestMethod;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.api.account.AccountRetrofit;
import com.mantic.control.api.account.AccountServiceApi;
import com.mantic.control.api.account.AccountUrl;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.control.R;
import com.mantic.control.data.DataFactory;
import com.mantic.control.listener.BaiduUnbindRequest;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.yokeyword.swipebackfragment.SwipeBackActivity;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jayson on 2017/6/21.
 */

public class DeviceDetailActivity extends SwipeBackActivity implements TitleBar.OnButtonClickListener,
        View.OnClickListener, DataFactory.OnUpdateDeviceNameListener, DataFactory.OnDeviceModeChangeListener{
    private static final String TAG = "DeviceDetailActivity";
    private RecyclerView infoView;
    private TextView nameText;
    private TextView wifiText;
    private ImageView osNewIcon;
    private Context mContext;
    private TitleBar device_detail_titlebar;
    private DeviceInfoAdapter infoAdapter;
    List<String> infoName = new ArrayList<String>();
    List<String> infoInfo = new ArrayList<String>();
    private String deviceId;
    private DataFactory mDataFactory;
    private ManticApplication application;
    private String deviceVersion;
    private String deviceString;
    private String localIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            StatusBarUtil.setColor(DeviceDetailActivity.this, Color.parseColor("#f9f9fa"), 1);
//            StatusBarHelper.statusBarLightMode(this);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(DeviceDetailActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        mContext = this;
        deviceVersion = SharePreferenceUtil.getDeviceVersion(this);
        initData();
        String name = SharePreferenceUtil.getDeviceName(this);

        deviceId = SharePreferenceUtil.getDeviceId(this);

        infoAdapter = new DeviceInfoAdapter();
        device_detail_titlebar = (TitleBar) findViewById(R.id.device_detail_titlebar);
        device_detail_titlebar.setTitleText(name);
        device_detail_titlebar.setOnButtonClickListener(this);
        infoView = (RecyclerView)findViewById(R.id.device_info_view);
        infoView.setLayoutManager(new LinearLayoutManager(this));
        infoView.addItemDecoration(new DeviceInfoDecoration(this));
        infoView.setAdapter(infoAdapter);
        osNewIcon = (ImageView) findViewById(R.id.new_os_version_icon);

        findViewById(R.id.name_info_content).setOnClickListener(this);
        findViewById(R.id.update_firmware).setOnClickListener(this);
        findViewById(R.id.restore_factory).setOnClickListener(this);
        findViewById(R.id.start_wifi).setOnClickListener(this);
        nameText = (TextView) findViewById(R.id.name_eidt_text);
        wifiText = (TextView) findViewById(R.id.wifi_name);
        mDataFactory = DataFactory.newInstance(this);
        mDataFactory.registerUpdateDeviceNameListener(this);
        mDataFactory.registerDeviceModeChangeListener(this);
        application = (ManticApplication)this.getApplicationContext();
//        if(SharePreferenceUtil.getCustomName(this).equals("pidianchong")){
//            deviceString = "\"" + "屁颠虫" + SharePreferenceUtil.getDeviceId(this) + "\"";
//        }else  if (SharePreferenceUtil.getCustomName(this).equals("yalanshi")){
//            deviceString =  "\"" + "雅兰仕" + SharePreferenceUtil.getDeviceId(this) + "\"";
//        }else {
//            deviceString = "\"" + "酷曼" + SharePreferenceUtil.getDeviceId(this) + "\"";
//        }
        deviceString = "\"" + SharePreferenceUtil.getDeviceName(this) + "\"";

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateName();
        updateDeviceInfo();
        checkNewVersion();
    }

    private int getResourceListIndex(String node_name) {
        int index = -1;
        if (application.getResourceList() != null) {
            for (int i = 0; i < application.getResourceList().size(); i++) {
                if (application.getResourceList().get(i).getName().equals(node_name)) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private void updateDeviceInfo() {
        final int index = getResourceListIndex("DeviceinfoQuery");
        if (index != -1)
            IoTSDKManager.getInstance().createDeviceAPI().controlDevice(deviceId, application.getResourceList().get(index), RequestMethod.GET,null, new IoTRequestListener<ControlResult>() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    Glog.i("jys","updateDeviceInfo -> sucess: " + ":" + obj.code +": ---------: " + obj.name +": --------: " + obj.content);
//                    infoInfo.set(1,info.v)
                    if (obj.content != null){
                    try {
                        JSONObject jsonObject = new JSONObject(obj.content);
                        JSONObject infoQuery = jsonObject.getJSONObject("DeviceinfoQuery");
                        String version = infoQuery.getString("ver");

                        String ip = infoQuery.getString("ip");
                        Glog.i("jys","ip: " + ip);
                        Glog.i("jys","version: " + version);

                        if(version != null && !version.equals(deviceVersion)){
                            SharePreferenceUtil.setDeviceVersion(getApplicationContext(), version);
                            infoInfo.set(0,version);
                        }else {
                            infoInfo.set(0,deviceVersion);
                        }

                        if(ip!=null && !ip.equals(SharePreferenceUtil.getCurrentDeviceIp(mContext))){
                            SharePreferenceUtil.setCurrentDeviceIp(mContext,ip);
                            infoInfo.set(2,ip);
                        }else {
                            infoInfo.set(2,SharePreferenceUtil.getCurrentDeviceIp(mContext));
                        }

                        infoAdapter.notifyDataSetChanged();
//                        if (!deviceVersion.equals(version)||ip!=null){
//                            SharePreferenceUtil.setDeviceVersion(getApplicationContext(), version);
//                            infoInfo.set(0,version);
//                            infoInfo.set(2,ip);
//                            infoAdapter.notifyDataSetChanged();
//                        }else {
//                            infoInfo.set(2,ip);
//                            infoInfo.set(0,deviceVersion);
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                }
                @Override
                public void onFailed(HttpStatus code) {
                    Glog.i("jys","updateDeviceInfo -> onFailed: " );
                }
                @Override
                public void onError(IoTException error) {
                    Glog.i("jys","updateDeviceInfo -> onError: " );
                }
            });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDataFactory.unregisterUpdateDeviceNameListener(this);
        mDataFactory.unregisterDeviceModeChangeListener(this);
    }

    private void updateName() {
        nameText.setText(SharePreferenceUtil.getDeviceName(this));
        wifiText.setText(SharePreferenceUtil.getDeviceWifi(this));
    }

    @Override
    public void updateDeviceName(String deviceName) {
        nameText.setText(deviceName);
        device_detail_titlebar.setTitleText(deviceName);
    }


    public void checkNewVersion(){
        IoTSDKManager.getInstance().createUpdateAPI().checkOta(SharePreferenceUtil.getDeviceId(this), new IoTRequestListener<DeviceOtaInfo>() {
            @Override
            public void onSuccess(HttpStatus httpStatus, DeviceOtaInfo otaInfo, PageInfo pageInfo) {
                Glog.i(TAG,"checkOta -> onSuccess...");
                if (otaInfo != null){
                    if (otaInfo.isUpdating){
                        ToastUtils.showShortSafe("正在升级");
                    }else if (otaInfo.newOsVersion != null){
                        //new
                        osNewIcon.setVisibility(View.VISIBLE);
                    }else {
                        //no
                        osNewIcon.setVisibility(View.GONE);
                    }
                }else {
                    //no
                    osNewIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailed(HttpStatus httpStatus) {
                Glog.i(TAG,"checkOta -> onFailed: " + httpStatus);
            }

            @Override
            public void onError(IoTException e) {
                Glog.i(TAG,"checkOta -> onError: " + e);
            }
        });
    }

    @Override
    public void deviceModeChange(int deviceMode) {
        if (deviceMode == 0) {
            infoInfo.set(3, "网络模式");
        } else if (deviceMode == 1) {
            infoInfo.set(3, "蓝牙模式");
        } else if (deviceMode == 2) {
            infoInfo.set(3, "AUX模式");
        } else if (deviceMode == 6) {
            infoInfo.set(3, "SD卡模式");
        } else {
            infoInfo.set(3, "未知模式");
        }
        infoName.set(3, "设备模式");
        infoAdapter.notifyDataSetChanged();
    }

    //
    private void initData() {
        localIp = Util.getIp(this);
//        infoName.add("品牌型号");
        infoName.add("固件版本");
        infoName.add("序列号");
        infoName.add("IP地址");
        infoName.add("设备模式");
//        infoName.add("Wi-Fi名称");
//        if(SharePreferenceUtil.getCustomName(this).equals("pidianchong")){
//            infoInfo.add("屁颠虫");
//        }else  if (SharePreferenceUtil.getCustomName(this).equals("yalanshi")){
//            infoInfo.add("雅兰仕");
//        }else {
//            infoInfo.add("Mantic");
//        }

        infoInfo.add(deviceVersion);
        infoInfo.add(SharePreferenceUtil.getDeviceId(this));
        infoInfo.add(SharePreferenceUtil.getCurrentDeviceIp(this));
        if (SharePreferenceUtil.getDeviceMode(this) == 1){
            infoInfo.add("蓝牙模式");
        } else if (SharePreferenceUtil.getDeviceMode(this) == 2){
            infoInfo.add("AUX模式");
        } else if (SharePreferenceUtil.getDeviceMode(this) == 6) {
            infoInfo.add("SD卡模式");
        } else if (SharePreferenceUtil.getDeviceMode(this) == 0 || SharePreferenceUtil.getDeviceMode(this) == -1){
            infoInfo.add("网络模式");
        } else {
            infoInfo.add("未知模式");
        }

//        infoInfo.add(SharePreferenceUtil.getDeviceWifi(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.name_info_content:
                startRename();
                break;
            case R.id.update_firmware:
                startFirmUpdate();
                break;
            case R.id.restore_factory:
                restoreFactory();
                break;
            case R.id.start_wifi:
                restartNetwork();
                break;
        }
    }

    private void restartNetwork() {
        Intent intent = new Intent(this,NetworkConfigActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    private void startRename() {
        Intent intent = new Intent(this,DeviceRenameActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    private void startFirmUpdate(){
        Intent intent = new Intent(this,FirmwareActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    private void restoreFactory(){
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
        String test = String.format(getResources().getString(R.string.factory_content),  deviceString);
        mBuilder.setTitle(mContext.getString(R.string.factory_title));
        mBuilder.setMessage(test);
        mBuilder.setPositiveButton(getString(R.string.ok), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
                dialog.dismiss();
                coomaanUnbindDevice();


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


    /**
     * 酷曼解绑接口
     * return:{
     * "router_id": "router_id",
     * "user_id": "b5231c42c5f7431caaa5076883634d92"
     * }
     */
    private void coomaanUnbindDevice(){
        String accessToken = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        Glog.i(TAG,"coomaanUnbindDevice - > accessToken: " + accessToken);
        String user_id = SharePreferenceUtil.getUserId(getApplicationContext());

        AccountServiceApi accountServiceApi = AccountRetrofit.getInstance().create(AccountServiceApi.class);
        Map<String,String> map = new HashMap<String, String>();
        map.put("Content-Type","application/json");
        map.put("Authorization","Bearer " + accessToken);
        map.put("Lc",ManticApplication.channelId);

        String request = "{\"user_id\":\""+ user_id+"\",\"device_uuid\":\""+ deviceId+"\"}";

        Glog.i(TAG,"request: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),request);

        Call<ResponseBody> call = accountServiceApi.deviceUnbind(map,body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()){
                    try {
                        JSONObject mainObject; //json 主体
                        mainObject = new JSONObject(response.body().string());
                        Glog.i(TAG,"Coomaan -> deviceUnbind success: "+ new Gson().toJson(mainObject));
                        if (!AccountUrl.BASE_URL.contains("v2")){
                            String  data  = new Gson().toJson(mainObject);
                        }else {// server v2 接口
                            JSONObject dataObject = mainObject.getJSONObject("data");
                            String  data  = new Gson().toJson(dataObject);
                        }

                        if (mainObject.getString("retcode").equals("0")){
                            DeviceStateController.getInstance(mContext, SharePreferenceUtil.getDeviceId(mContext)).sendPlayMode("{\"mode\": \"wifi_mode\",\"factory\": 1}", null);
                            DeviceManager.getInstance().unbindDevice(deviceId, mContext, new BaiduUnbindRequest(DeviceDetailActivity.this));
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i(TAG,"coomaanUnbindDevice fail: ");
            }
        });
    }

    private void delDeviceInformation(){
        SharePreferenceUtil.clearDeviceData(this);
        SharePreferenceUtil.clearSettingsData(this);
    }

    @Override
    public void onLeftClick() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {

    }

    class DeviceInfoAdapter extends RecyclerView.Adapter<DeviceInfoAdapter.DeviceInfoHolder>{
        private DeviceInfoAdapter(){}

        @Override
        public DeviceInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeviceInfoHolder(LayoutInflater.from(mContext).inflate(R.layout.device_info_item, parent ,false));
        }

        @Override
        public void onBindViewHolder(DeviceInfoHolder holder, int position) {
            holder.nameText.setText(infoName.get(position));
            holder.infoText.setText(infoInfo.get(position));
        }

        @Override
        public int getItemCount() {
            Glog.i(TAG,"COUNT:  " + infoName.size());
            return infoName.size();
        }

        class DeviceInfoHolder extends RecyclerView.ViewHolder{
            TextView nameText,infoText;
            DeviceInfoHolder(View itemView) {
                super(itemView);
                nameText = (TextView) itemView.findViewById(R.id.info_name);
                infoText = (TextView) itemView.findViewById(R.id.info_info);
            }
        }
    }

    public static class DeviceInfoDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private int divideHeight = 1;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public DeviceInfoDecoration(Context context){
            this.ctx = context;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.channel_detail_more_list_item_left_padding));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
            divideHeight = 1;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount; i++) { //
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + divideHeight;
//                if (i == childCount - 1) {
//                    c.drawRect(0, top, right, bottom, dividerPaint);
//                } else {
                    c.drawRect(left, top, right, bottom, dividerPaint);
//                }
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
