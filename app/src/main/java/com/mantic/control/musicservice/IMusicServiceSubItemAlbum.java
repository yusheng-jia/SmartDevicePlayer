package com.mantic.control.musicservice;

/**
 * Created by root on 17-4-22.
 */
public interface IMusicServiceSubItemAlbum extends IMusicServiceSubItem {
    public String getCoverUrl();
    public String getAlbumTitle();
    public String getAlbumTags();
    public String getAlbumIntro();
    public long getTotalCount();
    public long getUpdateAt();
    public String getAlbumId();
    public String getMainId();
    public String getSinger();

    /**
     * type 0:音乐 1：电台 2：广播
     * @return
     */
    public int getType();

    /**
     * 订阅总数
     * @return
     */
    public String getPlayCount();
}
