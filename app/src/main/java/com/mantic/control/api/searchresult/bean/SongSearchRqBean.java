package com.mantic.control.api.searchresult.bean;

/**
 * Created by lin on 2017/7/7.
 */

public class SongSearchRqBean {

    private String method;
    private SongSearchRqParams params;

    private String jsonrpc;
    private int id;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public SongSearchRqParams getParams() {
        return params;
    }

    public void setParams(SongSearchRqParams params) {
        this.params = params;
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
