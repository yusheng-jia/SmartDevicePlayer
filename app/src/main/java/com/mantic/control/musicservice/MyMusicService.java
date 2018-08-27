package com.mantic.control.musicservice;

import android.os.Bundle;

/**
 * Created by root on 17-4-21.
 */
public interface MyMusicService {
    public static final String MY_MUSIC_SERVICE_ID = "my_music_service_id";

    public static final String NEXT_DATA_TYPE = "next_data_type";
    public static final int TYPE_DATA_LIST = 0;
    public static final int TYPE_DATA_ALBUM = 1;
    public static final int TYPE_DATA_RADIO = 2;

    public static final String NEXT_DATA_ID = "next_data_id";

    public static final String PRE_DATA_TYPE = "pre_data_type";
    public static final int TYPE_FIRST = -1;
    public String getMusicServiceID();
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback);
    public void setIMusicServiceAlbum(IMusicServiceAlbum callback);
    public IMusicServiceAlbum getIMusicServiceAlbum();
    public void exec(Bundle bundle);
    public boolean isRefresh();
    public void RefreshAlbumList();
    public void RefreshTrackList();
}
