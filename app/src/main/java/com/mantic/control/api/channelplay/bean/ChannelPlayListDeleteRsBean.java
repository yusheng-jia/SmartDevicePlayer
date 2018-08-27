package com.mantic.control.api.channelplay.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/23.
 */

public class ChannelPlayListDeleteRsBean {
    private String jsonrpc;
    private int id;
    private List<DeleteResult> result;

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

    public List<DeleteResult> getResult() {
        return result;
    }

    public void setResult(List<DeleteResult> result) {
        this.result = result;
    }
}
