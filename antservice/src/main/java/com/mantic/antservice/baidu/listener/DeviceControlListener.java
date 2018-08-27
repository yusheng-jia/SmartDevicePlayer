package com.mantic.antservice.baidu.listener;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.model.DeviceResource;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.R;
import com.mantic.antservice.util.Logger;
import com.mantic.antservice.util.ToastUtils;

/**
 * Created by Jia on 2017/5/3.
 */
public class DeviceControlListener<ControlResult> implements IoTRequestListener<ControlResult> {
    private final static String TAG = "DeviceControlListener";
    public Context mContext;
    public DeviceResource.Resource mResource;
    public static boolean showToast = true;
    public DeviceControlListener(){

    }

    public DeviceControlListener(Context context, DeviceResource.Resource resource) {
        this.setContext(context);
        this.setResource(resource);
    }

    public void setContext(Context context){
        this.mContext = context;
    }

    public void setResource(DeviceResource.Resource resource){
        this.mResource = resource;
    }

    @Override
    public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
        com.baidu.iot.sdk.model.ControlResult result = (com.baidu .iot.sdk.model.ControlResult) obj;
        if (result != null){
            Logger.i(TAG, "control code = " + code + "  result code: " + result.code + " name: " + result.name + " content：" + result.content);
            if (result.code == null && result.name == null && result.content == null){
                ToastUtils.showShortSafe(mContext.getText(R.string.make_sure_device_isonline));
            }
        }
//        if (result != null && !TextUtils.isEmpty(result.content)) {
//            Toast.makeText(mContext, mResource.getLabel() + mContext.getString(R.string.control_success), Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public void onFailed(HttpStatus code) {
        Logger.i(TAG, "onFailed, code = " + code);
        if (null != mContext) {
            SharedPreferences sp = mContext.getSharedPreferences("mantic_device", Context.MODE_PRIVATE);
            if(sp.getInt("device_mode", -1) == 0 || sp.getInt("device_mode", -1) == -1){
                if (showToast){
                    ToastUtils.showShortSafe(mContext.getString(R.string.send_control_fail));
                    showToast = false;
                }
            }
        }
    }

    @Override
    public void onError(IoTException error) {
        StackTraceElement[] stackElements = error.getStackTrace();
        for (int i = 0; i < stackElements.length; i++) {
            Logger.i(TAG, "onError: " + stackElements[i].getClassName());
            Logger.i(TAG, "onError: " + stackElements[i].getFileName());
            Logger.i(TAG, "onError: " + stackElements[i].getLineNumber());
            Logger.i(TAG, "onError: " + stackElements[i].getMethodName());
        }

        SharedPreferences sp = mContext.getSharedPreferences("mantic_device", Context.MODE_PRIVATE);
        if(sp.getInt("device_mode", -1) == 0 || sp.getInt("device_mode", -1) == -1){
            if (showToast){
                ToastUtils.showShortSafe(mContext.getString(R.string.send_control_fail));
                showToast = false;
            }
        }
    }
}
