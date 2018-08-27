package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class AlbumSearchRsBean {
    private String jsonrpc;
    private int id;

    private List<AlbumSearchResult> result;

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

    public List<AlbumSearchResult> getResult() {
        return result;
    }

    public void setResult(List<AlbumSearchResult> result) {
        this.result = result;
    }
}
