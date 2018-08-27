package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class AlbumSearchRqParams {
    private int page;
    private int pagesize;
    private List<String> uris;
    private AlbumSearchRqQuery query;


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

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public AlbumSearchRqQuery getQuery() {
        return query;
    }

    public void setQuery(AlbumSearchRqQuery query) {
        this.query = query;
    }
}
