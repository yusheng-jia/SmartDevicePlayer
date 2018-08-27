package com.mantic.control.api.mopidy.bean;

/**
 * Created by Jia on 2017/5/22.
 */

public class MopidyRqBrowseBean {
    /**
     * method : core.library.browse
     * jsonrpc : 2.0
     * params : {"uri":"spotify:directory"}
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
        /**
         * uri : spotify:directory
         */

        private String uri;

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
