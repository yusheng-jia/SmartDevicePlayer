package com.mantic.control.api.beiwa.bean;

import java.util.List;

/**
 * Created by Jia on 2017/5/18.
 */

public class BwAlbumBean {
    public int code;
    public String message;
    public AlbumItem data;

    @Override
    public String toString() {
        return "BwAlbumBean{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public class AlbumItem{
        public List<Album> items;
        public Meta meta;

        @Override
        public String toString() {
            return "AlbumItem{" +
                    "items=" + items +
                    ", meta=" + meta +
                    '}';
        }

        public class Album{
            public int id;
            public String album_name;
            public int album_type;
            public String album_description;
            public String img_small;
            public String img_middle;
            public String img_big;

            @Override
            public String toString() {
                return "Album{" +
                        "id=" + id +
                        ", album_name='" + album_name + '\'' +
                        ", album_type=" + album_type +
                        ", album_description='" + album_description + '\'' +
                        ", img_small='" + img_small + '\'' +
                        ", img_middle='" + img_middle + '\'' +
                        ", img_big='" + img_big + '\'' +
                        '}';
            }
        }
        public class Meta{
            public int totalCount;
            public int pageCount;
            public int currentPage;
            public int perPage;

            @Override
            public String toString() {
                return "Meta{" +
                        "totalCount=" + totalCount +
                        ", pageCount=" + pageCount +
                        ", currentPage=" + currentPage +
                        ", perPage=" + perPage +
                        '}';
            }
        }
    }


}
