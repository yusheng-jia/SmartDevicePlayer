package com.mantic.control.api.entertainment.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/22.
 */

public class NewSongResultBean {

    private String jsonrpc;
    private int id;
    private List<NewSongListBean> result;

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

    public List<NewSongListBean> getResult() {
        return result;
    }

    public void setResult(List<NewSongListBean> result) {
        this.result = result;
    }
}
