package com.mantic.control.api.baidu.bean;

import java.util.List;

/**
 * Created by Jia on 2017/5/18.
 */

public class BaiduTrackList {
    public int status;
    public String code;
    public TrackItem data;
    public String message;

    @Override
    public String toString() {
        return "BaiduTrackList{" +
                "status=" + status +
                ", code='" + code + '\'' + ", data=" + data +
                ", message='" + message + '\'' +
                '}';
    }

    public class TrackItem{
        public List<Track> list;
        public int page;
        public int total_page;

        public class Track{
            public long id;
            public String name;
            public List<String> singer_name;
            public String head_image_url;

            @Override
            public String toString() {
                return "{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", singer_name=" + singer_name +
                        ", head_image_url='" + head_image_url + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "{" +
                    "list=" + list +
                    ", page=" + page +
                    ", total_page=" + total_page +
                    '}';
        }
    }


}
