package com.mantic.control.api.mylike.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeAddPrams {
    private String uri_scheme;
    private List<Track> tracks;


    public String getUri_scheme() {
        return uri_scheme;
    }

    public void setUri_scheme(String uri_scheme) {
        this.uri_scheme = uri_scheme;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
