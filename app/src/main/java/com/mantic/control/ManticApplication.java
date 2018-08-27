package com.mantic.control;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

/*import com.baidu.duersdk.DuerSDK;
import com.baidu.duersdk.DuerSDKFactory;
import com.baidu.duersdk.utils.AppLogger;*/

import com.alibaba.sdk.android.media.MediaService;
import com.alibaba.sdk.android.media.WantuService;
import com.baidu.iot.sdk.model.DeviceResource;
import com.iflytek.cloud.SpeechUtility;
import com.jd.smartcloudmobilesdk.authorize.AuthorizeManager;
import com.jd.smartcloudmobilesdk.init.JDSmartSDK;
import com.mantic.antservice.DeviceManager;
import com.mantic.antservice.util.Utils;
import com.mantic.control.data.jd.JDClass;
import com.mantic.control.data.jd.Md5Util;
import com.mantic.control.data.jd.SpUtils;
import com.mantic.control.utils.Glog;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;

import java.util.Objects;
import java.util.concurrent.Callable;
import com.alibaba.sdk.android.feedback.impl.FeedbackAPI;
import com.alibaba.sdk.android.feedback.impl.IActivityCallback;
import com.alibaba.sdk.android.feedback.util.ErrorCode;
import com.alibaba.sdk.android.feedback.util.FeedbackErrorCallback;

/**
 * Created by wujiangxia on 2017/4/25.
 */
public class ManticApplication extends Application {
    public IWXAPI api;

    private boolean isAreadyAddInterestData = false;//是否添加过兴趣列表
    private boolean isReplacePlayingList = false;
    private boolean isSleepTimeOn = false;
    private boolean resetProgress = true;
    private boolean isChannelStop = false;//是否设置暂停状态
    private boolean isPlaySrcZero = false;//播放列表是否为点播的东方歌曲

    private int channelPlayMode = 0;
    private static ArrayList<DeviceResource.Resource> mResourceList = new ArrayList<DeviceResource.Resource>();

    public static String channelId = "";

    public static WantuService mediaService;

    @Override
    public void onCreate() {
        super.onCreate();
        DeviceManager.getInstance().initAntService(this);

        api = WXAPIFactory.createWXAPI(this, "wxe064aead268b0df2", false);
        api.registerApp("wxe064aead268b0df2");

        Utils.init(this);
        registerActivityLifecycleCallbacks(new MyLifecycleHandler());
        //Bugly
//        CrashReport.initCrashReport(getApplicationContext(), "034d66c42e", false);
       Bugly.init(getApplicationContext(), "034d66c42e", false);
       // Bugly.init(this,"1a1f3c39dc",true);
        //统计start
        TCAgent.init(this);
        TCAgent.setReportUncaughtExceptions(true);
        //统计end

        //阿里云反馈
        /**
         * 添加自定义的error handler
         */
        FeedbackAPI.addErrorCallback(new FeedbackErrorCallback() {
            @Override
            public void onError(Context context, String errorMessage, ErrorCode code) {
                Toast.makeText(context, "ErrMsg is: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
        FeedbackAPI.addLeaveCallback(new Callable() {
            @Override
            public Object call() throws Exception {
                Glog.d("DemoApplication", "custom leave callback");
                return null;
            }
        });

        /**
         * 建议放在此处做初始化
         */
        FeedbackAPI.init(this, "24755001", "8c039e10606971c13dfd383a06be06b9");

        initChannelId("TD_CHANNEL_ID");

        /**
         * 讯飞
         */
        SpeechUtility.createUtility(ManticApplication.this, "appid=" +"5add7a76");

        /* 阿里百川多媒体服务*/
        WantuService.enableHttpDNS(); // 可选。为了避免域名劫持，建议开发者启用HttpDNS
        WantuService.enableLog(); //在调试时，可以打开日志。正式上线前可以关闭

        mediaService = WantuService.getInstance();
        mediaService.asyncInit(this);

        // JDSmartSDK初始化
        JDSmartSDK.getInstance().init(this, JDClass.appKey);
        //每次需要注册下
        String name = Md5Util.md5(JDClass.appKey + "");
        String jdAcessToken = SpUtils.getFromLocal(this, name, "access_token", "");
        if (!Objects.equals(jdAcessToken, "")){
            AuthorizeManager.getInstance().registerAccessToken(jdAcessToken);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
      //  MultiDex.install(this);

       // Beta.installTinker();
    }

    public boolean isAreadyAddInterestData() {
        return isAreadyAddInterestData;
    }

    public void setAreadyAddInterestData(boolean areadyAddInterestData) {
        isAreadyAddInterestData = areadyAddInterestData;
    }

    public boolean isReplacePlayingList() {
        return isReplacePlayingList;
    }

    public void setIsReplacePlayingList(boolean isReplacePlayingList) {
        this.isReplacePlayingList = isReplacePlayingList;
    }

    public void setSleepTimeOn(boolean sleepTimeOn) {
        isSleepTimeOn = sleepTimeOn;
    }

    public boolean isSleepTimeOn() {
        return isSleepTimeOn;
    }

    public void setResetProgress(boolean resetProgress) {
         this.resetProgress = resetProgress;
    }

    public boolean getResetProgress() {
        return resetProgress;
    }

    public boolean isChannelStop() {
        return isChannelStop;
    }

    public void setChannelStop(boolean channelStop) {
        isChannelStop = channelStop;
    }

    public int getChannelPlayMode() {
        return channelPlayMode;
    }

    public void setChannelPlayMode(int channelPlayMode) {
        this.channelPlayMode = channelPlayMode;
    }


    public boolean isPlaySrcZero() {
        return isPlaySrcZero;
    }

    public void setPlaySrcZero(boolean playSrcZero) {
        isPlaySrcZero = playSrcZero;
    }

    public ArrayList<DeviceResource.Resource> getResourceList() {
        return mResourceList;
    }
    public void setResourceList(ArrayList<DeviceResource.Resource> list){
        mResourceList = list;
    }

    public void initChannelId(String key) {
        try {
            PackageManager packageManager = this.getPackageManager();
            if (packageManager != null) {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelId = applicationInfo.metaData.getString(key);
                        Glog.i("jys","ManticApplication.channelId:" + channelId);
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}