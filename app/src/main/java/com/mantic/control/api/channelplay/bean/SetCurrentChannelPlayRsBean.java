package com.mantic.control.api.channelplay.bean;

/**
 * Created by lin on 2017/6/26.
 */

public class SetCurrentChannelPlayRsBean {
    private String jsonrpc;
    private int id;
    private long result;

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

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }
}
