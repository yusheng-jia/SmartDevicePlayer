package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class AuthorSearchRqQuery {

    private List<String> artist;

    public List<String> getArtist() {
        return artist;
    }

    public void setArtist(List<String> artist) {
        this.artist = artist;
    }
}
