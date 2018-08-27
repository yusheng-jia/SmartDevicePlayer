package com.mantic.control.entiy;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/1/3.
 * desc:
 */

public class WifiInformation {
    private String wifiName;
    private String wifiPwd;

    public String getWifiName() {
        return wifiName;
    }

    public String getWifiPwd() {
        return wifiPwd;
    }

    public void setWifiName(String wifiName) {
        this.wifiName = wifiName;
    }

    public void setWifiPwd(String wifiPwd) {
        this.wifiPwd = wifiPwd;
    }

    @Override
    public String toString() {
        return "WifiInformation{" +
                "wifiName='" + wifiName + '\'' +
                ", wifiPwd='" + wifiPwd + '\'' +
                '}';
    }
}
