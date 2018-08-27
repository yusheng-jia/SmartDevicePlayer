package com.mantic.control.api.mopidy.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jia on 2017/6/9.
 */

public class MopidyRsAlbumMoreBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsAlbumMoreBean.Result> results;

    @Override
    public String toString() {
        return "MopidyRsBrowseBean{" +
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
        @SerializedName("mantic_album_more") public String more;
        @SerializedName("mantic_last_modified") public String update;
        @SerializedName("mantic_image") public String image;
        @SerializedName("mantic_describe") public String description;
        @SerializedName("mantic_artists_name") public List<String> artists_name;
        @SerializedName("mantic_num_tracks") public int count;

        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", more='" + more + '\'' +
                    ", update='" + update + '\'' +
                    ", image='" + image + '\'' +
                    ", description='" + description + '\'' +
                    ", artists_name=" + artists_name +
                    '}';
        }
    }
}
