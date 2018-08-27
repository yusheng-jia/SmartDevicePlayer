package com.mantic.antservice.baidu.control;

import com.baidu.iot.sdk.net.RequestMethod;

/**
 * Created by Jia on 2017/5/3.
 */

public class ControlTaskInfo {
    private String deviceUuid;
    private int taskId;
    private RequestMethod method;
    private String name;
    private String label;

    public ControlTaskInfo(String uuid, int taskId, RequestMethod method, String name, String label) {
        this.deviceUuid = uuid;
        this.taskId = taskId;
        this.method = method;
        this.name = name;
        this.label = label;
    }

    public String getDeviceUuid() {
        return deviceUuid;
    }

    public void setDeviceUuid(String uuid) {
        this.deviceUuid = uuid;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public void setMethod(RequestMethod method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
