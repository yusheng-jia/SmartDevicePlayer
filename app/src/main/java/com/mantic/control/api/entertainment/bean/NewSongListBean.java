package com.mantic.control.api.entertainment.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/22.
 */

public class NewSongListBean {

    private String __model__;
    private String mantic_real_url;
    private String name;
    private String mantic_album_name;
    private long mantic_length;
    private String mantic_album_uri;
    private String uri;
    private String mantic_image;
    private List<String> mantic_artists_name;
    private String type;

    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getMantic_real_url() {
        return mantic_real_url;
    }

    public void setMantic_real_url(String mantic_real_url) {
        this.mantic_real_url = mantic_real_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMantic_album_name() {
        return mantic_album_name;
    }

    public void setMantic_album_name(String mantic_album_name) {
        this.mantic_album_name = mantic_album_name;
    }


    public long getMantic_length() {
        return mantic_length;
    }

    public void setMantic_length(long mantic_length) {
        this.mantic_length = mantic_length;
    }

    public String getMantic_album_uri() {
        return mantic_album_uri;
    }

    public void setMantic_album_uri(String mantic_album_uri) {
        this.mantic_album_uri = mantic_album_uri;
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
}
