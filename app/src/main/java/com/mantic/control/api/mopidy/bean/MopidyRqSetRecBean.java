package com.mantic.control.api.mopidy.bean;

import java.util.List;

/**
 * Created by Jia on 2017/5/22.
 */

public class MopidyRqSetRecBean {
    /**
     * method : core.library.get_images
     * jsonrpc : 2.0
     * params : {"uris":["spotify:track:72Q0FQQo32KJloivv5xge2"]}
     * id : 1
     */

    private String method;
    private String jsonrpc;
    private String device_id;
    private ParamsBean params;
    private int id;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static class ParamsBean {
        private boolean rec_status;

        public boolean isRec_status() {
            return rec_status;
        }

        public void setRec_status(boolean rec_status) {
            this.rec_status = rec_status;
        }
    }
}
