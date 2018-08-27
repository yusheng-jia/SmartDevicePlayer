package com.mantic.control.entiy;

import android.content.Context;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.data.Channel;
import com.mantic.control.utils.SharePreferenceUtil;

/**
 * Created by lin on 2017/9/7.
 */

public class SleepInfo {
    private String sleepTime;
    private int time;
    private boolean isSetting;

    public SleepInfo(String sleepTime, boolean isSetting, int time) {
        this.sleepTime = sleepTime;
        this.isSetting = isSetting;
        this.time = time;
    }

    public static void sendSleepTime(String sleepTime, final Channel.ChannelDeviceControlListenerCallBack callBack, Context context, boolean isStopSleep){
        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (callBack != null) {
                    callBack.onSuccess(code, obj, info);
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);

        String jsonStr = "";

        if (isStopSleep) {
            jsonStr = "{\"play_stop\":true,";
        } else {
            jsonStr = "{\"play_stop\":false,";
        }

        if (context.getString(R.string.after_current_audio).equals(sleepTime)) {
            jsonStr = jsonStr + "\"play_current\":true, \"play_time\":-1}";
        } else if (context.getString(R.string.not_open).equals(sleepTime)){
            jsonStr = jsonStr + "\"play_current\":false, \"play_time\":-2}";
        } else if (context.getString(R.string.after_ten_minute).equals(sleepTime)){
            jsonStr = jsonStr + "\"play_current\":false, \"play_time\":600}";
        } else if (context.getString(R.string.after_twenty_minute).equals(sleepTime)){
            jsonStr = jsonStr + "\"play_current\":false, \"play_time\":1200}";
        } else if (context.getString(R.string.after_thirty_minute).equals(sleepTime)){
            jsonStr = jsonStr + "\"play_current\":false, \"play_time\":1800}";
        } else if (context.getString(R.string.after_sixty_minute).equals(sleepTime)){
            jsonStr = jsonStr + "\"play_current\":false, \"play_time\":3600}";
        } else if (context.getString(R.string.after_ninety_minute).equals(sleepTime)){
            jsonStr = jsonStr + "\"play_current\":false, \"play_time\":5400}";
        }


        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(context, SharePreferenceUtil.getDeviceId(context)).sendTimeShutDown(jsonStr, listener);
        } else {
//            WebSocketController.getInstance().mConnection.sendTextMessage(command.writeVolCommand(volume),listener);
        }
    }


    public static void getSleepTime(final Channel.ChannelDeviceControlListenerCallBack callBack, Context mContext) {
        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if(callBack != null) {
                    callBack.onSuccess(code, obj, info);
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext, SharePreferenceUtil.getDeviceId(mContext)).getDeviceSleepTime(listener);
        } else {
        }
    }


    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public boolean isSetting() {
        return isSetting;
    }

    public void setSetting(boolean setting) {
        isSetting = setting;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
