package com.mantic.control.api.channelplay.bean;

/**
 * Created by lin on 2017/8/1.
 */

public class ChannelPlayModeSetRqBean {
    private String method;
    private String jsonrpc;
    private String device_id;
    private int id;
    private ChannelPlayModeSetParams params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChannelPlayModeSetParams getParams() {
        return params;
    }

    public void setParams(ChannelPlayModeSetParams params) {
        this.params = params;
    }
}
