package com.mantic.control.api.channelplay.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/23.
 */

public class ChannelPlayAddRsBean {
    private int id;
    private String jsonrpc;
    private List<AddResult> result;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public List<AddResult> getResult() {
        return result;
    }

    public void setResult(List<AddResult> result) {
        this.result = result;
    }
}