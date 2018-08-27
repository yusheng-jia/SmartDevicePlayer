package com.mantic.control.api.mopidy.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jayson on 2017/7/18.
 */

public class MopidyRsTrackBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsTrackBean.Result> results;

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
        @SerializedName("mantic_length") public Long length;
        @SerializedName("mantic_real_url") public String mantic_real_url;
        @SerializedName("mantic_radio_length") public String mantic_radio_length;
        @SerializedName("mantic_artists_name") public List<String> mantic_artists_name;
        @SerializedName("mantic_album_uri") public String mantic_album_uri;

        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", update='" + update + '\'' +
                    ", mantic_length='" + length + '\'' +
                    ", mantic_real_url='" + mantic_real_url + '\'' +
                    ", mantic_radio_length='" + mantic_radio_length + '\'' +
                    ", mantic_artists_name='" + mantic_artists_name + '\'' +
                    '}';
        }
    }
}
