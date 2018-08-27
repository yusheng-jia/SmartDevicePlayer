package com.mantic.control.api.channelplay.bean;


import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class  AddParams {

    private int play_position;
    private List<AddTrack> tracks;


    public int getPlay_position() {
        return play_position;
    }

    public void setPlay_position(int play_position) {
        this.play_position = play_position;
    }

    public List<AddTrack> getTracks() {
        return tracks;
    }

    public void setTracks(List<AddTrack> tracks) {
        this.tracks = tracks;
    }
}
