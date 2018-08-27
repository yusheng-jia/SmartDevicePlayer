package com.mantic.control.api.mychannel.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class AddTrack {
    private String __model__;
    private String name;
    private String mantic_last_modified;
    private String uri;
    private String mantic_image;
    private String mantic_real_url;
    private int track_no;
    private long length;
    private List<String> mantic_artists_name;
    private String mantic_album_name;
    private String mantic_album_uri;

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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMantic_image() {
        return mantic_image;
    }

    public void setMantic_image(String mantic_image) {
        this.mantic_image = mantic_image;
    }

    public String getMantic_real_url() {
        return mantic_real_url;
    }

    public void setMantic_real_url(String mantic_real_url) {
        this.mantic_real_url = mantic_real_url;
    }

    public int getTrack_no() {
        return track_no;
    }

    public void setTrack_no(int track_no) {
        this.track_no = track_no;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public List<String> getMantic_artists_name() {
        return mantic_artists_name;
    }

    public void setMantic_artists_name(List<String> mantic_artists_name) {
        this.mantic_artists_name = mantic_artists_name;
    }

    public String getMantic_album_name() {
        return mantic_album_name;
    }

    public void setMantic_album_name(String mantic_album_name) {
        this.mantic_album_name = mantic_album_name;
    }

    public String getMantic_album_uri() {
        return mantic_album_uri;
    }

    public void setMantic_album_uri(String mantic_album_uri) {
        this.mantic_album_uri = mantic_album_uri;
    }
}
