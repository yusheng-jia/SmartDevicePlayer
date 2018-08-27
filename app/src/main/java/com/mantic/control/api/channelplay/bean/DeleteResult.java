package com.mantic.control.api.channelplay.bean;


import com.mantic.control.api.channelplay.bean.ChannelPlayListRsBean.Result.Track;

/**
 * Created by lin on 2017/6/19.
 */

public class DeleteResult {
    private Track track;
    private String __model__;
    private int tlid;


    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public int getTlid() {
        return tlid;
    }

    public void setTlid(int tlid) {
        this.tlid = tlid;
    }
}
