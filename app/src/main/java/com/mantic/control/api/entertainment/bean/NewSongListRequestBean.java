package com.mantic.control.api.entertainment.bean;

/**
 * Created by lin on 2018/1/22.
 */

public class NewSongListRequestBean {
    private String method;
    private String jsonrpc;
    private String device_id;
    private int id;
    private NewSongListRequestParams params;



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

    public NewSongListRequestParams getParams() {
        return params;
    }

    public void setParams(NewSongListRequestParams params) {
        this.params = params;
    }
}
