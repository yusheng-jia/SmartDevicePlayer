package com.mantic.control.api.entertainment.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/22.
 */

public class PopularSongListRequestBean {
    private String method;
    private String jsonrpc;
    private String device_id;
    private int id;
    private PopularSongListRequestParams params;



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

    public PopularSongListRequestParams getParams() {
        return params;
    }

    public void setParams(PopularSongListRequestParams params) {
        this.params = params;
    }
}
