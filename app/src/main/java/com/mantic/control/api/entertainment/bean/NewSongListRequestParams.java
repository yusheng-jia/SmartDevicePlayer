package com.mantic.control.api.entertainment.bean;

/**
 * Created by lin on 2018/1/22.
 */

public class NewSongListRequestParams {
    private String uri;
    private int page;
    private int pagesize;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }
}
