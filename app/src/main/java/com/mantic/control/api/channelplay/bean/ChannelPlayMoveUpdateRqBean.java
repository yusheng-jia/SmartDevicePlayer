package com.mantic.control.api.channelplay.bean;

/**
 * Created by lin on 2017/6/23.
 */

public class ChannelPlayMoveUpdateRqBean {
    private String method;
    private String device_id;
    private String jsonrpc;
    private UpdateParams params;
    private int id;

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

    public UpdateParams getParams() {
        return params;
    }

    public void setParams(UpdateParams params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
