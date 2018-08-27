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
public class MopidyRsSoundBgMusicBean {
    @SerializedName("jsonrpc") public String jsonrpc;
    @SerializedName("id") public int id;
    @SerializedName("result")
    public List<MopidyRsSoundBgMusicBean.Result> results;

    @Override
    public String toString() {
        return "MopidyRsSoundBgMusicBean{" +
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
        @SerializedName("mantic_length") public int mantic_length;
        @SerializedName("mantic_real_url") public String mantic_real_url;

        @Override
        public String toString() {
            return "Result{" +
                    "model='" + model + '\'' +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    ", uri='" + uri + '\'' +
                    ", mantic_length=" + mantic_length +
                    ", mantic_real_url='" + mantic_real_url + '\'' +
                    '}';
        }
    }
}
