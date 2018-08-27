package com.mantic.control.device;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.iot.sdk.DeviceAPI;
import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.DeviceOnlineStatus;
import com.baidu.iot.sdk.model.DeviceResource;
import com.baidu.iot.sdk.model.PageInfo;
import com.baidu.iot.sdk.model.PropertyData;
import com.baidu.iot.sdk.model.PropertyType;
import com.baidu.iot.sdk.model.TaskInfo;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.R;
import com.mantic.antservice.baidu.control.ControlManager;
import com.mantic.antservice.baidu.listener.DeviceControlListener;
import com.mantic.antservice.baidu.setting.DeviceSettingManager;
import com.mantic.antservice.util.Logger;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jia on 2017/5/3.
 */

public class ControlTaskService extends Service {
    private final static String TAG = "ControlTaskService";
    private HashMap<String, String> devReportMap = new HashMap<String, String>();
    public static final String DEVICE_STATUS = "device_statue";
    public static final String ONLINE = "online";
    private DeviceAPI deviceApi;
    private String device;
    private boolean online = false;
    private boolean isAlive = true;
    private int count = 1;//获取第一次进入的状态的次数
    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent) {
        Logger.i(TAG, "onBind");
        return null;
    }


    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        isAlive = false;
        devReportMap.clear();
