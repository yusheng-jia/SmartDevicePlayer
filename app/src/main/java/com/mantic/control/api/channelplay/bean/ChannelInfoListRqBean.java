package com.mantic.control.api.channelplay.bean;

/**
 * Created by lin on 2017/7/8.
 */

public class ChannelInfoListRqBean {

    private String method;
    private String jsonrpc;
    private int id;
    private ChannelInfoListRqParam params;


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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ChannelInfoListRqParam getParams() {
        return params;
    }

    public void setParams(ChannelInfoListRqParam params) {
        this.params = params;
    }
}
