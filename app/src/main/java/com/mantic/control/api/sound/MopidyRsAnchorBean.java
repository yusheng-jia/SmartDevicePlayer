package com.mantic.control.api.sound;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/15.
 * desc:
 */
public class MopidyRsAnchorBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsAnchorBean.Result> results;

    @Override
    public String toString() {
        return "MopidyRsAlbumPageBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id=" + id +
                ", results=" + results +
                '}';
    }

    public class Result implements Serializable {
        @SerializedName("__model__") public String model;
        @SerializedName("type") public String type;
        @SerializedName("name") public String name;
        @SerializedName("uri") public String uri;
        @SerializedName("mantic_describe") public String mantic_describe;
        @SerializedName("mantic_podcaster_avater") public String mantic_podcaster_avater;
        @SerializedName("mantic_podcaster_key") public String mantic_podcaster_key;


        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", mantic_radio_length='" + mantic_describe + '\'' +
                    ", mantic_podcaster_avater='" + mantic_podcaster_avater + '\'' +
                    ", mantic_podcaster_key='" + mantic_podcaster_key + '\'' +
                    '}';
        }
    }
}
