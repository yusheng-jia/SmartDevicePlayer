package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class AuthorSearchRsBean {
    private String jsonrpc;
    private int id;

    private List<AuthorSearchResult> result;

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

    public List<AuthorSearchResult> getResult() {
        return result;
    }

    public void setResult(List<AuthorSearchResult> result) {
        this.result = result;
    }
}
