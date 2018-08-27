package com.mantic.control.api.sound;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/19.
 * desc:
 */
public class MopidyRsSoundProductBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result") public MopidyRsSoundProductBean.Result result;

    @Override
    public String toString() {
        return "MopidyRsSoundProductBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id=" + id +
                ", result=" + result +
                '}';
    }

    public class Result{
        @SerializedName("__model__") public String model;
        @SerializedName("mantic_device_id") public String  device_id;
        @SerializedName("name") public String name;
        @SerializedName("uri") public String uri;
        @SerializedName("last_modified") public Long last_modified;
        @SerializedName("mantic_num_tracks") public int mantic_num_tracks;
        public List<Result.Tracks> tracks;

        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", device_id='" + device_id + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", last_modified=" + last_modified +
                    ", mantic_num_tracks=" + mantic_num_tracks +
                    ", tracks=" + tracks +
                    '}';
        }


        public class Tracks implements Serializable {
            @SerializedName("__model__") public String model;
            @SerializedName("name") public String name;
            @SerializedName("mantic_album_name") public String mantic_album_name;
            @SerializedName("mantic_describe") public String mantic_describe;
            @SerializedName("mantic_real_url") public String mantic_real_url;
            @SerializedName("uri") public String uri;
            @SerializedName("mantic_image") public String mantic_image;
            @SerializedName("length") public Long length;
            @SerializedName("mantic_podcaster_avater") public String mantic_podcaster_avater;
            @SerializedName("mantic_podcaster_key") public String mantic_podcaster_key;
            @SerializedName("mantic_podcaster_name") public String mantic_podcaster_name;

            @Override
            public String toString() {
                return "Tracks{" +
                        "model='" + model + '\'' +
                        ", name='" + name + '\'' +
                        ", mantic_album_name='" + mantic_album_name + '\'' +
                        ", mantic_describe='" + mantic_describe + '\'' +
                        ", mantic_real_url='" + mantic_real_url + '\'' +
                        ", uri='" + uri + '\'' +
                        ", mantic_image='" + mantic_image + '\'' +
                        ", length=" + length +
                        ", mantic_podcaster_avater='" + mantic_podcaster_avater + '\'' +
                        ", mantic_podcaster_key='" + mantic_podcaster_key + '\'' +
                        ", mantic_podcaster_name='" + mantic_podcaster_name + '\'' +
                        '}';
            }
        }
    }
}
