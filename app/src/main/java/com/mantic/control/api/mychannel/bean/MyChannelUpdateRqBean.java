package com.mantic.control.api.mychannel.bean;

/**
 * Created by lin on 2017/6/2.
 */

public class MyChannelUpdateRqBean {
    private String method;
    private String device_id;
    private String jsonrpc;
    private int id;
    private UpdateParams params;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
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

    public UpdateParams getParams() {
        return params;
    }

    public void setParams(UpdateParams params) {
        this.params = params;
    }
}