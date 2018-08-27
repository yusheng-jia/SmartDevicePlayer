package com.mantic.control.entiy;

import android.content.Context;


import com.mantic.control.utils.Glog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 更新用户信息接口
 *
 */
public class AccountInfo {

    private String phoneNumber;
    //autoKey
    private String autoKey;
    private String headUrl;
    private int sex;
    //设备id
    private String deviceId;
    private String nickName;
    private String birthday;
    private String userId;

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAutoKey() {
        return autoKey;
    }

    public void setAutoKey(String autoKey) {
        this.autoKey = autoKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static AccountInfo parseFrom(String bean, Context context) {
        AccountInfo accountInfo = new AccountInfo();
        try {
            JSONObject jsonObject = new JSONObject(bean);

            Glog.d("wujx", "response ->parseFrom: " +jsonObject +"autoKey:"+jsonObject.optString("autoKey"));
            if (jsonObject.optString("err_code").equals("200"))  {
                JSONObject jsonUserVo= jsonObject.getJSONObject("userVo");
                accountInfo.setPhoneNumber(jsonUserVo.optString("phone"));
                accountInfo.setHeadUrl(jsonUserVo.optString("icon"));
                accountInfo.setSex(jsonUserVo.optInt("sex"));
                accountInfo.setNickName(jsonUserVo.optString("nickname"));
                accountInfo.setUserId(jsonUserVo.optString("userid"));
                accountInfo.setAutoKey(jsonObject.optString("autoKey"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Glog.d("wujx", "response ->accountInfo: " +accountInfo.toString());


        return accountInfo;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }





    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }


    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccountInfo:\n\t");
        sb.append("phoneNumber=").append(phoneNumber).append("\n\t");
        sb.append("autokey=").append(autoKey).append("\n\t");
        sb.append("headUrl=").append(headUrl).append("\n\t");
        sb.append("sex=").append(sex).append("\n\t");
        sb.append("deviceId=").append(deviceId).append("\n\t");
        sb.append("nickName=").append(nickName);

        return sb.toString();
    }
}