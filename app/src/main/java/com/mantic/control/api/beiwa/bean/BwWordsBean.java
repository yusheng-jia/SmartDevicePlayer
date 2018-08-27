package com.mantic.control.api.beiwa.bean;

import java.util.List;

/**
 * Created by Jia on 2017/5/19.
 */

public class BwWordsBean {
    public String code;
    public String message;
    public Data data;

    @Override
    public String toString() {
        return "BwWordsBean{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }


    public class Data{
        public List<Items> items;
        public Meta meta;

        @Override
        public String toString() {
            return "Data{" +
                    "items=" + items +
                    ", meta=" + meta +
                    '}';
        }
    }
    public class Items{
        public String id;
        public String name;
        public String description;
        public String img_small;
        public String img_big;
        public ExtInfo ext_info;

        @Override
        public String toString() {
            return "Items{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", img_small='" + img_small + '\'' +
                    ", img_big='" + img_big + '\'' +
                    ", ext_info=" + ext_info +
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
    public class ExtInfo{
        public String singer;
        public int duration;
        public String original_path;
        public Long original_size;

        @Override
        public String toString() {
            return "ExtInfo{" +
                    "singer='" + singer + '\'' +
                    ", duration=" + duration +
                    ", original_path='" + original_path + '\'' +
                    ", original_size=" + original_size +
                    '}';
        }
    }

    public class Word{
        public String code;
        public String message;
        public Items data;

        @Override
        public String toString() {
            return "Word{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }
}
