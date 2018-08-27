package com.mantic.control.api.netizen.bean;

/**
 * Created by lin on 2017/7/7.
 */

public class NetizenUpLoadRqBean {

    private String method;
    private NetizenUploadRqParams params;

    private String jsonrpc;
    private int id;
    private String device_id;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public NetizenUploadRqParams getParams() {
        return params;
    }

    public void setParams(NetizenUploadRqParams params) {
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }
}
