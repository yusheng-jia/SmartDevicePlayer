package com.mantic.control.entiy;

/**
 * Created by wujiangxia on 2017/5/3.
 */
public class ManticDeviceInfo {

    private String deviceName;
    private String oldVersion;
    private String newVersion;
    private String updateinfo;
    private String uuid;
    private String bindToken;
    private boolean isOnLine = false;
    private boolean isBind = false;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getOldVersion() {
        return oldVersion;
    }

    public void setOldVersion(String oldVersion) {
        this.oldVersion = oldVersion;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public String getUpdateinfo() {
        return updateinfo;
    }

    public void setUpdateinfo(String updateinfo) {
        this.updateinfo = updateinfo;
    }

    public boolean isOnLine() {
        return isOnLine;
    }

    public void setOnLine(boolean onLine) {
        isOnLine = onLine;
    }

    public boolean isBind() {
        return isBind;
    }

    public void setBind(boolean bind) {
        isBind = bind;
    }
    public String getUuid() {
        return uuid;
    }

    public String getBindToken() {
        return bindToken;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setBindToken(String bindToken) {
        this.bindToken = bindToken;
    }
}
