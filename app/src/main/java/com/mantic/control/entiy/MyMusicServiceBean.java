package com.mantic.control.entiy;

import android.graphics.Bitmap;

import java.io.Serializable;


/**
 * Created by lin on 2017/6/28.
 * 该类功能只是用于缓存本地数据
 */

public class MyMusicServiceBean implements Serializable{
    private String name;
    private String introduction;

    public void setName(String msName){
        this.name = msName;
    }
    public String getName(){
        return this.name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
