package com.mantic.control.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.mantic.control.activity.JdSmartHomeActivity;
import com.mantic.control.entiy.ManticDeviceInfo;
import com.mantic.control.entiy.WifiInformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linbingjie on 2017/5/16.
 */

public class SharePreferenceUtil {

    private static final String SP_NAME = "settings";
    private static final String SP_MANTIC_DEVICE = "mantic_device";
    private static final String SP_NAME_USERINFO = "user_info";
    private static final String SP_NAME_WIFIINFO = "wifi_information";

    private static final String FIRST_NAME = "first";
    private static final String IS_BIND = "is_bind";
    private static final String IS_NETWORK_SET = "is_network_set";
    private static final String IS_NAME_SET = "is_name_set";
    private static final String IS_FM_CHANNEL = "is_fm_channel";
    private static final String NEED_SHOW_MINVOLUME = "need_show_minvolume";
    private static final String SMART_HOME = "smart_home";
    private static final String VOICE_SHOPPING = "voice_shopping";

    private static final String QR_UUID = "qrcode_uuid";//第一次配网扫描的uuid ， 用来区分配网
    private static final String UUID = "device_uuid";
    private static final String TOKEN = "device_token";
    private static final String CURRENT_DEVICE_NAME = "current_device_name";
    private static final String CURRENT_DEVICE_VOLUME = "current_device_volume";
    private static final String CURRENT_DEVICE_WIFI = "current_device_wifi";
    private static final String CURRENT_DEVICE_VERSION = "current_device_version";
    private static final String CURRENT_DEVICE_IP = "current_device_ip";
    private static final String DEVICE_PLAYING_STATE = "device_playing_state";
    private static final String DEVICE_MODE = "device_mode";
    private static final String CUSTOM_NAME = "custom_name";
    private static final String ADVERT_SWITCH = "advert_switch";
    private static final String JD_SKILL_SWITCH = "jd_skill_switch";
    private static final String LC = "device_lc";

    private static final String LOGIN_TIME = "login_time";
    private static final String USER_ID= "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_PHOTO = "user_photo";
    private static final String KEY_AUTO_KEY = "key_auto_key";
    private static ArrayList<String> XbUuidArray = new ArrayList<String>(Arrays.asList("07e1000000003d","07e1000000003c","07e1000000003f","07e1000000004a",
            "07e1000000004b","07e1000000004c","07e1000000004d","07e1000000004e","07e1000000004f","07e1000000005a","07e1000000005b","07e1000000005c","07e1000000005d",
            "07e1000000005e","07e1000000005f","07e10000000040","07e10000000041","07e10000000042","07e10000000043","07e10000000044","07e10000000045","07e10000000046",
            "07e10000000047","07e10000000048","07e10000000049","07e10000000050","07e10000000051","07e10000000052","07e10000000053","07e10000000054","07e10000000055",
            "07e10000000056","07e10000000057","07e10000000058","07e10000000059","07e10000000060","07e10000000061","07e10000000062","07e10000000063","07e10000000064"));

    private static ArrayList<String> LxUuidArray = new ArrayList<String>(Arrays.asList("07e10000000069","07e10000000070","07e10000000071","07e10000000072",
            "07e10000000073","07e10000000074","07e10000000075","07e10000000076","07e10000000077","07e10000000077"));

