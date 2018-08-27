package com.mantic.control.musicservice;

import com.mantic.control.data.DataFactory;

import java.util.ArrayList;

/**
 * Created by root on 17-4-22.
 */
public interface IMusicServiceAlbum {
    public void createMusicServiceAlbum(ArrayList<IMusicServiceTrackContent> items);
    public void onError(int code, String message);
}
