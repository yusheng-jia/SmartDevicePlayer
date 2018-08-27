package com.mantic.control.api.searchresult.bean;

import com.mantic.control.data.Channel;
import com.mantic.control.data.MyChannel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lin on 2017/7/20.
 */

public class SongSearchResultBean implements Serializable{

    private int resultSize;
    private String resultType;
    private Channel channel;
    private List<Channel> channelList;


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

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public List<Channel> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
