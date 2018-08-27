package com.mantic.control.api.mychannel.bean;

/**
 * Created by lin on 2017/6/19.
 */

public class SaveParams {
    private SavePlayList playlist;

    private String mantic_image;

    public SavePlayList getPlaylist() {
        return playlist;
    }

    public void setPlaylist(SavePlayList playlist) {
        this.playlist = playlist;
    }

    public String getMantic_image() {
        return mantic_image;
    }

    public void setMantic_image(String mantic_image) {
        this.mantic_image = mantic_image;
    }

}
