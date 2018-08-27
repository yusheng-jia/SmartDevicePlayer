package com.mantic.control.api.channelplay.bean;


import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class InsertParams {

    private int at_position;
    private List<AddTrack> tracks;


    public int getAt_position() {
        return at_position;
    }

    public void setAt_position(int at_position) {
        this.at_position = at_position;
    }

    public List<AddTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<AddTrack> tracks) {
        this.tracks = tracks;
    }
}
