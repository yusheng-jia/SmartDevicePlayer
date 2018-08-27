package com.mantic.control.api.channelplay.bean;

import java.util.List;

/**
 * Created by lin on 2017/6/23.
 */

public class ChannelPlayListRsBean {
    private String jsonrpc;
    private int id;
    private List<Result> result;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public class Result {
        public String __model__;
        public int tlid;
        public Track track;
        public boolean is_playing;

        @Override
        public String toString() {
            return "Result{" +
                    "__model__='" + __model__ + '\'' +
                    ", tlid=" + tlid +
                    ", track=" + track +
                    ", is_playing=" + is_playing +
                    '}';
        }

        public class Track {
            private String __model__;
            private String name;
            private String mantic_last_modified;
            private String uri;
            private String mantic_image;
            private String mantic_real_url;
            private int track_no;
            private long length;
            private List<String> mantic_artists_name;
            private String mantic_radio_length;
            private String mantic_album_name;
            private String mantic_album_uri;

            public String get__model__() {
                return __model__;
            }

            public void set__model__(String __model__) {
                this.__model__ = __model__;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getMantic_last_modified() {
                return mantic_last_modified;
            }

            public void setMantic_last_modified(String mantic_last_modified) {
                this.mantic_last_modified = mantic_last_modified;
            }

            public String getUri() {
                return uri;
            }

            public void setUri(String uri) {
                this.uri = uri;
            }

            public String getMantic_image() {
                return mantic_image;
            }

            public void setMantic_image(String mantic_image) {
                this.mantic_image = mantic_image;
            }

            public int getTrack_no() {
                return track_no;
            }

            public void setTrack_no(int track_no) {
                this.track_no = track_no;
            }

            public long getLength() {
                return length;
            }

            public void setLength(long length) {
                this.length = length;
            }

            public List<String> getMantic_artists_name() {
                return mantic_artists_name;
            }

            public void setMantic_artists_name(List<String> mantic_artists_name) {
                this.mantic_artists_name = mantic_artists_name;
            }

            public String getMantic_real_url() {
                return mantic_real_url;
            }

            public void setMantic_real_url(String mantic_real_url) {
                this.mantic_real_url = mantic_real_url;
            }
            public String getMantic_radio_length() {
                return mantic_radio_length;
            }

            public void setMantic_radio_length(String mantic_radio_length) {
                this.mantic_radio_length = mantic_radio_length;
            }

            public String getMantic_album_name() {
                return mantic_album_name;
            }

            public void setMantic_album_name(String mantic_album_name) {
                this.mantic_album_name = mantic_album_name;
            }

            public String getMantic_album_uri() {
                return mantic_album_uri;
            }

            public void setMantic_album_uri(String mantic_album_uri) {
                this.mantic_album_uri = mantic_album_uri;
            }

            @Override
            public String toString() {
                return "Track{" +
                        "__model__='" + __model__ + '\'' +
                        ", name='" + name + '\'' +
                        ", mantic_last_modified='" + mantic_last_modified + '\'' +
                        ", mantic_image='" + mantic_image + '\'' +
                        ", mantic_real_url='" + mantic_real_url + '\'' +
                        ", track_no=" + track_no +
                        ", length=" + length +
                        ", mantic_artists_name=" + mantic_artists_name +
                        ", mantic_radio_length=" + mantic_radio_length +
                        '}';
            }
        }
    }


}
