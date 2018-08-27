package com.mantic.control.api.kaola.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import retrofit2.Call;

/**
 * Created by Jia on 2017/5/19.
 */

public class KlCategoryBean {
    @SerializedName("result") public List<Category> list;
    @SerializedName("serverTime") public String time;
    @SerializedName("requestId") public String requestId;

    @Override
    public String toString() {
        return "KlCategoryBean{" +
                "list=" + list +
                ", time='" + time + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }

    public class Category{
        @SerializedName("cid") public int id;
        @SerializedName("name") public String name;
        @SerializedName("hasSub") public int sub;
        @SerializedName("img") public String img;

        @Override
        public String toString() {
            return "Category{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", sub=" + sub +
                    ", img='" + img + '\'' +
                    '}';
        }
    }
}
