package com.mantic.control.api.searchresult.bean;

import java.util.List;

/**
 * Created by lin on 2017/7/7.
 */

public class RadioSearchResult {
    private String __model__;
    private String uri;
    private List<SearchRsRadio> radios;


    public String get__model__() {
        return __model__;
    }

    public void set__model__(String __model__) {
        this.__model__ = __model__;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<SearchRsRadio> getRadios() {
        return radios;
    }

    public void setRadios(List<SearchRsRadio> radios) {
        this.radios = radios;
    }
}
