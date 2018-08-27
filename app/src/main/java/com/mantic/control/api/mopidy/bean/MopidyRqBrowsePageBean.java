package com.mantic.control.api.mopidy.bean;

/**
 * Created by jayson on 2017/7/17.
 */

public class MopidyRqBrowsePageBean {
    /**
     * method : core.library.browse
     * jsonrpc : 2.0
     * params : {"uri":"spotify:directory"}
     * id : 1
     */

    private String method;
    private String jsonrpc;
    private MopidyRqBrowsePageBean.ParamsBean params;
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

    public MopidyRqBrowsePageBean.ParamsBean getParams() {
        return params;
    }

    public void setParams(MopidyRqBrowsePageBean.ParamsBean params) {
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
        private int page;
        private int pagesize;

        public int getPage() {
            return page;
        }

        public int getPageSize() {
            return pagesize;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public void setPageSize(int pagesize) {
            this.pagesize = pagesize;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
