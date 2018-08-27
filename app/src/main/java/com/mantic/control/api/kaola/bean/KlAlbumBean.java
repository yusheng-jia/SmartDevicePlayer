package com.mantic.control.api.kaola.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jia on 2017/5/19.
 */

public class KlAlbumBean {
    @SerializedName("result") public Result result;
    @SerializedName("serverTime") public String serverTime;
    @SerializedName("requestId") public String requestId;

    @Override
    public String toString() {
        return "KlAlbumBean{" +
                "result=" + result +
                ", serverTime='" + serverTime + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }

    public class Result{
        @SerializedName("haveNext") public int haveNext;
        @SerializedName("nextPage") public int nextPage;
        @SerializedName("currentPage") public int currentPage;
        @SerializedName("count") public int count;
        @SerializedName("sumPage") public int sumPage;
        @SerializedName("pageSize") public int pageSize;
        @SerializedName("dataList") public List<Album> dataList;
        @SerializedName("serverTime") public String serverTime;
        @SerializedName("requestId") public String requestId;

        @Override
        public String toString() {
            return "KlAlbumBean{" +
                    "haveNext=" + haveNext +
                    ", nextPage=" + nextPage +
                    ", currentPage=" + currentPage +
                    ", count=" + count +
                    ", sumPage=" + sumPage +
                    ", pageSize=" + pageSize +
                    ", dataList=" + dataList +
                    ", serverTime='" + serverTime + '\'' +
                    ", requestId='" + requestId + '\'' +
                    '}';
        }
    }

    public class AlbumDetail{
        @SerializedName("id") public Long id;
        @SerializedName("name") public String name;
        @SerializedName("img") public String img;
        @SerializedName("listenNum") public Long listenNum;
        @SerializedName("desc") public String desc;

        @Override
        public String toString() {
            return "AlbumDetail{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", img='" + img + '\'' +
                    ", listenNum=" + listenNum +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }

    public class Album{
        @SerializedName("id") public Long id;
        @SerializedName("name") public String name;
        @SerializedName("img") public String img;
        @SerializedName("listenNum") public Long listenNum;
        @SerializedName("source") public int source;
        @SerializedName("sourceName") public String sourceName;

        @Override
        public String toString() {
            return "Album{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", img='" + img + '\'' +
                    ", listenNum=" + listenNum +
                    ", source=" + source +
                    ", sourceName='" + sourceName + '\'' +
                    '}';
        }
    }
}
