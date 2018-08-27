package com.mantic.control.api.channelplay.bean;


import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class UpdateParams {
    private List<UpdateTrack> tl_tracks;

    public List<UpdateTrack> getTl_tracks() {
        return tl_tracks;
    }

    public void setTl_tracks(List<UpdateTrack> tl_tracks) {
        this.tl_tracks = tl_tracks;
    }
}
