package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class SearchRqParams {
    private int offset;
    private int limit;
    private List<String> uris;
    private SearchRqQuery query;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<String> getUris() {
        return uris;
    }

    public void setUris(List<String> uris) {
        this.uris = uris;
    }

    public SearchRqQuery getQuery() {
        return query;
    }

    public void setQuery(SearchRqQuery query) {
        this.query = query;
    }
}
