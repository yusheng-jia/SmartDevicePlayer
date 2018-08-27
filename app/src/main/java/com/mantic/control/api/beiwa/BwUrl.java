package com.mantic.control.api.beiwa;

import com.mantic.control.api.Url;

/**
 * Created by Jia on 2017/5/18.
 */

public interface BwUrl {
    public String BASE_URL= Url.OTHER_URL+"mantic/beiwa/api/";
    public String CHANNEL_LIST="channelList";
    public String CATEGORY_TAGLIST="categoryTagList";
    public String ALBUMLIST_BYTAGID="albumListByCategoryTagId";
    public String ALBUMDETAIL_BYALBUMID="albumDetailByAlbumId";
    public String WORKS_BYALBUMID="worksListByAlbumId";
    public String WORKSDETAIL_BYWORKSID="worksDetailByWorksId";
}