package com.mantic.control.api.mychannel.bean;

/**
 * Created by lin on 2017/6/2.
 */

public class AlbumDetailRqBean {

    private String method;
    private String jsonrpc;
    private int id;
    private AlbumParams params;

    public String getMethod() {
        return method;
    }

    public void setMethod(String methos) {
        this.method = methos;
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

    public AlbumParams getParams() {
        return params;
    }

    public void setParams(AlbumParams params) {
        this.params = params;
    }
}
