package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class RadioSearchRsBean {
    private String jsonrpc;
    private int id;

    private List<RadioSearchResult> result;

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

    public List<RadioSearchResult> getResult() {
        return result;
    }

    public void setResult(List<RadioSearchResult> result) {
        this.result = result;
    }
}
