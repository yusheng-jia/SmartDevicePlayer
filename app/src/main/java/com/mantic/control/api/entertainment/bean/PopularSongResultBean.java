package com.mantic.control.api.entertainment.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/22.
 */

public class PopularSongResultBean {

    private String jsonrpc;
    private int id;
    private List<PopularSongListBean> result;

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

    public List<PopularSongListBean> getResult() {
        return result;
    }

    public void setResult(List<PopularSongListBean> result) {
        this.result = result;
    }
}
