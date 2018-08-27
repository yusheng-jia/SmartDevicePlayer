package com.mantic.control.api.mopidy.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jayson on 2017/7/18.
 */

public class MopidyRsLiveBrowseBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsLiveBrowseBean.Result> results;

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
        @SerializedName("mantic_image") public String image;
        @SerializedName("mantic_describe") public String describe;

        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", mantic_image='" + image + '\'' +
                    ", mantic_describe='" + describe + '\'' +
                    '}';
        }
    }
}
