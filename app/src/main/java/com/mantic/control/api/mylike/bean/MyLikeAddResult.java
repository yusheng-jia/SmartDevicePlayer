package com.mantic.control.api.mylike.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeAddResult {
    private int mantic_num_tracks;
    private long last_modified;
    private String __model__;
    private String mantic_device_id;
    private String name;
    private String uri;
    private List<Track> tracks;

    public int getMantic_num_tracks() {
        return mantic_num_tracks;
    }

    public void setMantic_num_tracks(int mantic_num_tracks) {
        this.mantic_num_tracks = mantic_num_tracks;
    }

    public long getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }

    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getMantic_device_id() {
        return mantic_device_id;
    }

    public void setMantic_device_id(String mantic_device_id) {
        this.mantic_device_id = mantic_device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }
}