//        getTasksStatus.interrupt();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Context mContext = this;
        device = SharePreferenceUtil.getDeviceId(mContext);
        IoTSDKManager mIoTManager = IoTSDKManager.getInstance();
        deviceApi = mIoTManager.createDeviceAPI();

        if (!getTasksStatus.isAlive()){
            getTasksStatus.start();
//            getTasksStatus.run();
        }
        if (!getOnlineStatus.isAlive()){
            getOnlineStatus.start();
//            getOnlineStatus.run();
        }
    }

    /**
     * 检查设备是否在线，10秒轮询一次
     */
    private Thread getOnlineStatus = new Thread(new Runnable() {
        @Override
        public void run() {
            if (isAlive){
                deviceApi.getDevicesOnlineStatus(new String[]{device}, new IoTRequestListener<List<DeviceOnlineStatus>>() {
                    @Override
                    public void onSuccess(HttpStatus code, List<DeviceOnlineStatus> obj, PageInfo pageInfo) {
                        if (obj != null){
                            if (obj.size() > 0) {
                                for (int i = 0; i < obj.size(); i++) {
                                    if (obj.get(i).getStatus().equals("true")) {
                                        online = true;
                                    } else {
                                        online = false;
                                    }
                                }
                            }
                        }
                        Glog.i(TAG,"online:   " + online);
                        Intent intent = new Intent(DEVICE_STATUS);
                        intent.putExtra(ONLINE,online);
                        sendBroadcast(intent);
                    }

                    @Override
                    public void onFailed(HttpStatus httpStatus) {
                        Intent intent = new Intent(DEVICE_STATUS);
                        intent.putExtra(ONLINE,false);
                        sendBroadcast(intent);
                    Glog.i(TAG,"online httpStatus:   " + httpStatus);
                    }

                    @Override
                    public void onError(IoTException e) {
                    Glog.i(TAG,"online httpStatus:   " + e);
                    }
                });

                handler.postDelayed(getOnlineStatus, DeviceSettingManager.getInstance().getDefaultRefreshTaskTime()*10);//10秒轮循一次
            } else {
                handler.removeCallbacks(getOnlineStatus);
            }
        }
    });


    private void getFirstPlayState() {
        DeviceManager.getInstance().getPropertyKey(SharePreferenceUtil.getDeviceId(ControlTaskService.this), "PlaySwitchReport", new IoTRequestListener<PropertyData>() {
            @Override
            public void onSuccess(HttpStatus code, PropertyData obj, PageInfo info) {
                if (obj != null) {
                    Glog.i("lbj", obj.getTimeStamp() + "  " + obj.getValue());
                    Intent intent = new Intent();
                    intent.putExtra("deviceUuid", device);
                    intent.putExtra("online", online);
                    intent.putExtra("key", "PlaySwitchReport");
                    intent.putExtra("timestamp", obj.getTimeStamp());
                    intent.putExtra("value", obj.getValue());
                    intent.setAction(ControlManager.DEVICE_REPORT_IND_ACTION);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onFailed(HttpStatus code) {

            }

            @Override
            public void onError(IoTException error) {

            }
        });
    }

    /**
     * 改线程获取第一次进入应用音箱最后一次的播放状态
     */
    private Thread getTasksStatus = new Thread(new Runnable() {
        @Override
        public void run() {
//            while (isAlive) {
                /*deviceApi.getDeviceHistory(device, "PlaySwitchReport", getStartTime(), getEndTime(), new IoTRequestListener<List<PropertyData>>() {
                    @Override
                    public void onSuccess(HttpStatus httpStatus, List<PropertyData> propertyDatas, PageInfo pageInfo) {
                        Glog.i(TAG,"getDeviceHistory - onSuccess --> " + propertyDatas.size());
                        if (propertyDatas.size() > 0){
                            Glog.i(TAG,"<<<<<<<<<<<<<<<=====================分割线==================>>>>>>>>>>>>>>>");
                            for (int i = propertyDatas.size() -1; i >= 0; i--){ //因为新记录放到了开头，所以必须反序遍历
                                PropertyData data = propertyDatas.get(i);
                                Glog.i(TAG,"data: " + data.getKey() + "---" + data.getValue() + "---" + data.getTimeStamp());
                                String mapKey = data.getTimeStamp();
                                if (devReportMap.get(mapKey) == null || (devReportMap.get(mapKey) != null && !devReportMap.get(mapKey).equals(mapKey))) {
                                    devReportMap.put(mapKey, mapKey);
                                    Intent intent = new Intent();
                                    intent.putExtra("deviceUuid", device);
                                    intent.putExtra("key", propertyDatas.get(i).getKey());
                                    intent.putExtra("value", propertyDatas.get(i).getValue());
                                    intent.putExtra("timestamp", propertyDatas.get(i).getTimeStamp());
                                    intent.putExtra("online", online);
                                    intent.setAction(ControlManager.DEVICE_REPORT_IND_ACTION);

                                    Glog.i(TAG, "PlaySwitchReportBroadCast dev report: " + propertyDatas.get(i).getKey()  + "Time: " + propertyDatas.get(i).getTimeStamp());
                                    if (i == 0){ //发送最后一条记录 （第一次判断状态和每次的变化）

                                        sendBroadcast(intent);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailed(HttpStatus httpStatus) {
                        Glog.i(TAG,"getDeviceHistory - onFailed -->" + httpStatus);
                    }

                    @Override
                    public void onError(IoTException e) {
                        Glog.i(TAG,"getDeviceHistory - onFailed -->" + e);
                    }
                },null);

                try {
                    Thread.sleep(DeviceSettingManager.getInstance().getDefaultRefreshTaskTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/

                if (isAlive && count == 1) {
                    if (online) {
                        count--;
                        getFirstPlayState();
                    }

                    if (count == 0) {
                        handler.removeCallbacks(getTasksStatus);
                    } else {
                        handler.postDelayed(getTasksStatus, 1000);
                    }
                } else {
                    handler.removeCallbacks(getTasksStatus);
                }

        }

//        }
    });

    private String getEndTime() {
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        endTime = endTime.replaceAll(" ", "T");
        endTime += "Z";
//        Glog.i(TAG,"endTime: " + endTime);
        return endTime;
    }

    private String getStartTime() {
        Date dateNow = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(dateNow);
        cl.add(Calendar.HOUR_OF_DAY, -30);
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cl.getTime());
        startTime = startTime.replaceAll(" ", "T");
        startTime += "Z";
//        Glog.i(TAG, "startTime :" + startTime);
        return startTime;
    }

}