    /**
     * 获取是否是第一次启动
     * @param context
     * @return
     */
    public static boolean isFirstSetup(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getInt(FIRST_NAME, 0) != 1);
    }

    /**
     * 设置第一次启动
     * @param context
     */
    public static void setStartedFlag(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(FIRST_NAME, 1);
        editor.apply();
    }

    /**
     * 获取是否配网状态
     * @param context
     * @return
     */
    public static boolean IsNetworkSet(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getInt(IS_NETWORK_SET, 0) == 1);
    }

    /**
     * 设置已上网
     * @param context
     */
    public static void setNetwork(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(IS_NETWORK_SET, 1);
        editor.apply();
    }

    /**
     * 获取是否已经绑定
     * @param context
     * @return
     */
    public static boolean IsBind(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getInt(IS_BIND, 0) == 1);
    }

    /**
     * 设置绑定状态
     * @param context
     */
    public static void setBind(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(IS_BIND, 1);
        editor.apply();
    }

    /**
     * 查看绑定状态
     * @param context
     */
    public static int getBind(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getInt(IS_BIND,0);
    }

    /**
     * 获取是否已经显示过最小音量Dialog
     * @param context
     * @return
     */
    public static boolean getMinVolumeDialogShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getBoolean(NEED_SHOW_MINVOLUME, false));
    }

    /**
     * 设置已经显示过最小音量
     * @param context
     */
    public static void setMinVolumeDialogShow(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(NEED_SHOW_MINVOLUME, true);
        editor.apply();
    }

    /**
     * 清除已设置名字
     * @param context
     */
    public static void delNameSet(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(IS_NAME_SET);
        editor.apply();
    }


    /**
     * 获取当前Channel 是否是FM
     * @param context
     * @return
     */
    public static boolean getFmChannel(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getBoolean(IS_FM_CHANNEL,false));
    }

    /**
     * 设置当前Channel 是否是FM
     * @param context
     */
    public static void setFmChannel(Context context, boolean state) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_FM_CHANNEL, state);
        editor.apply();
    }

    /**
     * 获取设备UUID
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        if (null != context) {
            SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
            return sp.getString(UUID, "");
        }
        return "";
    }

    /**
     * 设置设备UUID
     * @param context
     * @param value
     */
    public static void setDeviceId(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(UUID, value);
        editor.apply();
        //保存客户名称 显示不同的引导页
//        if (XbUuidArray.indexOf(value)>=0 || value.contains("0eb9")){
//            setCustomName(context,"xinbeng");
//        }else if (LxUuidArray.indexOf(value)>=0){
//            setCustomName(context,"longxin");
//        } else if(value.contains("02e3")||value.contains("0b06")){ //P屁颠虫
//            setCustomName(context,"pidianchong");
//        }else if(value.contains("07cd00") ||value.contains("099700")||value.contains("07e100")){//yls 的
//            setCustomName(context,"yalanshi");
//        }else {
//            setCustomName(context,"mantic");
//        }

    }

    /**
     * 获取设备UUID
     * @param context
     * @return
     */
    public static String getQrCodeId(Context context) {
        if (null != context) {
            SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
            return sp.getString(QR_UUID, "");
        }
        return "";
    }

    /**
     * 设置设备UUID
     * @param context
     * @param value
     */
    public static void setQrCodeId(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(QR_UUID, value);
        editor.apply();
        //保存客户名称 显示不同的引导页
        if (XbUuidArray.indexOf(value)>=0){
            setCustomName(context,"xinbeng");
        }else if (LxUuidArray.indexOf(value)>=0){
            setCustomName(context,"longxin");
        } else if(value.contains("02e3")||value.contains("0b06")){ //P屁颠虫
            setCustomName(context,"pidianchong");
        }else if(value.contains("07cd00") ||value.contains("099700")||value.contains("07e100")){//yls 的
            setCustomName(context,"yalanshi");
        }else if(value.contains("0eb9")){
            setCustomName(context,"suoai");
        } else {
            setCustomName(context,"mantic");
        }

    }


    /**
     * 清除UUID
     * @param context
     */
    public static void delDeviceId(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(UUID);
        editor.apply();
    }

    /**
     * 获取设备TOKEN
     * @param context
     * @return
     */
    public static String getDeviceToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(TOKEN, "");
    }

    /**
     * 设置设备TOKEN
     * @param context
     * @param value
     */
    public static void setDeviceToken(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(TOKEN, value);
        editor.apply();
    }

    /**
     * 获取设备Lc
     * @param context
     * @return
     */
    public static String getDeviceLc(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(LC, "");
    }

    /**
     * 设置设备Lc
     * @param context
     * @param value
     */
    public static void setDeviceLc(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(LC, value);
        editor.apply();
    }

    /**
     * 获取设备名字
     * @param context
     * @return
     */
    public static String getDeviceName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CURRENT_DEVICE_NAME, "");
    }

    /**
     * 当前设备音量
     * @param context
     * @param value
     */
    public static void setDeviceVolume(Context context,int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(CURRENT_DEVICE_VOLUME, value);
        editor.apply();
    }

    /**
     * 获取当前设备音量
     * @param context
     * @return
     */
    public static int getDeviceVolume(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getInt(CURRENT_DEVICE_VOLUME, 0);
    }

    /**
     * 设置设备名字
     * @param context
     * @param value
     */
    public static void setDeviceName(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CURRENT_DEVICE_NAME, value);
        editor.apply();
    }

    /**
     * 清除设备名字
     * @param context
     */
    public static void delDeviceName(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(CURRENT_DEVICE_NAME);
        editor.apply();
    }

    /**
     * 获取当前设备WIFI
     * @param context
     * @return
     */
    public static String getDeviceWifi(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CURRENT_DEVICE_WIFI, "");
    }

    /**
     * 设置设备WIFI名称
     * @param context
     * @param value
     */
    public static void setDeviceWifi(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CURRENT_DEVICE_WIFI, value);
        editor.apply();
    }


    /**
     * 获取当前客户名称
     * @param context
     * @return
     */
    public static String getCustomName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CUSTOM_NAME, "");
    }

    /**
     * 设置客户名称
     * @param context
     * @param value
     */
    public static void setCustomName(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CUSTOM_NAME, value);
        editor.apply();
    }

    /**
     * 获取广告开关
     * @param context
     * @return
     */
    public static Boolean getAdvertSwitch(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getBoolean(ADVERT_SWITCH, false);
    }

    /**
     * 设置广告开关
     * @param context
     * @param value
     */
    public static void setAdvertSwitch(Context context,Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(ADVERT_SWITCH, value);
        editor.apply();
    }

    /**
     * 获取JD技能开关
     * @param context
     * @return
     */
    public static Boolean getJdSkillSwitch(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getBoolean(JD_SKILL_SWITCH, false);
    }

    /**
     * 设置JD技能开关
     * @param context
     * @param value
     */
    public static void setJdSkillSwitch(Context context,Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(JD_SKILL_SWITCH, value);
        editor.apply();
    }

    /**
     * 获取当前设备版本号
     * @param context
     * @return
     */
    public static String getDeviceVersion(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CURRENT_DEVICE_VERSION, "1.0.0");
    }

    /**
     * 设置设备当前设备版本号
     * @param context
     * @param value
     */
    public static void setDeviceVersion(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CURRENT_DEVICE_VERSION, value);
        editor.apply();
    }

    /**
     * 获取当前设备版本号
     * @param context
     * @return
     */
    public static String getCurrentDeviceIp(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getString(CURRENT_DEVICE_IP, "获取中...");
    }

    /**
     * 设置设备当前设备版本号
     * @param context
     * @param value
     */
    public static void setCurrentDeviceIp(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(CURRENT_DEVICE_IP, value);
        editor.apply();
    }

    /**
     * 设置设备当前设备播放状态
     * @param context
     * @param value
     */
    public static void setDevicePlayState(Context context,int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(DEVICE_PLAYING_STATE, value);
        editor.apply();
    }

    /**
     * 获取当前设备播放状态
     * @param context
     * @return
     */
    public static int getDevicePlayState(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        return sp.getInt(DEVICE_PLAYING_STATE, -1);
    }

    /**
     * 设置设备当前设备模式
     * @param context
     * @param value
     */
    public static void setDeviceMode(Context context,int value) {
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(DEVICE_MODE, value);
        editor.apply();
    }

    /**
     * 获取当前设备模式
     * @param context
     * @return
     */
    public static int getDeviceMode(Context context) {
        if (context == null) {
            return -1;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        if (null != sp) {
            return sp.getInt(DEVICE_MODE, -1);
        }
        return -1;
    }

    /**
     * 设置登录时间秒
     * @param context
     * @param value
     */
    public static void setLoginTime(Context context,long value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(LOGIN_TIME, value);
        editor.apply();
    }

    /**
     * 获取登录时间秒
     * @param context
     * @return
     */
    public static long getLoginTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        return sp.getLong(LOGIN_TIME, -1);
    }

    //设置用户id
    public static void setUserId(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_ID, value);
        editor.apply();
    }

    //获取用户id
    public static String getUserId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        return sp.getString(USER_ID, "");
    }

    // 设置用户名字
    public static void setUserName(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_NAME, value);
        editor.apply();
    }

    // 获取用户名字
    public static String getUserName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        return sp.getString(USER_NAME, "");
    }

    // 设置用户头像
    public static void setUserPhoto(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(USER_PHOTO, value);
        editor.apply();
    }

    // 获取用户头像
    public static String getUserPhoto(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        return sp.getString(USER_PHOTO, "");
    }

    //登录Key
    public static void setKeyAutoKey(Context context,String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_AUTO_KEY, value);
        editor.apply();
    }

    //登录Key
    public static String getKeyAutoKey(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        return sp.getString(KEY_AUTO_KEY, "");
    }

    public static void clearDeviceData(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_MANTIC_DEVICE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearSettingsData(Context context){
        Glog.i("jys","clearSettingsData...");
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearUserData(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_USERINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public static String getSharePreferenceData(Context context, String spName, String name) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(name, "");
    }

    public static void setSharePreferenceData(Context context, String spName, String name, String value) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static void deleteSharePreferenceData(Context context, String spName, String name) {
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(name);
        editor.apply();
    }

    public static boolean saveArray(Context context, String spName ,List<ManticDeviceInfo> list) {
        SharedPreferences sp = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        mEdit1.putInt("Size",list.size());

        for(int i=0;i<list.size();i++) {
            mEdit1.remove("name" + i);
            mEdit1.putString("name" + i, list.get(i).getDeviceName());
            mEdit1.remove("uuid" + i);
            mEdit1.putString("uuid" + i, list.get(i).getUuid());
            mEdit1.remove("bindToken" + i);
            mEdit1.putString("bindToken" + i, list.get(i).getBindToken());
        }
        return mEdit1.commit();
    }

    public static ArrayList<ManticDeviceInfo> loadArray(Context context, String spName) {
        ArrayList<ManticDeviceInfo> list= new ArrayList<ManticDeviceInfo>();
        SharedPreferences mSharedPreference1 = context.getSharedPreferences(spName, context.MODE_PRIVATE);
        int size = mSharedPreference1.getInt("Size", 0);
        for(int i=0;i<size;i++) {
            ManticDeviceInfo manticDeviceInfo = new ManticDeviceInfo();
            manticDeviceInfo.setDeviceName(mSharedPreference1.getString("name" + i, null));
            manticDeviceInfo.setUuid(mSharedPreference1.getString("uuid" + i, null));
            manticDeviceInfo.setBindToken(mSharedPreference1.getString("bindToken" + i, null));
            list.add(manticDeviceInfo);

        }
        return list;
    }

    public static boolean saveWifiArray(Context context,List<WifiInformation> list) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_WIFIINFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        mEdit1.putInt("Size",list.size());

        for(int i=0;i<list.size();i++) {
            mEdit1.remove("name" + i);
            mEdit1.putString("name" + i, list.get(i).getWifiName());
            mEdit1.remove("pwd" + i);
            mEdit1.putString("pwd" + i, list.get(i).getWifiPwd());
        }
        return mEdit1.commit();
    }

    public static ArrayList<WifiInformation> loadWifiArray(Context context) {
        ArrayList<WifiInformation> list= new ArrayList<WifiInformation>();
        SharedPreferences mSharedPreference1 = context.getSharedPreferences(SP_NAME_WIFIINFO, Context.MODE_PRIVATE);
        int size = mSharedPreference1.getInt("Size", 0);
        for(int i=0;i<size;i++) {
            WifiInformation wifiInfo = new WifiInformation();
            wifiInfo.setWifiName(mSharedPreference1.getString("name" + i, null));
            wifiInfo.setWifiPwd(mSharedPreference1.getString("pwd" + i, null));
            list.add(wifiInfo);

        }
        return list;
    }

    public static boolean getSmartHomeOpen(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getBoolean(SMART_HOME,false));
    }
    public static void setSmartHomeOpen(Context context,boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SMART_HOME, value);
        editor.apply();
    }
    public static boolean getVoiceShoppingOpen(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (sp.getBoolean(VOICE_SHOPPING,false));
    }
    public static void setVoiceShoppingOpen(Context context,boolean value){
        SharedPreferences.Editor editor = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(VOICE_SHOPPING, value);
        editor.apply();
    }

}
