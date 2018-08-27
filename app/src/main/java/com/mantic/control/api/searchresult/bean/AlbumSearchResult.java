package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class AlbumSearchResult {
    private String __model__;
    private String uri;
    private List<SearchRsArtist> albums;


    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<SearchRsArtist> getAlbums() {
        return albums;
    }

    public void setAlbums(List<SearchRsArtist> albums) {
        this.albums = albums;
    }
}
