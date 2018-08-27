package com.mantic.control.api.mylike.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeGetPrams {

    private String uri_scheme;
    private boolean include_tracks;

    public String getUri_scheme() {
        return uri_scheme;
    }

    public void setUri_scheme(String uri_scheme) {
        this.uri_scheme = uri_scheme;
    }

    public boolean isInclude_tracks() {
        return include_tracks;
    }

    public void setInclude_tracks(boolean include_tracks) {
        this.include_tracks = include_tracks;
    }
}
