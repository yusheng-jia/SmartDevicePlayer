package com.mantic.control.listener;


/**
 * Created by Jia on 2017/6/8.
 */

public class Device {
    private String uuid;
    private String bindToken;
    private String deviceName;
    private int status;
    private String osVersion;
    private String projectName;
    private String seriesName;
    private String type;

    private int id;


    public String getUuid() {
        return uuid;
    }

    public String getBindToken() {
        return bindToken;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getStatus() {
        return status;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public String getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setBindToken(String bindToken) {
        this.bindToken = bindToken;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(int id) {
        this.id = id;
    }
}
