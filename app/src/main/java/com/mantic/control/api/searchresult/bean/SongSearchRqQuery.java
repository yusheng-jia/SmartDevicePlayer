package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class SongSearchRqQuery {

    private List<String> track_name;

    public List<String> getTrack_name() {
        return track_name;
    }

    public void setTrack_name(List<String> track_name) {
        this.track_name = track_name;
    }
}
