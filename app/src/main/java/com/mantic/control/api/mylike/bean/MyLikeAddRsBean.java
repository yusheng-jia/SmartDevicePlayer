package com.mantic.control.api.mylike.bean;

/**
 * Created by lin on 2017/6/26.
 */

public class MyLikeAddRsBean {
    private String jsonrpc;
    private int id;
    private long last_modified;
    private int mantic_num_tracks;
    private MyLikeAddResult result;


    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(long last_modified) {
        this.last_modified = last_modified;
    }

    public int getMantic_num_tracks() {
        return mantic_num_tracks;
    }

    public void setMantic_num_tracks(int mantic_num_tracks) {
        this.mantic_num_tracks = mantic_num_tracks;
    }

    public MyLikeAddResult getResult() {
        return result;
    }

    public void setResult(MyLikeAddResult result) {
        this.result = result;
    }
}
