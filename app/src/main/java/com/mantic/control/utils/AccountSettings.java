package com.mantic.control.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.mantic.control.entiy.AccountInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存用户的相关信息
 */
public class AccountSettings {
    private static final String PREF_NAME = "account_info";

    // 账号相关
    public static final String KEY_HEAD_URL = "key_head_url";
    public static final String KEY_NICK_NAME = "key_nick_name";
    private static final String KEY_BIRTHDAY = "key_birthday";
    public static final String KEY_PHONE = "key_phone";
    private static final String KEY_AUTO_KEY = "key_auto_key";
    private static final String KEY_SEX = "key_sex";
    private static final String KEY_LAST_UPDATE_TIME = "key_last_update_time";

    public static final String KEY_OPEN_ID = "openId";
    public static final String KEY_THRID_TYPE = "thrid_type";
    public static final String KEY_DEVICE_ID = "key_device_id";
    public static final String KEY_USER_ID = "key_user_id";


    private static AccountSettings sInstance;
    private SharedPreferences mPrefs;

    private List<SharedPreferences.OnSharedPreferenceChangeListener> mListeners = new ArrayList<>();

    private AccountSettings(Context context) {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized static AccountSettings getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AccountSettings(context);
        }
        return sInstance;
    }

    public void addPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        mPrefs.registerOnSharedPreferenceChangeListener(listener);
        mListeners.add(listener);
    }

    public void removePreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (mListeners.contains(listener)) {
            mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
            mListeners.remove(listener);
        }
    }

    /**
     * 获取最近一次调用的时间
     * 每次用户主动进入app之后会调用此接口刷新session_id (每天仅调用一次)
     */
    public long getLastUpdateTime() {
        return mPrefs.getLong(KEY_LAST_UPDATE_TIME, 0);
    }

    public void setLastUpdateTime(long time) {
        mPrefs.edit().putLong(KEY_LAST_UPDATE_TIME, time).apply();
    }

    public String getPhoneNumber() {
        return mPrefs.getString(KEY_PHONE, null);
    }

    public void setPhoneNumber(String phone) {
        mPrefs.edit().putString(KEY_PHONE, phone != null ? phone : "").apply();
    }


    public String getHeadUrl() {
        return mPrefs.getString(KEY_HEAD_URL, null);
    }

    public void setHeadUrl(String headUrl) {
        mPrefs.edit().putString(KEY_HEAD_URL, headUrl != null ? headUrl : "").apply();
    }

    public void setUserId(String userId) {
        mPrefs.edit().putString(KEY_USER_ID, userId != null ? userId : "").apply();
    }

    public String getUserId() {
        return mPrefs.getString(KEY_USER_ID, null);
    }

    public  String getAutoKey() {
        return mPrefs.getString(KEY_AUTO_KEY, null);
    }

    public void setAutoKey(String autoKey) {
        mPrefs.edit().putString(KEY_AUTO_KEY, autoKey != null ? autoKey : "").apply();

    }

    public String getNickName() {
        return mPrefs.getString(KEY_NICK_NAME, null);
    }

    public void setNickName(String nickName) {
        mPrefs.edit().putString(KEY_NICK_NAME, nickName != null ? nickName : "").apply();
    }

    public int getSex() {
        return mPrefs.getInt(KEY_SEX, AccountConstant.Sex.MALE.ordinal());
    }

    public void setSex(int sex) {
        mPrefs.edit().putInt(KEY_SEX, sex).apply();
    }

    public String getDeviceId() {
        return mPrefs.getString(KEY_DEVICE_ID, null);
    }

    public void setDeviceId(String deviceId) {
        mPrefs.edit().putString(KEY_DEVICE_ID, deviceId != null ? deviceId : "").apply();
    }

    public String getBirthday() {
        return mPrefs.getString(KEY_BIRTHDAY, null);
    }

    public void setBirthday(String birthday) {
        mPrefs.edit().putString(KEY_BIRTHDAY, birthday != null ? birthday : "").apply();
    }

    public String getOpenId() {
        return mPrefs.getString(KEY_OPEN_ID, null);
    }

    public void setOpenId(String openId) {
        mPrefs.edit().putString(KEY_OPEN_ID, openId != null ? openId : "").apply();
    }

    public String getThridType() {
        return mPrefs.getString(KEY_THRID_TYPE, null);
    }

    public void setThridType(String thridtype) {
        mPrefs.edit().putString(KEY_THRID_TYPE, thridtype != null ? thridtype : "").apply();
    }


    public AccountInfo getAccountInfo() {
        AccountInfo info = new AccountInfo();
        info.setPhoneNumber(getPhoneNumber());
        info.setAutoKey(getAutoKey());
        info.setHeadUrl(getHeadUrl());
        info.setBirthday(getBirthday());
        info.setSex(getSex());
        info.setNickName(getNickName());
        info.setUserId(getUserId());
        return info;
    }

    public void setAccountInfo(AccountInfo info) {
        if (info != null) {
            setPhoneNumber(info.getPhoneNumber());
            setAutoKey(info.getAutoKey());
            setHeadUrl(info.getHeadUrl());
            setBirthday(info.getBirthday());
            setSex(info.getSex());
            setNickName(info.getNickName());
            setUserId(info.getUserId());
            setDeviceId(info.getDeviceId());
        }
    }

    public void clearAccountInfo() {
        setAutoKey("");
        setLastUpdateTime(0);
        setHeadUrl("");
        setNickName("");
        setPhoneNumber("");
        setHeadUrl("");
        setBirthday("");
        setUserId("");
        setDeviceId("");
    }

    public void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

}