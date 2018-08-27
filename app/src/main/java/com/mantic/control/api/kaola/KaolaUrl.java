package com.mantic.control.api.kaola;

import com.mantic.control.api.Url;

/**
 * Created by Jia on 2017/5/16.
 */

public interface KaolaUrl {
    public String BASE_URL= Url.OTHER_URL+"mantic/kaola/api/";
    //类别
    public String CATEGORY_LIST="category/list";
    public String CHILD_LIST="category/child/list";
    public String GETALBUM_BYID="album/list";
    public String GETALBUMDETAIL_BYALBUMID="album/detail";
    public String GETAUDIO_BYALBUMID="audio/albumId";
    public String GETAUDIODETAIL_BYAUDIOID="audio/detail";
    //电台
    public String GETCATEGORY_RADIO="category/radio/classification";
    public String GETRADIOLIST_BYID="typeradio/list";
    public String GETRADIODETAIL_BYRADIOID="radio/detail";
    public String GETRADIOLIST_BYRADIOID="radio/list";
    //广播
    public String GETCATEGORY_FM="category/broadcast";
    public String GETCATEGORY_FMAREALIST="broadcast/arealist";
    public String GETFMDETAIL_BYID="broadcast/detail";
    public String GETFMPROGRAMLIST_BYID="broadcast/programlist";
    public String GETFMPROGRAMDETAIL_BYPROGRAMID="program/detail";
    public String GETCURRENTPROGRAM_BYID="broadcast/currentprogram";
}
