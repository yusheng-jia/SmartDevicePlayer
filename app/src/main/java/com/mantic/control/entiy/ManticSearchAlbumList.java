package com.mantic.control.entiy;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by wujiangxia on 2017/5/11.
 */
public class ManticSearchAlbumList {
    @SerializedName("total_page")
    private int totalPage;
    @SerializedName("total_count")
    private int totalCount;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("tag_name")
    private String tagName;
    private List<ManticAlbum> albums;

    public ManticSearchAlbumList() {
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getTagName() {
        return this.tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public List<ManticAlbum> getAlbums() {
        return this.albums;
    }

    public void setAlbums(List<ManticAlbum> albums) {
        this.albums = albums;
    }

    public String toString() {
        return "ManticSearchAlbumList [totalPage=" + this.totalPage + ", totalCount=" + this.totalCount + ", albums=" + this.albums + "]";
    }
}
