package com.mantic.control.utils;

import android.content.Context;
import android.text.TextUtils;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.adapter.AudioSleepAdapter;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.entiy.SleepInfo;

import java.util.List;

/**
 * Created by lin on 2017/9/11.
 */

public class SleepTimeSetUtils {

    /**
     * 清除睡眠设置
     *
     * @param mContext
     * @param mDataFactory
     */
    public static void clearSleepTimeSet(final Context mContext, final DataFactory mDataFactory, final AudioSleepAdapter.SleepSetResultListener listener) {
        if (((ManticApplication) mContext.getApplicationContext()).isSleepTimeOn()) {
            String sleepTime = getSleepTime(mDataFactory);
            SleepInfo.sendSleepTime(sleepTime, new Channel.ChannelDeviceControlListenerCallBack() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    if (null != listener) {
                        listener.success();
                    }

                    ToastUtils.showShortSafe("音箱退出睡眠模式");
                    ((ManticApplication) mContext.getApplicationContext()).setSleepTimeOn(false);
                    resetSleepTime(mDataFactory);
                }

                @Override
                public void onFailed(HttpStatus code) {
                    if (null != listener) {
                        listener.fail();
                    }
                    ToastUtils.showShortSafe("音箱退出睡眠模式失败");
                }

                @Override
                public void onError(IoTException error) {
                    if (null != listener) {
                        listener.onError();
                    }
                    ToastUtils.showShortSafe("音箱退出睡眠模式失败");
                }
            }, mContext, true);
        }

    }

    /**
     * 清除睡眠设置，但是不下发命令
     * @param mContext
     * @param mDataFactory
     */
    public static void onlyClearSleepTimeSet(final Context mContext, final DataFactory mDataFactory) {
        if (((ManticApplication) mContext.getApplicationContext()).isSleepTimeOn()) {
            ((ManticApplication) mContext.getApplicationContext()).setSleepTimeOn(false);
            resetSleepTime(mDataFactory);
        }

    }


    /**
     * 只当当前睡眠模式为播放完当前歌曲清除睡眠设置
     *
     * @param mContext
     * @param mDataFactory
     */
    public static void clearSleepTimeSetByCurrent(final Context mContext, final DataFactory mDataFactory) {
        if (((ManticApplication) mContext.getApplicationContext()).isSleepTimeOn()) {
            String sleepTime = getSleepTime(mDataFactory);

            if (mContext.getString(R.string.after_current_audio).equals(sleepTime)) {
                SleepInfo.sendSleepTime(sleepTime, new Channel.ChannelDeviceControlListenerCallBack() {
                    @Override
                    public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                        ToastUtils.showShortSafe("音箱退出睡眠模式");
                        ((ManticApplication) mContext.getApplicationContext()).setSleepTimeOn(false);
                        resetSleepTime(mDataFactory);
                    }

                    @Override
                    public void onFailed(HttpStatus code) {
                        ToastUtils.showShortSafe("音箱退出睡眠模式失败");
                    }

                    @Override
                    public void onError(IoTException error) {
                        ToastUtils.showShortSafe("音箱退出睡眠模式失败");
                    }
                }, mContext, true);
            }

        }

    }


    /**
     * 只当当前睡眠模式为播放完当前歌曲清除睡眠设置,但是不下发命令
     *
     * @param mContext
     * @param mDataFactory
     */
    public static void onlyClearSleepTimeSetByCurrent(final Context mContext, final DataFactory mDataFactory) {
        if (((ManticApplication) mContext.getApplicationContext()).isSleepTimeOn()) {
            String sleepTime = getSleepTime(mDataFactory);
            if (mContext.getString(R.string.after_current_audio).equals(sleepTime)) {
//                ToastUtils.showShortSafe("音箱退出睡眠模式");
                ((ManticApplication) mContext.getApplicationContext()).setSleepTimeOn(false);
                resetSleepTime(mDataFactory);
            }

        }
    }

    /**
     * 增加睡眠设置
     *
     * @param mContext
     * @param mDataFactory
     */
    public static void addSleepTimeSet(final String sleepTime, final Context mContext, final DataFactory mDataFactory, final AudioSleepAdapter.SleepSetResultListener listener) {
        if (!TextUtils.isEmpty(sleepTime)) {
            if (mContext.getString(R.string.not_open).equals(sleepTime)) {
                clearSleepTimeSet(mContext, mDataFactory, listener);
            } else {
                SleepInfo.sendSleepTime(sleepTime, new Channel.ChannelDeviceControlListenerCallBack() {
                    @Override
                    public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                        listener.success();
                        ((ManticApplication) mContext.getApplicationContext()).setSleepTimeOn(true);
                        mDataFactory.notifyResetTimeChange(SleepTimeSetUtils.getTime(mDataFactory));
                        ToastUtils.showShortSafe(sleepTime + ",音箱将会进入睡眠模式");

                    }

                    @Override
                    public void onFailed(HttpStatus code) {
                        listener.fail();
                        ToastUtils.showShortSafe("设置睡眠时间失败");
                    }

                    @Override
                    public void onError(IoTException error) {
                        listener.onError();
                        ToastUtils.showShortSafe("设置睡眠时间失败");
                    }
                }, mContext, false);
            }
        }
    }


    public static String getSleepTime(DataFactory mDataFactory) {
        String sleepTime = "";
        for (int i = 0; i < mDataFactory.getSleepInfoList().size(); i++) {
            if (mDataFactory.getSleepInfoList().get(i).isSetting()) {
                sleepTime = mDataFactory.getSleepInfoList().get(i).getSleepTime();
                break;
            }
        }
        return sleepTime;
    }

    public static int getSleepPosition(DataFactory mDataFactory) {
        int sleepPosition = 0;
        for (int i = 0; i < mDataFactory.getSleepInfoList().size(); i++) {
            if (mDataFactory.getSleepInfoList().get(i).isSetting()) {
                sleepPosition = i;
                break;
            }
        }
        return sleepPosition;
    }

    public static int getTime(DataFactory mDataFactory) {
        int time = 0;
        for (int i = 0; i < mDataFactory.getSleepInfoList().size(); i++) {
            if (mDataFactory.getSleepInfoList().get(i).isSetting()) {
                time = mDataFactory.getSleepInfoList().get(i).getTime();
                break;
            }
        }
        return time;
    }

    public static void resetSleepTime(DataFactory mDataFactory) {
        List<SleepInfo> sleepInfoList = mDataFactory.getSleepInfoList();
        for (int i = 0; i < sleepInfoList.size(); i++) {
            if (i == 0) {
                sleepInfoList.get(i).setSetting(true);
            } else {
                sleepInfoList.get(i).setSetting(false);
            }
        }
        mDataFactory.setSleepInfoList(sleepInfoList);
        mDataFactory.notifySleepStateOrClearTimeChange(false);
    }
}
