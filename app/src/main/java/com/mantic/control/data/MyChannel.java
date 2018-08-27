package com.mantic.control.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.mantic.control.R;

import java.util.ArrayList;

/**
 * Created by chenyun on 17-5-27.
 */
//我的频道（添加到我的频道）
public class MyChannel{
    private  final int CHANNEL_TYPE_ALBUM = 0;//专辑
    private  final int CHANNEL_TYPE_CHANNEL = 1;//频道
    private  final int CHANNEL_TYPE_RADIO = 2;//电台
    private String mChannelCoverUrl;
    private Bitmap mChannelCover;
    private String mMusicServiceId;
    private String mChannelId;
    private String mChannelName;
    private String mChannelTags;
    private String mChannelIntro;
    private String mSingerName;
    private String albumId;
    private String mainId;
    private String url;//唯一标识符，用来做删除操作
    private boolean isSelect;//用来做新建频道选择的标记

    private boolean isSelfDefinition = false;
    private int mChannelType;
    private ArrayList<Channel> mChannelList = new ArrayList<>();
    private Bundle mCreateBundle;

    private int mChannelCount = 0;
    private long mTotalCount = 0;

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    private int mediaType;

    public String getPlayCount() {
        return playCount;
    }

    public void setPlayCount(String playCount) {
        this.playCount = playCount;
    }

    private String playCount;

    public void setmUpdateAt(long mUpdateAt) {
        this.mUpdateAt = mUpdateAt;
    }

    public long getmUpdateAt() {
        return mUpdateAt;
    }

    private long mUpdateAt = 0;

    public void setChannelCoverUrl(String url){
        this.mChannelCoverUrl = url;
    }

    public void setChannelCover(Bitmap cover){
        this.mChannelCover = cover;
    }

    public void setMusicServiceId(String musicServiceId){
        this.mMusicServiceId = musicServiceId;
    }

    public void setChannelId(String channelId){
        this.mChannelId = channelId;
    }

    public void setChannelName(String name){
        this.mChannelName = name;
    }

    public void setChannelTags(String tags){
        this.mChannelTags = tags;
    }

    public void setChannelIntro(String intro){
        this.mChannelIntro = intro;
    }

    public void setChannelType(int type){
        this.mChannelType = type;
    }

    public String getChannelCoverUrl(){
        return this.mChannelCoverUrl;
    }

    public Bitmap getChannelCover(){
        return this.mChannelCover;
    }

    public String getMusicServiceId(){
        return this.mMusicServiceId;
    }

    public String getChannelId(){
        return this.mChannelId;
    }

    public String getChannelName(){
        return this.mChannelName;
    }

    public String getChannelTags(){
        return this.mChannelTags;
    }

    public String getChannelIntro(){
        return this.mChannelIntro;
    }

    public void setSingerName(String singerName){
        this.mSingerName = singerName;
    }

    public String getSingerName(){
        return this.mSingerName;
    }


    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getMainId() {
        return mainId;
    }

    public void setMainId(String mainId) {
        this.mainId = mainId;
    }

    public boolean isSelfDefinition() {
        return isSelfDefinition;
    }

    public void setSelfDefinition(boolean selfDefinition) {
        isSelfDefinition = selfDefinition;
    }

    public int getChannelType(){
        return this.mChannelType;
    }

    public int getChannelCount(){
        //return this.mChannelCount;
        int count = this.mChannelCount;
        if(this.mChannelList != null && this.mChannelList.size() > 0){
            count = this.mChannelList.size();
        }
        return count;
    }

    public void setChannelCount(int count){
        this.mChannelCount = count;
    }

    public long getmTotalCount() {
        return mTotalCount;
    }

    public void setmTotalCount(long mTotalCount) {
        this.mTotalCount = mTotalCount;
    }

    public String getChannelDescribe(Context mContext){
        String des = null;
        if(this.mChannelType == CHANNEL_TYPE_ALBUM){
            des = mContext.getString(R.string.music_count,this.getmTotalCount());
        }else if(this.mChannelType == CHANNEL_TYPE_CHANNEL){
            des = mContext.getResources().getString(R.string.channel_type_channel)+"|"+mContext.getString(R.string.music_count,this.getmTotalCount());
        }else if(this.mChannelType == CHANNEL_TYPE_RADIO){
            des = mContext.getResources().getString(R.string.channel_type_radio);
        }
        return des;
    }

    public void addChannel(Channel channel){
        if (null != mChannelList) {
            this.mChannelList.add(channel);
        }

    }

    public void setChannelList(ArrayList<Channel> channelList){
        this.mChannelList = channelList;
    }

    public ArrayList<Channel> getChannelList(){
        return this.mChannelList;
    }

    public Bundle getCreateBundle(){
        return this.mCreateBundle;
    }

    public void setCreateBundle(Bundle bundle){
        this.mCreateBundle = bundle;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}