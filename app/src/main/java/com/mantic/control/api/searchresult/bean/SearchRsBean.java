package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class SearchRsBean {
    private String jsonrpc;
    private int id;

    private List<SearchResult> result;

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


    public List<SearchResult> getResult() {
        return result;
    }

    public void setResult(List<SearchResult> result) {
        this.result = result;
    }
}
