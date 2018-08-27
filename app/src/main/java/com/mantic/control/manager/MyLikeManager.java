package com.mantic.control.manager;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mantic.control.api.channelplay.ChannelPlayOperatorRetrofit;
import com.mantic.control.api.channelplay.ChannelPlayOperatorServiceApi;
import com.mantic.control.api.channelplay.bean.AddParams;
import com.mantic.control.api.channelplay.bean.AddTrack;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListRsBean;
import com.mantic.control.api.channelplay.bean.GetCurrentChannelPlayRqBean;
import com.mantic.control.api.channelplay.bean.GetCurrentChannelPlayRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelParams;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRqBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mylike.MyLikeOperatorServiceApi;
import com.mantic.control.api.mylike.bean.MyLikeAddPrams;
import com.mantic.control.api.mylike.bean.MyLikeAddRqBean;
import com.mantic.control.api.mylike.bean.MyLikeAddRsBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRqBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.api.mylike.bean.MyLikeDeltePrams;
import com.mantic.control.api.mylike.bean.MyLikeGetPrams;
import com.mantic.control.api.mylike.bean.MyLikeGetRqBean;
import com.mantic.control.api.mylike.bean.MyLikeGetRsBean;
import com.mantic.control.api.mylike.bean.MyLikeUpdatePlayList;
import com.mantic.control.api.mylike.bean.MyLikeUpdatePrams;
import com.mantic.control.api.mylike.bean.MyLikeUpdateRqBean;
import com.mantic.control.api.mylike.bean.Track;
import com.mantic.control.data.Channel;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/6/22.
 */

public class MyLikeManager {
    private static MyLikeManager instance = null;
    private MyLikeOperatorServiceApi serviceApi;

    private MyLikeManager() {
        serviceApi = ChannelPlayOperatorRetrofit.getInstance().create(MyLikeOperatorServiceApi.class);
    }

    public static MyLikeManager getInstance() {
        if (instance == null) {
            synchronized (MyLikeManager.class) {
                instance = new MyLikeManager();
            }
        }
        return instance;
    }


