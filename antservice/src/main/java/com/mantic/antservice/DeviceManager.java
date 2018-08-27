package com.mantic.antservice;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

//import com.baidu.duersdk.DuerSDK;
//import com.baidu.duersdk.DuerSDKFactory;
//import com.baidu.duersdk.utils.AppLogger;
//import com.baidu.duersdk.DuerSDK;
//import com.baidu.duersdk.DuerSDKFactory;
//import com.baidu.duersdk.utils.AppLogger;
import com.baidu.iot.sdk.DeviceAPI;
import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.DeviceInfo;
import com.baidu.iot.sdk.model.PageInfo;
import com.baidu.iot.sdk.model.PropertyData;
import com.mantic.antservice.baidu.listener.UnbindDeviceListener;
import com.mantic.antservice.listener.BindRequestListener;
import com.mantic.antservice.listener.GetUnbindDeviceInfoListener;
import com.mantic.antservice.util.Logger;

/**
 * Created by Jia on 2017/5/3.
 */

public class DeviceManager {
    public static final String TAG = "antservice";
    public String antService = "baidu";
    private static DeviceManager mInstance;
    public static final boolean idRda = true;

    public DeviceAPI deviceApi;

    public synchronized static DeviceManager getInstance() {
        if (mInstance == null) {
            mInstance = new DeviceManager();
        }
        return mInstance;
    }

    public DeviceManager() {

    }

    public void initAntService(Application app) {
        if (antService.equals("baidu")) {
            Logger.i(TAG, "init baiduIot");
            IoTSDKManager ioTManager = IoTSDKManager.getInstance();
            ioTManager.initSDK(app);
            deviceApi = ioTManager.createDeviceAPI();
        } else {
            //
        }
    }

    public DeviceAPI getBaiduDeviceApi() {
        if (deviceApi != null) {
            return deviceApi;
        } else {
            return IoTSDKManager.getInstance().createDeviceAPI();
        }
    }

    public void getUnbindDeviceInfo(String id, String token, final GetUnbindDeviceInfoListener listener) {
        if (deviceApi != null) {
            deviceApi.getUnbindDeviceInfo(id, token, new IoTRequestListener<DeviceInfo>() {
                @Override
                public void onSuccess(HttpStatus httpStatus, DeviceInfo deviceInfo, PageInfo pageInfo) {
                    com.baidu.iot.sdk.model.DeviceInfo device = (com.baidu.iot.sdk.model.DeviceInfo) deviceInfo;
                    listener.onSuccess(device.getDeviceUuid());
                    Logger.i(TAG, " onSuccess getDeviceUuid = " + device.getDeviceUuid());
                }

                @Override
                public void onFailed(HttpStatus code) {
                    Logger.i(TAG, "get device info failed code=" + code);
                    listener.onFailed();
                }

                @Override
                public void onError(IoTException e) {
                    Logger.i(TAG, "get device info error=" + e);
                    listener.onError();
                }
            });
        }
    }


    public void getPropertyKey(String deviceUuid, String propertyKey, IoTRequestListener<PropertyData> listener) {
        if (deviceApi != null) {
            deviceApi.getDevicePropertyData(deviceUuid, propertyKey, listener);
        }
    }

    public void bindDevice(final String id, String token, final BindRequestListener listener) {
        if (deviceApi != null) {
            deviceApi.bindDevice(id, token, new IoTRequestListener<Boolean>() {
                @Override
                public void onSuccess(HttpStatus httpStatus, Boolean deviceInfo, PageInfo pageInfo) {
                    Logger.i(TAG, " bindSuccess ..........");
                    Logger.i(TAG, " deviceInfo: " + deviceInfo);

                    listener.onSuccess(id);
                }

                @Override
                public void onFailed(HttpStatus httpStatus) {
                    Logger.i(TAG, " bindFailed .........." + httpStatus.toString());
                    listener.onFailed(id,httpStatus);
                }

                @Override
                public void onError(IoTException e) {
                    Logger.i(TAG, " bindError ..........");
                    listener.onError();
                }
            });
        }
    }

    public void unbindDevice(final String id, Context context, final BindRequestListener listener) {
        if (deviceApi != null) {
            deviceApi.unBindDevice(id, new UnbindDeviceListener<Boolean>(context) {
                @Override
                public void onSuccess(HttpStatus code, Boolean obj, PageInfo info) {
                    if (obj){
                        listener.onSuccess(id);
                    }
//                    Intent it = new Intent();
//                    it.putExtra("result", "1");
//                    setResult(DeviceControlActivity.ACTIVITY_RESULT_UNBIND, it);
//                    finish();
                }

                @Override
                public void onFailed(HttpStatus code) {
                    super.onFailed(code);
                    listener.onFailed(id,code);
                }

                @Override
                public void onError(IoTException error) {
                    super.onError(error);
                    listener.onError();
                }

            });

        }
    }
}
