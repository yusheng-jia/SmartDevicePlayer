package com.mantic.control.api.mylike.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeUpdatePrams {

    private String uri_scheme;
    private MyLikeUpdatePlayList playlist;

    public String getUri_scheme() {
        return uri_scheme;
    }

    public void setUri_scheme(String uri_scheme) {
        this.uri_scheme = uri_scheme;
    }

    public MyLikeUpdatePlayList getPlaylist() {
        return playlist;
    }

    public void setPlaylist(MyLikeUpdatePlayList playlist) {
        this.playlist = playlist;
    }

}
