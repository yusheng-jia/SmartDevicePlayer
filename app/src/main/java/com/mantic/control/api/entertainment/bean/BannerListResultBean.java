package com.mantic.control.api.entertainment.bean;

import java.util.List;

/**
 * Created by lin on 2018/2/1.
 */

public class BannerListResultBean {
    private String jsonrpc;
    private int id;
    private List<BannerListBean> result;


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

    public List<BannerListBean> getResult() {
        return result;
    }

    public void setResult(List<BannerListBean> result) {
        this.result = result;
    }
}
