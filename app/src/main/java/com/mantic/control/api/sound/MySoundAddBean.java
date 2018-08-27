package com.mantic.control.api.sound;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/25.
 * desc:
 */
public class MySoundAddBean {
    private String method;
    private String device_id;
    private String jsonrpc;
    private int id;
    private AddParams params;
    public void setMethod(String method) {
        this.method = method;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParams(AddParams params) {
        this.params = params;
    }



    public String getMethod() {
        return method;
    }

    public String getDevice_id() {
        return device_id;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public int getId() {
        return id;
    }

    public AddParams getParams() {
        return params;
    }




}
