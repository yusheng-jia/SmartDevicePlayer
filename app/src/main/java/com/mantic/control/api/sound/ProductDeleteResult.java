package com.mantic.control.api.sound;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/28.
 * desc:
 */
public class ProductDeleteResult {
    private int mantic_num_tracks;
    private long last_modified;
    private String __model__;
    private String mantic_device_id;
    private String name;
    private String uri;

    public int getMantic_num_tracks() {
        return mantic_num_tracks;
    }

    public void setMantic_num_tracks(int mantic_num_tracks) {
        this.mantic_num_tracks = mantic_num_tracks;
    }

    public long getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }

    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getMantic_device_id() {
        return mantic_device_id;
    }

    public void setMantic_device_id(String mantic_device_id) {
        this.mantic_device_id = mantic_device_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return "ProductDeleteResult{" +
                "mantic_num_tracks=" + mantic_num_tracks +
                ", last_modified=" + last_modified +
                ", __model__='" + __model__ + '\'' +
                ", mantic_device_id='" + mantic_device_id + '\'' +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                '}';
    }
}
