package com.mantic.control.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mantic.control.data.Channel;
import com.mantic.control.data.MyChannel;
import com.mantic.control.entiy.MyMusicServiceBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2017/6/10.
 */

public class GsonUtil {
    public static String myChannelListToString(ArrayList<MyChannel> myChannels) {
        Gson gson = new Gson();
        String myChannelListStr = gson.toJson(myChannels);
        return myChannelListStr;
    }


    public static ArrayList<MyChannel> stringToMyChannelList(String listData) {
        Glog.i("linbingjie", "listData: " + listData);
        Gson gson = new Gson();
        ArrayList<MyChannel> myChannels = gson.fromJson(listData, new TypeToken<ArrayList<MyChannel>>(){}.getType());
        return myChannels;
    }

    public static String channellistToString(List<Channel> channels) {
        Gson gson = new Gson();
        String channelListStr = gson.toJson(channels);
        return channelListStr;
    }

    public static List<Channel> stringToChannelList(String listData) {
        Glog.i("linbingjie", "listData: " + listData);
        Gson gson = new Gson();
        List<Channel> channels = gson.fromJson(listData, new TypeToken<List<Channel>>(){}.getType());
        return channels;
    }

    public static String searchListToString(List<String> searchList) {
        Gson gson = new Gson();
        String searchListStr = gson.toJson(searchList);
        return searchListStr;
    }

    public static List<String> stringToSearchList(String listData) {
        Glog.i("linbingjie", "listData: " + listData);
        Gson gson = new Gson();
        List<String> searchList = gson.fromJson(listData, new TypeToken<List<String>>(){}.getType());
        return searchList;
    }


    public static String myMusicServiceBeanListToString(List<MyMusicServiceBean> musicServices) {
        Gson gson = new Gson();
        Glog.i("lbj", "myMusicServiceListToString: ");
        String musicServiceListStr = gson.toJson(musicServices);
        Glog.i("lbj", "myMusicServiceListToString: ");
        return musicServiceListStr;
    }

    public static List<MyMusicServiceBean> stringToMyMusicServiceBeanList(String listData) {
        Gson gson = new Gson();
        List<MyMusicServiceBean> musicServices = gson.fromJson(listData, new TypeToken<List<MyMusicServiceBean>>(){}.getType());
        return musicServices;
    }
}
