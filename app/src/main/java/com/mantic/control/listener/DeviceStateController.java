package com.mantic.control.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.baidu.iot.sdk.DeviceAPI;
import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.DeviceResource;
import com.baidu.iot.sdk.model.PageInfo;
import com.baidu.iot.sdk.net.RequestMethod;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.baidu.control.ControlManager;
import com.mantic.antservice.baidu.listener.DeviceControlListener;
import com.mantic.antservice.util.Logger;
import com.mantic.control.ManticApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jia on 2017/5/3.
 */

public class DeviceStateController extends BroadcastReceiver {
    public static final String TAG = "DeviceStateController";
    private boolean isRegister = false;
    private Context mContext;
    static DeviceStateController sInstance;
    private ArrayList<DeviceReportCallback> mDevReportCallbacks = new ArrayList<DeviceReportCallback>();
    private DeviceAPI deviceApi;

    private String device = "002b000000001a";
    private ManticApplication application;
    private String command = null;
    private String data = null;
    DeviceControlListener<ControlResult> controlListener = null;

    public DeviceStateController() {

    }

    public interface DeviceReportCallback {
        public void onDeviceReport(String key, String value, String timestamp);
    }

    private DeviceStateController(Context context, String uuid) {
        mContext = context;
        application = (ManticApplication)context.getApplicationContext();
        deviceApi = DeviceManager.getInstance().deviceApi;
        device = uuid;
        Logger.i(TAG, "deviceApi:" + deviceApi + "device:" + uuid);
        deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ControlManager.DEVICE_REPORT_IND_ACTION);
        context.registerReceiver(this, filter);
        isRegister = true;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String deviceUuid = intent.getStringExtra("deviceUuid");
        Logger.i(TAG, "onReceive:" + action + "deviceUuid:" + deviceUuid);
        if (deviceUuid.equals(device)) {
//            int taskId = intent.getIntExtra("taskId", -1);
            String key = intent.getStringExtra("key");
            String value = intent.getStringExtra("value");
            String timestamp = intent.getStringExtra("timestamp");
            boolean online = intent.getBooleanExtra("online", false);
            Logger.i(TAG, "node name: " + value + " ,online: " + online);
            sendState(key, value,timestamp);
        }
    }


    public static synchronized DeviceStateController getInstance(Context context,String uuid) {
        Logger.i(TAG,"DeviceStateController -- > getInstance");
        if (sInstance == null) {
            sInstance = new DeviceStateController(context, uuid);
        }
        return sInstance;
    }

    public void addDevReportCallback(DeviceReportCallback cb) {
        mDevReportCallbacks.clear();
        mDevReportCallbacks.add(cb);
    }

    public void delStateChanngedCallback() {
        if (isRegister) {
            isRegister = false;
            mDevReportCallbacks.clear();
            mContext.unregisterReceiver(this);
            sInstance = null;
        }
    }

    private void sendState(String key, String value, String timestamp) {
        for (DeviceReportCallback cb : mDevReportCallbacks) {
            cb.onDeviceReport(key, value, timestamp);
        }
    }

    /** 暂停播放*/
    public void sendPauseMusic() {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendPauseMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x02, device, RequestMethod.PUT);
        }else {
            Logger.i(TAG, "Re - sendPauseMusic..." + index);
            command = "PauseMusicEmpty";
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    /** 音量控制*/
    public void sendPlayVolume(int volume, DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayVolume");
        Logger.i(TAG, "sendPlayVolume Volume " + volume + " index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), volume, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
            command = "sPlayVolume";
            data = volume+"";
            controlListener = listener;
        }
    }

    public void sendPlayProgress(int progress, DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayProgress");
        Logger.i(TAG, "PlayProgress Volume " + progress + " index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), progress, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
            command = "sPlayProgress";
            data = progress+"";
            controlListener = listener;
        }
    }

    /** 定时关闭*/
    public void sendTimeShutDown(String time, DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("ScheduleTime");
        Logger.i(TAG, "ScheduleTime " + time + " index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), time, device, RequestMethod.PUT, listener);
        }else { // index 为1 重新获取
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
            command = "sTimeShutDown";
            data = time;
            controlListener = listener;
        }
    }

    /** 播放歌曲*/
    public void sendPlayMusic(String url, DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlaySong");
        if (url != null) {
            Logger.i(TAG, "sendPlayMusic..." + index + " url: " + url.toString());
            if (index != -1) {
                sendCommand(application.getResourceList().get(index), url, device, RequestMethod.PUT, listener);
            }else {
                Logger.i(TAG, "Re - getDeviceResource..." + index);
                command = "PlayMusic";
                data = url;
                controlListener = listener;
                deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
            }
        } else {
            Logger.i(TAG, "sendPlayMusic..." + index + " url is null!");
        }
    }

    /** 暂停播放*/
    public void sendPauseMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendPauseMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x02, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
            command = "PauseMusic";
            controlListener = listener;
        }
    }

    /** 恢复播放*/
    public void sendResumeMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendResumeMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x03, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "ResumeMusic";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    public void sendStopMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendStopMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x01, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "StopMusic";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    //播放百度pass平台下一首歌曲
    public void sendNextMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendNextMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x04, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "NextMusic";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    //播放百度pass平台上一首歌曲
    public void sendPreMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendPreMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x05, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "PreMusic";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    //播放coomaan下一首歌曲
    public void sendCoomaanNextMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendCoomaanNextMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x06, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "NextMusic";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    //播放coomaan上一首歌曲
    public void sendCoomaanPreMusic(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayControl");
        Logger.i(TAG, "sendCoomaanPreMusic..." + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), 0x07, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "PreMusic";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    public void getDeviceInfo(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("DeviceinfoQuery");
        Logger.i(TAG, "getDeviceInfo index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), null, device, RequestMethod.GET, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "DeviceInfo";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    /**
     * 获取音量
     * @param listener
     */
    public void getDeviceVolume(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayVolume");
        Logger.i(TAG, "getDeviceVolume index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), null, device, RequestMethod.GET, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "gDeviceVolume";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    /**
     * 获取播放进度
     * @param listener
     */
    public void getDeviceProgress(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("PlayProgress");
        Logger.i(TAG, "getDeviceProgress index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), null, device, RequestMethod.GET, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "gDeviceProgress";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    /**
     * 获取睡眠时间
     * @param listener
     */
    public void getDeviceSleepTime(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("ScheduleTime");
        Logger.i(TAG, "getDeviceSleepTime index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), null, device, RequestMethod.GET, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "gDeviceSleepTime";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    /**
     * 模式切换 也可以恢复出厂设置
     * @param mode
     * @param listener
     */
    public void sendPlayMode(String  mode, DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("ModeSwitch");
        Logger.i(TAG, "ModeSwitch  " + mode);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), mode, device, RequestMethod.PUT, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "PlayMode";
            controlListener = listener;
            data = mode;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }

    /**
     * 获取音箱的设备模式
     * @param listener
     */
    public void getDevicePlayMode(DeviceControlListener<ControlResult> listener) {
        int index = getResourceListIndex("ModeSwitch");
        Logger.i(TAG, "getDevicePlayMode index: " + index);
        if (index != -1) {
            sendCommand(application.getResourceList().get(index), null, device, RequestMethod.GET, listener);
        }else {
            Logger.i(TAG, "Re - getDeviceResource..." + index);
            command = "gPlayMode";
            controlListener = listener;
            deviceApi.getDeviceResource(device, new DeviceResourceListener<DeviceResource>());
        }
    }


    public void sendCommand(DeviceResource.Resource resource, Object value, String mDeviceUuid, RequestMethod method, DeviceControlListener<ControlResult> listener) {
        Logger.i(TAG, "send command1 value=" + value);
        DeviceControlListener<ControlResult> controlListener = null;
        if (listener != null) {
            listener.setContext(this.mContext);
            listener.setResource(resource);
            controlListener = listener;
        } else {
            controlListener = new DeviceControlListener<ControlResult>(mContext, resource);
        }
        deviceApi.controlDevice(mDeviceUuid, resource, method, value, controlListener);
    }

    public void sendCommand(DeviceResource.Resource resource, Object value, String mDeviceUuid, RequestMethod method) {
        Logger.i(TAG, "send command value=" + value);
        deviceApi.controlDevice(mDeviceUuid, resource, method, value, new DeviceControlListener<ControlResult>(mContext, resource));
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

    public class DeviceResourceListener<DeviceResource> implements IoTRequestListener<DeviceResource> {
        @Override
        public void onSuccess(HttpStatus code, DeviceResource obj, PageInfo info) {
            Logger.i(TAG, "obj=" + obj);
            if (obj != null) {
                List<com.baidu.iot.sdk.model.DeviceResource.Resource> resource = ((com.baidu.iot.sdk.model.DeviceResource) obj).getResources();
                application.setResourceList((ArrayList) resource);
                if(resource.size()>0 && command!=null){ // 有数据点
                    if (command.equals("PlayMusic")){
                        if (data != null && controlListener!=null){
                            sendPlayMusic(data,controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("PauseMusicEmpty")){
                        sendPauseMusic();
                        command = null;
                    }else if (command.equals("sPlayVolume")){
                        if(data!=null && controlListener !=null){
                            sendPlayVolume(Integer.parseInt(data),controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("sPlayProgress")){
                        if(data!=null && controlListener !=null){
                            sendPlayProgress(Integer.parseInt(data),controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("sTimeShutDown")){
                        if(data!=null && controlListener !=null){
                            sendTimeShutDown(data,controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("PauseMusic")){
                        if (controlListener != null){
                            sendPauseMusic(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("ResumeMusic")){
                        if (controlListener != null){
                            sendResumeMusic(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("StopMusic")){
                        if (controlListener != null){
                            sendStopMusic(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("DeviceInfo")){
                        if (controlListener != null){
                            getDeviceInfo(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("gDeviceVolume")){
                        if (controlListener != null){
                            getDeviceVolume(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("gDeviceProgress")){
                        if (controlListener != null){
                            getDeviceProgress(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }

                    }else if (command.equals("gDeviceSleepTime")){
                        if (controlListener != null){
                            getDeviceSleepTime(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }else if (command.equals("PlayMode")){
                        if (data != null && controlListener != null){
                            sendPlayMode(data,controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    } else if (command.equals("gPlayMode")) {
                        if (controlListener != null){
                            getDevicePlayMode(controlListener);
                            command = null;
                            data = null;
                            controlListener = null;
                        }
                    }
                }
                mContext.sendBroadcast(new Intent("PassResourceDone"));
                for (int i = 0; i < resource.size(); i++) {
                    com.baidu.iot.sdk.model.DeviceResource.Resource item = resource.get(i);
                    Logger.i(TAG, "mResourceList getName=" + item.getName() + "\n\t"
                            + "getLabel:" + item.getLabel() + "\n\t"
                            + "getResourceType:" + item.getResourceType() + "\n\t"
                            + "getType:" + item.getType() + "\n\t"
                            + "getMethod:" + item.getMethod()
                    );
                }
            } else {

            }
        }

        @Override
        public void onFailed(HttpStatus code) {
            Logger.i(TAG, "onFailed=" + "code" + code);
        }

        @Override
        public void onError(IoTException error) {
            Logger.i(TAG, "onError=");
        }
    }
}
