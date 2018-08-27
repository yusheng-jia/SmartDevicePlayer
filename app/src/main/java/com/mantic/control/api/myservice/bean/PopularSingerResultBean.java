package com.mantic.control.api.myservice.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/22.
 */

public class PopularSingerResultBean {
    private String jsonrpc;
    private int id;
    private List<PopularSinger> result;

    public class PopularSinger {
        private String head_image_small;
        private String head_image_big;
        private String uri;
        private String name;

        public String getHead_image_small() {
            return head_image_small;
        }

        public void setHead_image_small(String head_image_small) {
            this.head_image_small = head_image_small;
        }

        public String getHead_image_big() {
            return head_image_big;
        }

        public void setHead_image_big(String head_image_big) {
            this.head_image_big = head_image_big;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }


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

    public List<PopularSinger> getResult() {
        return result;
    }

    public void setResult(List<PopularSinger> result) {
        this.result = result;
    }
}
