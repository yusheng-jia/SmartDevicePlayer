package com.mantic.control.api.channelplay.bean;

/**
 * Created by lin on 2017/8/1.
 */

public class ChannelPlayModeGetRsBean {
    private String jsonrpc;
    private int result;
    private int id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
