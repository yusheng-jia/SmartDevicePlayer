package com.mantic.control.api.mylike.bean;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeGetRqBean {

    private String method;
    private MyLikeGetPrams params;
    private String device_id;
    private String jsonrpc;
    private int id;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public MyLikeGetPrams getParams() {
        return params;
    }

    public void setParams(MyLikeGetPrams params) {
        this.params = params;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
