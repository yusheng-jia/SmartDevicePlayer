package com.mantic.control.api.mylike.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeDeltePrams {

    private String uri_scheme;
    private List<String> track_uris;

    public String getUri_scheme() {
        return uri_scheme;
    }

    public void setUri_scheme(String uri_scheme) {
        this.uri_scheme = uri_scheme;
    }

    public List<String> getTrack_uris() {
        return track_uris;
    }

    public void setTrack_uris(List<String> track_uris) {
        this.track_uris = track_uris;
    }
}
