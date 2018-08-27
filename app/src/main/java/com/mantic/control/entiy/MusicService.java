package com.mantic.control.entiy;

import android.graphics.Bitmap;

import com.mantic.control.musicservice.MyMusicService;

/**
 * Created by lin on 2018/1/4.
 */

public class MusicService {
    private MyMusicService myMusicService;
    private Bitmap icon;
    private String iconUrl;
    private String name;
    private String introduction;
    private boolean isActive;

    public void setMyMusicService(MyMusicService service){
        this.myMusicService = service;
    }
    public MyMusicService getMyMusicService(){
        return this.myMusicService;
    }
    public String getID(){
        return this.myMusicService.getMusicServiceID();
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setIcon(Bitmap msIcon){
        this.icon = msIcon;
    }
    public Bitmap getIcon(){
        return this.icon;
    }
    public void setName(String msName){
        this.name = msName;
    }
    public String getName(){
        return this.name;
    }
    public void setActive(boolean msActive){
        this.isActive = msActive;
    }
    public boolean getIsActive(){
        return this.isActive;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }
}
