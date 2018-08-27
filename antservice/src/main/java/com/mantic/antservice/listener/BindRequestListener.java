package com.mantic.antservice.listener;

import com.baidu.iot.sdk.HttpStatus;

/**
 * Created by Jia on 2017/5/17.
 */

public interface BindRequestListener {
    void onSuccess(String id);

    void onFailed(String  uuid, HttpStatus httpStatus);

    void onError();
}
