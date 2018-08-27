package com.mantic.control.api.mychannel.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class MyChannelPlay {
    private String __model__;
    private String name;
    private String mantic_last_modified;
    private long mantic_num_tracks;
    private String mantic_describe;
    private String mantic_album_uri;
    private int mantic_type;
    private List<String> mantic_artists_name;
    private String mantic_image;
    private long last_modified;
    private String mantic_device_id;
    private String uri;
    private String mantic_play_count;


    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMantic_last_modified() {
        return mantic_last_modified;
    }

    public void setMantic_last_modified(String mantic_last_modified) {
        this.mantic_last_modified = mantic_last_modified;
    }

    public long getMantic_num_tracks() {
        return mantic_num_tracks;
    }

    public void setMantic_num_tracks(long mantic_num_tracks) {
        this.mantic_num_tracks = mantic_num_tracks;
    }

    public String getMantic_describe() {
        return mantic_describe;
    }

    public void setMantic_describe(String mantic_describe) {
        this.mantic_describe = mantic_describe;
    }

    public String getMantic_album_uri() {
        return mantic_album_uri;
    }

    public void setMantic_album_uri(String mantic_album_uri) {
        this.mantic_album_uri = mantic_album_uri;
    }

    public int getMantic_type() {
        return mantic_type;
    }

    public void setMantic_type(int mantic_type) {
        this.mantic_type = mantic_type;
    }

    public List<String> getMantic_artists_name() {
        return mantic_artists_name;
    }

    public void setMantic_artists_name(List<String> mantic_artists_name) {
        this.mantic_artists_name = mantic_artists_name;
    }

    public String getMantic_image() {
        return mantic_image;
    }

    public void setMantic_image(String mantic_image) {
        this.mantic_image = mantic_image;
    }


    public long getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }

    public String getMantic_device_id() {
        return mantic_device_id;
    }

    public void setMantic_device_id(String mantic_device_id) {
        this.mantic_device_id = mantic_device_id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }


    public String getMantic_play_count() {
        return mantic_play_count;
    }

    public void setMantic_play_count(String mantic_play_count) {
        this.mantic_play_count = mantic_play_count;
    }
}
