package com.mantic.control.api.account.bean;

/**
 * Created by lin on 2017/12/25.
 */

public class RegisterRsBean {
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
        private String mobile;
        private String error;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
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
                    "mobile='" + mobile + '\'' +
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
