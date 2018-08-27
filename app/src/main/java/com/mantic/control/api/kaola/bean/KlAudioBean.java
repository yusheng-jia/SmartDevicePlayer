package com.mantic.control.api.kaola.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Jia on 2017/5/19.
 */

public class KlAudioBean {
    @SerializedName("result") public Result result;
    @SerializedName("serverTime") public String serverTime;
    @SerializedName("requestId") public String requestId;

    @Override
    public String toString() {
        return "KlAudioBean{" +
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
        @SerializedName("dataList") public List<Audio> dataList;

        @Override
        public String toString() {
            return "Result{" +
                    "haveNext=" + haveNext +
                    ", nextPage=" + nextPage +
                    ", currentPage=" + currentPage +
                    ", count=" + count +
                    ", sumPage=" + sumPage +
                    ", pageSize=" + pageSize +
                    ", dataList=" + dataList +
                    '}';
        }
    }


    public class AudioDetail{
        @SerializedName("result") public Audio audio;
        @SerializedName("serverTime") public String serverTime;
        @SerializedName("requestId") public String requestId;

        @Override
        public String toString() {
            return "AudioDetail{" +
                    "audio=" + audio +
                    ", serverTime='" + serverTime + '\'' +
                    ", requestId='" + requestId + '\'' +
                    '}';
        }
    }

    public class Audio{
        @SerializedName("audioId") public String audioId;
        @SerializedName("audioName") public String andioName;
        @SerializedName("audioPic") public String audioPic;
        @SerializedName("audioDes") public String audioDes;
        @SerializedName("mp3PlayUrl32") public String playUrl;
        @SerializedName("mp3FileSize32") public Long size;
        @SerializedName("updateTime") public Long updateTime;
        @SerializedName("duration") public Long duration;

        @Override
        public String toString() {
            return "Audio{" +
                    "audioId='" + audioId + '\'' +
                    ", andioName='" + andioName + '\'' +
                    ", audioPic='" + audioPic + '\'' +
                    ", audioDes='" + audioDes + '\'' +
                    ", playUrl='" + playUrl + '\'' +
                    ", size=" + size +
                    ", updateTime=" + updateTime +
                    ", duration=" + duration +
                    '}';
        }
    }

}
