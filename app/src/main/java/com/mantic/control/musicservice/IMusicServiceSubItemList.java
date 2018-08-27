package com.mantic.control.musicservice;

import java.util.ArrayList;

/**
 * Created by root on 17-4-21.
 */
public interface IMusicServiceSubItemList {
    public void createMusicServiceSubItemList(ArrayList<IMusicServiceSubItem> items);
    public void onEmpty();
    public void onError(int code, String message);
}
