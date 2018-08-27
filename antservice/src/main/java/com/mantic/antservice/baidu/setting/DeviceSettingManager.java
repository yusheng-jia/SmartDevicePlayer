package com.mantic.antservice.baidu.setting;

/**
 * Created by Jia on 2017/5/3.
 */

public class DeviceSettingManager {
    private int defaultControlExpireTime = 100;  //default control expire time length
    private int defaultDataTimeInterval = 10;  //default report data time interval
    private int defaultRefreshTaskTime = 1000; //2s
    public final static int SMART_CONFIG_CHECK_ONLINE_NUMBER = 20;
    public final static int BIND_DEVICES_COUND_MAX = 20;
    public final static String APP_KEY_TEST = "zQHVxb6RqwKiFFyEsr7rh9gMjfKFguGV";
    public final static String APP_SEC_KEY_TEST = "LkftBPE3TTGpRG8mw0APmOCrx7WvndKN";
    public final static String APP_KEY = "wySGOmZVthALy6jt7Nfhc6BeCvm9GSaK";
    public final static String APP_SEC_KEY = "8HhTKgXK1UqHvbXyxIQY75jbjLlSGfGF";
    public final static boolean APP_TEST = true;
    public final static boolean DEBUG = true;
    private static DeviceSettingManager mInstance;

    private DeviceSettingManager() {

    }

    public static String getAppKey() {
        if (APP_TEST) {
            return APP_KEY_TEST;
        } else {
            return APP_KEY;
        }
    }

    public static String getAppSecKey(){
        if(APP_TEST){
            return APP_SEC_KEY_TEST;
        }else{
            return APP_SEC_KEY;
        }
    }

    public static DeviceSettingManager getInstance() {
        if (mInstance == null) {
            synchronized (DeviceSettingManager.class) {
                mInstance = new DeviceSettingManager();
            }
        }
        return mInstance;
    }

    /**
     * get control expire time
     * @return
     */
    public int getControlExpireTime() {
        return defaultControlExpireTime;
    }

    public int getDataTimeInterval() {
        return defaultDataTimeInterval;
    }

    public int getDefaultRefreshTaskTime() {
        return defaultRefreshTaskTime;
    }
}
