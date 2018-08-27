package com.mantic.control.api.entertainment.bean;

/**
 * Created by lin on 2018/1/22.
 */

public class BannerListBean {

    private String __model__;
    private String mantic_describe;
    private String type;
    private String uri;
    private String mantic_image;


    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getMantic_describe() {
        return mantic_describe;
    }

    public void setMantic_describe(String mantic_describe) {
        this.mantic_describe = mantic_describe;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
