package com.mantic.control.api.channelplay.bean;

/**
 * Created by lin on 2017/6/23.
 */

public class ChannelPlayListMoveRsBean {
    private String jsonrpc;
    private int id;
    private Object result;

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

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
