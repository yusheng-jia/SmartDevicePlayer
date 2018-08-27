package com.mantic.control.api.mychannel.bean;

/**
 * Created by lin on 2017/6/19.
 */

public class AddParams {
    private String uri_scheme;
    private AddPlayList playlist;

    public String getUri_scheme() {
        return uri_scheme;
    }

    public void setUri_scheme(String uri_scheme) {
        this.uri_scheme = uri_scheme;
    }

    public AddPlayList getPlaylist() {
        return playlist;
    }

    public void setPlaylist(AddPlayList playlist) {
        this.playlist = playlist;
    }



}
