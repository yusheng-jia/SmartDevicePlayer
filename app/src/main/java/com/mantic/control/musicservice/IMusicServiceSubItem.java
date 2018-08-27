package com.mantic.control.musicservice;

import android.os.Bundle;

/**
 * Created by root on 17-4-21.
 */
public interface IMusicServiceSubItem {
    public String getItemIconUrl();
    public String getItemText();
    public Bundle gotoNext();
    public int getNextDataType();
    public String getNextDataId();

    /**
     * 0: url 网络图片 1：本地图片
     * @return
     */
    public int getIconType();
}
