package com.mantic.control.api.mychannel.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/19.
 */

public class SaveResult {
    public String __model__;
    public int mantic_type;
    public String name;
    public String mantic_last_modified;
    public String mantic_album_uri;
    public String uri;
    private List<AddTrack> tracks;
    public String mantic_image;
    public String mantic_describe;
    public int mantic_num_tracks;
    private List<String> mantic_artists_name;
}
