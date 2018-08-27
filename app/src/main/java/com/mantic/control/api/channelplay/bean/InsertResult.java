package com.mantic.control.api.channelplay.bean;


/**
 * Created by lin on 2017/6/19.
 */

public class InsertResult {
    private String __model__;
    private int tlid;
    private AddTrack track;

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

    public AddTrack getTrack() {
        return track;
    }

    public void setTrack(AddTrack track) {
        this.track = track;
    }
}
