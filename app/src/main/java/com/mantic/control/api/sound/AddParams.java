package com.mantic.control.api.sound;

import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/25.
 * desc:
 */
public class AddParams {
    private String uri_scheme;
    private List<SoundTrack> tracks;

    public String getUri_scheme() {
        return uri_scheme;
    }

    public List<SoundTrack> getListTracks() {
        return tracks;
    }

    public void setUri_scheme(String uri_scheme) {
        this.uri_scheme = uri_scheme;
    }

    public void setListTracks(List<SoundTrack> listTracks) {
        this.tracks = listTracks;
    }
}
