package com.mantic.control.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.baidu.iot.sdk.HttpStatus;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.listener.BindRequestListener;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.DeviceDetailActivity;
import com.mantic.control.activity.LoadingActivity;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelClearRsBean;
import com.mantic.control.data.DataFactory;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTToastUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Util;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jia on 2017/6/7.
 */

public class BaiduUnbindRequest  implements BindRequestListener{
    private static final String TAG = "BaiduUnbindRequest";
    private Activity mActivity;

    public BaiduUnbindRequest(Activity activity){
        mActivity = activity;
    }
    @Override
    public void onSuccess(final String id) {
        Glog.i(TAG,"UUID: " + id);
        /*MyChannelOperatorServiceApi mMyChannelOperatorServiceApi =  MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class);
        Call<MyChannelClearRsBean> clearCall = mMyChannelOperatorServiceApi.postMyChannelClearQuest(Util.createClearRqBean(mActivity));
        MyChannelManager.clearMyChannel(clearCall, new Callback<MyChannelClearRsBean>() {
            @Override
            public void onResponse(Call<MyChannelClearRsBean> call, Response<MyChannelClearRsBean> response) {
                if (response.isSuccessful()) {
                    ChannelPlayListManager.getInstance().clearChannelPlayList(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                restartApp();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    }, mActivity);
                }
            }

            @Override
            public void onFailure(Call<MyChannelClearRsBean> call, Throwable t) {

            }
        });*/

        ChannelPlayListManager.getInstance().clearChannelPlayList(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    restartApp();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        }, mActivity);

    }

    @Override
    public void onFailed(String  uuid, HttpStatus httpStatus) {
        Glog.i(TAG,"onFailed...: " + httpStatus);
        ToastUtils.showShortSafe(R.string.factory_failed);
    }

    @Override
    public void onError() {
        Glog.i(TAG,"onError...: ");
        ToastUtils.showShortSafe(R.string.factory_failed);
    }

    private void delDeviceInformation(){
        SharePreferenceUtil.clearDeviceData(mActivity);
        SharePreferenceUtil.clearSettingsData(mActivity);
    }

    private void restartApp(){
        delDeviceInformation();
        DataFactory.newInstance(mActivity).clearDataFactory();
        Intent intent = new Intent(mActivity, LoadingActivity.class);
        mActivity.sendBroadcast(new Intent("close_main"));
        mActivity.startActivity(intent);
        mActivity.finish();

    }
}
