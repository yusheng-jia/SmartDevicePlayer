package com.mantic.control.api.mylike.bean;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeGetRsBean {

    private String jsonrpc;
    private int id;

    private MyLikeAddResult result;

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

    public MyLikeAddResult getResult() {
        return result;
    }

    public void setResult(MyLikeAddResult result) {
        this.result = result;
    }
}
