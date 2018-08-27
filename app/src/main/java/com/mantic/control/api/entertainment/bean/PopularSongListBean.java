package com.mantic.control.api.entertainment.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/22.
 */

public class PopularSongListBean {

    private String __model__;
    private String mantic_album_more;
    private String mantic_last_modified;
    private String uri;
    private String mantic_image;
    private String mantic_describe;
    private String mantic_play_count;
    private int mantic_num_tracks;
    private List<String> mantic_artists_name;
    private String type;
    private String name;


    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getMantic_album_more() {
        return mantic_album_more;
    }

    public void setMantic_album_more(String mantic_album_more) {
        this.mantic_album_more = mantic_album_more;
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

    public String getMantic_describe() {
        return mantic_describe;
    }

    public void setMantic_describe(String mantic_describe) {
        this.mantic_describe = mantic_describe;
    }

    public String getMantic_play_count() {
        return mantic_play_count;
    }

    public void setMantic_play_count(String mantic_play_count) {
        this.mantic_play_count = mantic_play_count;
    }

    public int getMantic_num_tracks() {
        return mantic_num_tracks;
    }

    public void setMantic_num_tracks(int mantic_num_tracks) {
        this.mantic_num_tracks = mantic_num_tracks;
    }

    public List<String> getMantic_artists_name() {
        return mantic_artists_name;
    }

    public void setMantic_artists_name(List<String> mantic_artists_name) {
        this.mantic_artists_name = mantic_artists_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