    public  void addMyLike(final Callback<MyLikeAddRsBean> callback, Channel channel, Context context) {
        List<Channel> channels = new ArrayList<>();
        channels.add(channel);
        Call<MyLikeAddRsBean> addCall = serviceApi.postMyLikeAddQuest(MopidyTools.getHeaders(),createAddRqBean(channels, context));
        addCall.enqueue(new Callback<MyLikeAddRsBean>() {
            @Override
            public void onResponse(Call<MyLikeAddRsBean> call, Response<MyLikeAddRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyLikeAddRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public  void deleteMyLike(final Callback<MyLikeDeleteRsBean> callback, Channel channel, Context context) {
        List<Channel> channels = new ArrayList<>();
        channels.add(channel);
        Call<MyLikeDeleteRsBean> addCall = serviceApi.postMyLikeDeleteQuest(MopidyTools.getHeaders(),createDeleteRqBean(channels, context));
        addCall.enqueue(new Callback<MyLikeDeleteRsBean>() {
            @Override
            public void onResponse(Call<MyLikeDeleteRsBean> call, Response<MyLikeDeleteRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyLikeDeleteRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public  void getMyLike(final Callback<MyLikeGetRsBean> callback, Context context) {
        Call<MyLikeGetRsBean> addCall = serviceApi.postMyLikeListQuest(MopidyTools.getHeaders(),createGetRqBean(context));
        addCall.enqueue(new Callback<MyLikeGetRsBean>() {
            @Override
            public void onResponse(Call<MyLikeGetRsBean> call, Response<MyLikeGetRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<MyLikeGetRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void updateMyLike(final Callback<ResponseBody> callback, Context context, String uri, List<Channel> channelList) {
        Call<ResponseBody> addCall = serviceApi.postMyLikeUpdateQuest(MopidyTools.getHeaders(),createUpdateRqBean(context, uri, channelList));
        addCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    private  RequestBody createAddRqBean(List<Channel> channelList, Context context) {
        MyLikeAddRqBean addBean = new MyLikeAddRqBean();
        addBean.setMethod("core.playlists.mantic_favorite_add");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        MyLikeAddPrams params = new MyLikeAddPrams();

        ArrayList<Track> addTracks = new ArrayList<Track>();
        for (int i = 0; i < channelList.size(); i++) {
            Track track = new Track();
            Channel channel = channelList.get(i);
            track.set__model__("Track");
            track.setName(channel.getName());
            track.setMantic_last_modified(TimeUtil.getDateFromMillisecond(System.currentTimeMillis()));
            track.setMantic_real_url(channel.getPlayUrl());
            track.setUri(channel.getUri());
            track.setMantic_image(channel.getIconUrl());
            track.setLength(channel.getDuration());
            track.setTrack_no(0);
            ArrayList<String> artists = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                artists.add(channel.getSinger());
            }
            track.setMantic_artists_name(artists);
            track.setMantic_album_name(channel.getMantic_album_name());
            track.setMantic_album_uri(channel.getMantic_album_uri());
            addTracks.add(track);
        }

        params.setTracks(addTracks);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createAddRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private  RequestBody createDeleteRqBean(List<Channel> channelList, Context context) {
        MyLikeDeleteRqBean addBean = new MyLikeDeleteRqBean();
        addBean.setMethod("core.playlists.mantic_favorite_remove");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        MyLikeDeltePrams params = new MyLikeDeltePrams();

        List<String> track_uris = new ArrayList<>();
        for (int i = 0; i < channelList.size(); i++) {
            Channel channel = channelList.get(i);
            track_uris.add(channel.getUri());
        }
        params.setUri_scheme("mongodb");
        params.setTrack_uris(track_uris);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createDeleteRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private  RequestBody createGetRqBean(Context context) {
        MyLikeGetRqBean addBean = new MyLikeGetRqBean();
        addBean.setMethod("core.playlists.mantic_get_favorite");
        addBean.setDevice_id(SharePreferenceUtil.getUserId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        MyLikeGetPrams params = new MyLikeGetPrams();

        params.setInclude_tracks(true);
        params.setUri_scheme("mongodb");
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createGetRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private  RequestBody createUpdateRqBean(Context context, String uri, List<Channel> channelList) {
        MyLikeUpdateRqBean bean = new MyLikeUpdateRqBean();
        bean.setMethod("core.playlists.mantic_save_favorite");
        bean.setDevice_id(SharePreferenceUtil.getUserId(context));
        bean.setId(1);
        bean.setJsonrpc("2.0");
        MyLikeUpdatePrams params = new MyLikeUpdatePrams();
        params.setUri_scheme("mongodb");
        MyLikeUpdatePlayList playlist = new MyLikeUpdatePlayList();
        playlist.set__model__("Playlist");
        playlist.setMantic_device_id(SharePreferenceUtil.getUserId(context));
        playlist.setName("favorite");
        playlist.setUri(uri);
        List<Track> tracks = new ArrayList<>();
        for (int i = 0; i < channelList.size(); i++) {
            Track track = new Track();
            Channel channel = channelList.get(i);
            track.set__model__("Track");
            track.setName(channel.getName());
            track.setMantic_last_modified(TimeUtil.getDateFromMillisecond(System.currentTimeMillis()));
            track.setMantic_real_url(channel.getPlayUrl());
            track.setUri(channel.getUri());
            track.setMantic_image(channel.getIconUrl());
            track.setLength(channel.getDuration());
            track.setTrack_no(0);
            ArrayList<String> artists = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                artists.add(channel.getSinger());
            }
            track.setMantic_artists_name(artists);
            track.setMantic_album_name(channel.getMantic_album_name());
            track.setMantic_album_uri(channel.getMantic_album_uri());
            tracks.add(track);
        }
        playlist.setTracks(tracks);
        params.setPlaylist(playlist);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createUpdateRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


}
