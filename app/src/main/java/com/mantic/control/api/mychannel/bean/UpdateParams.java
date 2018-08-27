package com.mantic.control.api.mychannel.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class UpdateParams {
    private String uri;
    private List<AddTrack> tracks;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<AddTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<AddTrack> tracks) {
        this.tracks = tracks;
    }
}
