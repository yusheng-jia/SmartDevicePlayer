package com.mantic.control.api.searchresult.bean;

import com.mantic.control.data.MyChannel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lin on 2017/7/20.
 */

public class RadioSearchResultBean implements Serializable{

    private int resultSize;
    private String resultType;
    private MyChannel myChannel;
    private List<SearchRsRadio> radios;


    public int getResultSize() {
        return resultSize;
    }

    public void setResultSize(int resultSize) {
        this.resultSize = resultSize;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public MyChannel getMyChannel() {
        return myChannel;
    }

    public void setMyChannel(MyChannel myChannel) {
        this.myChannel = myChannel;
    }

    public List<SearchRsRadio> getRadios() {
        return radios;
    }

    public void setRadios(List<SearchRsRadio> radios) {
        this.radios = radios;
    }
}
