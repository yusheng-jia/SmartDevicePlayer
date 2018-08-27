package com.mantic.control.api.mopidy.bean;

import java.util.List;

/**
 * Created by Jia on 2017/5/22.
 */

public class MopidyRqImageBean {
    /**
     * method : core.library.get_images
     * jsonrpc : 2.0
     * params : {"uris":["spotify:track:72Q0FQQo32KJloivv5xge2"]}
     * id : 1
     */

    private String method;
    private String jsonrpc;
    private ParamsBean params;
    private int id;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class ParamsBean {
        private List<String> uris;

        public List<String> getUris() {
            return uris;
        }

        public void setUris(List<String> uris) {
            this.uris = uris;
        }
    }
}
