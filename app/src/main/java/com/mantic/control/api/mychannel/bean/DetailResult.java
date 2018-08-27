package com.mantic.control.api.mychannel.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class DetailResult {
    public String __model__;
    public String mantic_device_id;
    public int mantic_type;
    public String name;
    public String mantic_last_modified;
    public String mantic_album_uri;
    public String mantic_real_url;
    public String mantic_image;
    public String mantic_describe;
    public int mantic_num_tracks;
    public long last_modified;
    public String uri;
    public List<AddTrack> tracks;
    public List<String> mantic_artists_name;
}
