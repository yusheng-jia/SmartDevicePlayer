package com.mantic.control.api.sound;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/14.
 * desc:
 */
public class MopidyRsSoundModalBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsSoundModalBean.Result> results;

    @Override
    public String toString() {
        return "MopidyRsAlbumPageBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id=" + id +
                ", results=" + results +
                '}';
    }

    public class Result implements Serializable{
        @SerializedName("__model__") public String model;
        @SerializedName("type") public String type;
        @SerializedName("name") public String name;
        @SerializedName("uri") public String uri;
        @SerializedName("mantic_length") public Long length;
        @SerializedName("mantic_real_url") public String mantic_real_url;
        @SerializedName("mantic_describe") public String mantic_describe;
        @SerializedName("mantic_album_name") public String mantic_album_name;
        @SerializedName("mantic_image") public String mantic_image;
        @SerializedName("mantic_album_more") public String mantic_album_more;
        @SerializedName("mantic_podcaster_avater") public String mantic_podcaster_avater;
        @SerializedName("mantic_podcaster_key") public String mantic_podcaster_key;
        @SerializedName("mantic_podcaster_name") public String mantic_podcaster_name;
        @SerializedName("mantic_background_url") public String mantic_background_url;


        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", length=" + length +
                    ", mantic_real_url='" + mantic_real_url + '\'' +
                    ", mantic_describe='" + mantic_describe + '\'' +
                    ", mantic_album_name='" + mantic_album_name + '\'' +
                    ", mantic_image='" + mantic_image + '\'' +
                    ", mantic_album_more='" + mantic_album_more + '\'' +
                    ", mantic_podcaster_avater='" + mantic_podcaster_avater + '\'' +
                    ", mantic_podcaster_key='" + mantic_podcaster_key + '\'' +
                    ", mantic_podcaster_name='" + mantic_podcaster_name + '\'' +
                    ", mantic_background_url='" + mantic_background_url + '\'' +
                    '}';
        }
    }
}
