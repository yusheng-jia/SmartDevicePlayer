package com.mantic.control.api.account.bean;

/**
 * Created by lin on 2017/12/25.
 */

public class ModifyRsBean {
    private ResponseData data;
    private String retcode;
    private String service;

    public ResponseData getData() {
        return data;
    }

    public void setData(ResponseData data) {
        this.data = data;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public class ResponseData {
        private String access_token;
        private String expires_in;
        private String refresh_token;
        private String error;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public String getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(String expires_in) {
            this.expires_in = expires_in;
        }

        public String getRefresh_token() {
            return refresh_token;
        }

        public void setRefresh_token(String refresh_token) {
            this.refresh_token = refresh_token;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "ResponseData{" +
                    "access_token='" + access_token + '\'' +
                    ", expires_in='" + expires_in + '\'' +
                    ", refresh_token='" + refresh_token + '\'' +
                    ", error='" + error + '\'' +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "RegisterRsBean{" +
                "data=" + data +
                ", retcode='" + retcode + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
