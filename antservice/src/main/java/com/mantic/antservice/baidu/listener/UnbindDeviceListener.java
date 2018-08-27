package com.mantic.antservice.baidu.listener;

import android.content.Context;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTRequestListener;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.util.Logger;

/**
 * Created by Jia on 2017/6/7.
 */

public class UnbindDeviceListener<Boolean> implements IoTRequestListener<Boolean> {

    private final static String TAG = "UnbindDeviceListener";
    private Context mContext;

    public UnbindDeviceListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSuccess(HttpStatus code, Boolean obj, PageInfo info) {
//        IoTToastUtils.showShort(mContext, mContext.getString(R.string.unbind_succ));
    }

    @Override
    public void onFailed(HttpStatus code) {
        Logger.i(TAG, "unbind device failed code=" + code);
//        IoTToastUtils.showShort(mContext, mContext.getString(R.string.unbind_fail));
    }

    @Override
    public void onError(IoTException error) {
        Logger.i(TAG, "unbind device error=" + error);
//        IoTToastUtils.showShort(mContext, mContext.getString(R.string.unbind_fail));
    }
}
