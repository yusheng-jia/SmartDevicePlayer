package com.mantic.control.musicservice;

/**
 * Created by root on 17-4-22.
 */
public interface IMusicServiceTrackContent {
    public static final int DYNAMIC_URL = 0;
    public static final int STATIC_URL = 1;
    public String getCoverUrlSmall();
    public String getCoverUrlMiddle();
    public String getCoverUrlLarge();
    public String getTrackTitle();
    public int getUrlType();
    public String getPlayUrl();
    public String getSinger();
    public long getDuration();
    public long getUpdateAt();
    public String getUri();

    /**
     * 时间段 广播需要
     * @return
     */
    public String getTimePeriods();
    public String getAlbumId();
}
