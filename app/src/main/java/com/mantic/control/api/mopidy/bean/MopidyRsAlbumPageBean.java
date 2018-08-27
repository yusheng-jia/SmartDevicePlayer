package com.mantic.control.api.mopidy.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jayson on 2017/7/17.
 */

public class MopidyRsAlbumPageBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsAlbumPageBean.Result> results;

    @Override
    public String toString() {
        return "MopidyRsAlbumPageBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id=" + id +
                ", results=" + results +
                '}';
    }

    public class Result{
        @SerializedName("__model__") public String model;
        @SerializedName("type") public String type;
        @SerializedName("name") public String name;
        @SerializedName("uri") public String uri;
        @SerializedName("mantic_last_modified") public String update;
        @SerializedName("mantic_image") public String image;
        @SerializedName("mantic_describe") public String description;
        @SerializedName("mantic_artists_name") public List<String> artists_name;
        @SerializedName("mantic_num_tracks") public int count;
        @SerializedName("mantic_play_count") public String play_count;

        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", update='" + update + '\'' +
                    ", image='" + image + '\'' +
                    ", description='" + description + '\'' +
                    ", artists_name=" + artists_name + '\'' +
                    ", mantic_play_count=" + play_count +
                    '}';
        }
    }
}
