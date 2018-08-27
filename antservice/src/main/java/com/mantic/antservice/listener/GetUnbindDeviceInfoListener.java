package com.mantic.antservice.listener;


/**
 * Created by Jia on 2017/5/3.
 */

public interface GetUnbindDeviceInfoListener {
    void onSuccess(String uuid);

    void onFailed();

    void onError();
}
