package com.mantic.control.musicservice;

import android.content.Context;
import android.os.Bundle;

/**
 * Created by Jia on 2017/5/16.
 */

public class MiguMusicService implements MyMusicService{
    private Context mContext;

    public MiguMusicService(Context context){
        mContext = context;
    }

    @Override
    public String getMusicServiceID() {
        return "nodata";
    }

    @Override
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback) {

    }

    @Override
    public void setIMusicServiceAlbum(IMusicServiceAlbum callback) {

    }

    @Override
    public IMusicServiceAlbum getIMusicServiceAlbum() {
        return null;
    }

    @Override
    public void exec(Bundle bundle) {

    }

    @Override
    public boolean isRefresh() {
        return false;
    }

    @Override
    public void RefreshAlbumList() {

    }
    @Override
    public void RefreshTrackList() {

    }
}
