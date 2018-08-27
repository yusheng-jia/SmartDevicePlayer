package com.mantic.control.manager;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.AudioPlayer;
import com.mantic.control.api.channelplay.ChannelPlayOperatorRetrofit;
import com.mantic.control.api.channelplay.ChannelPlayOperatorServiceApi;
import com.mantic.control.api.channelplay.bean.AddParams;
import com.mantic.control.api.channelplay.bean.AddTrack;
import com.mantic.control.api.channelplay.bean.ChangeParams;
import com.mantic.control.api.channelplay.bean.ChannelInfoListRqBean;
import com.mantic.control.api.channelplay.bean.ChannelInfoListRqParam;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayInsertRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayInsertRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListClearRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListMoveRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListMoveRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayModeGetRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayModeGetRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayModeSetParams;
import com.mantic.control.api.channelplay.bean.ChannelPlayModeSetRqBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayMoveUpdateRqBean;
import com.mantic.control.api.channelplay.bean.DeleteParams;
import com.mantic.control.api.channelplay.bean.GetCurrentChannelPlayRqBean;
import com.mantic.control.api.channelplay.bean.GetCurrentChannelPlayRsBean;
import com.mantic.control.api.channelplay.bean.InsertParams;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelParams;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRqBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.api.channelplay.bean.UpdateParams;
import com.mantic.control.api.channelplay.bean.UpdateTrack;
import com.mantic.control.api.mopidy.MopidyTools;
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

public class ChannelPlayListManager {
    private static ChannelPlayListManager instance = null;
    private ChannelPlayOperatorServiceApi serviceApi;

    private ChannelPlayListManager() {
        serviceApi = ChannelPlayOperatorRetrofit.getInstance().create(ChannelPlayOperatorServiceApi.class);
    }

    public static ChannelPlayListManager getInstance() {
        if (instance == null) {
            synchronized (ChannelPlayListManager.class) {
                instance = new ChannelPlayListManager();
            }
        }
        return instance;
    }


    public void addChannelPlayList(final Callback callback, List<Channel> channelList, Context context) {
        Call<ChannelPlayAddRsBean> addCall = serviceApi.postChannelPlayAddQuest(MopidyTools.getHeaders(),createAddRqBean(channelList, context));
        addCall.enqueue(new Callback<ChannelPlayAddRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void getChannelPlayList(final Callback<ChannelPlayListRsBean> callback, Context context) {
        Call<ChannelPlayListRsBean> addCall = serviceApi.postChannelPlayListQuest(MopidyTools.getHeaders(),createGetRqBean(context));
        addCall.enqueue(new Callback<ChannelPlayListRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayListRsBean> call, Response<ChannelPlayListRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayListRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void deleteChannelPlayList(final Callback<ChannelPlayListDeleteRsBean> callback, Context context, int tlid) {
        Call<ChannelPlayListDeleteRsBean> addCall = serviceApi.postChannelPlayDeleteListQuest(MopidyTools.getHeaders(),createDeleteRqBean(context, tlid));
        addCall.enqueue(new Callback<ChannelPlayListDeleteRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayListDeleteRsBean> call, Response<ChannelPlayListDeleteRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayListDeleteRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    /**
     * 会替换掉tlid，谨慎使用
     * @param callback
     * @param channelList
     * @param context
     */
    public void updateChannelPlayList(final Callback<ChannelPlayAddRsBean> callback, List<Channel> channelList, Context context) {
        Call<ChannelPlayAddRsBean> addCall = serviceApi.postChannelPlayUpdateQuest(MopidyTools.getHeaders(),createUpdateRqBean(channelList, context));
        addCall.enqueue(new Callback<ChannelPlayAddRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void moveUpdateChannelPlayList(final Callback<ResponseBody> callback, List<Channel> channelList, Context context) {
        Call<ResponseBody> addCall = serviceApi.postChannelPlayMoveUpdateQuest(MopidyTools.getHeaders(),createMoveUpdateRqBean(channelList, context));
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


    public void insertChannelPlayList(final Callback<ChannelPlayInsertRsBean> callback, List<Channel> channelList, Context context, int position) {
        Call<ChannelPlayInsertRsBean> addCall = serviceApi.postChannelPlayInsertQuest(MopidyTools.getHeaders(),createInsertRqBean(channelList, context, position));
        addCall.enqueue(new Callback<ChannelPlayInsertRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayInsertRsBean> call, Response<ChannelPlayInsertRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayInsertRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void changeChannelPlayListPosition(final Callback<ChannelPlayListMoveRsBean> callback, Context context, int start, int end, int position) {
        Call<ChannelPlayListMoveRsBean> call = serviceApi.postChannelPlayChangeQuest(MopidyTools.getHeaders(),createChangeRqBean(start, end, position, context));
        call.enqueue(new Callback<ChannelPlayListMoveRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayListMoveRsBean> call, Response<ChannelPlayListMoveRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayListMoveRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void setCurrentChannelPlay(final Callback<SetCurrentChannelPlayRsBean> callback, long tlid, Context context) {
        Call<SetCurrentChannelPlayRsBean> addCall = serviceApi.postChannelPlaySetCurrentQuest(MopidyTools.getHeaders(),createSetCurrentRqBean(tlid, context));
        addCall.enqueue(new Callback<SetCurrentChannelPlayRsBean>() {
            @Override
            public void onResponse(Call<SetCurrentChannelPlayRsBean> call, Response<SetCurrentChannelPlayRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<SetCurrentChannelPlayRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void getCurrentChannelPlay(final Callback<GetCurrentChannelPlayRsBean> callback, Context context) {
        Call<GetCurrentChannelPlayRsBean> addCall = serviceApi.postChannelPlayGetCurrentQuest(MopidyTools.getHeaders(),createGetCurrentRqBean(context));
        addCall.enqueue(new Callback<GetCurrentChannelPlayRsBean>() {
            @Override
            public void onResponse(Call<GetCurrentChannelPlayRsBean> call, Response<GetCurrentChannelPlayRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<GetCurrentChannelPlayRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }


    public void getChannelInfoList(final Callback<ResponseBody> callback, List<String> uris) {
        Call<ResponseBody> addCall = serviceApi.postChannelInfoListQuest(MopidyTools.getHeaders(),createGetChannelInfoListRqBean(uris));
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

    public void setChannelPlayMode(final Callback<ResponseBody> callback, Context context, int mode) {
        Call<ResponseBody> addCall = serviceApi.postChannelPlaySetModeQuest(MopidyTools.getHeaders(),createSetModeRqBean(context, mode));
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

    public void getChannelPlayMode(final Callback<ChannelPlayModeGetRsBean> callback, Context context) {
        Call<ChannelPlayModeGetRsBean> addCall = serviceApi.postChannelPlayGetModeQuest(MopidyTools.getHeaders(),createGetModeRqBean(context));
        addCall.enqueue(new Callback<ChannelPlayModeGetRsBean>() {
            @Override
            public void onResponse(Call<ChannelPlayModeGetRsBean> call, Response<ChannelPlayModeGetRsBean> response) {
                callback.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ChannelPlayModeGetRsBean> call, Throwable t) {
                callback.onFailure(call, t);
            }
        });
    }

    public void clearChannelPlayList(final Callback<ResponseBody> callback, Context context) {
        Call<ResponseBody> addCall = serviceApi.postChannelPlayClearQuest(MopidyTools.getHeaders(),createClearRqBean(context));
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

    public void playSongByCode(final Callback<ResponseBody> callback, Context context, int code) {
        String playMethod = "";
        if (AudioPlayer.PLAY_COOMAAN_PRE == code) {
            playMethod = "core.playback.previous";
        } else if (AudioPlayer.PLAY_COOMAAN_NEXT == code) {
            playMethod = "core.playback.next";
        }
        Call<ResponseBody> addCall = serviceApi.postChannelPlayCodeQuest(playCodeRqBean(context, playMethod));
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


    private RequestBody createAddRqBean(List<Channel> channelList, Context context) {
        ChannelPlayAddRqBean addBean = new ChannelPlayAddRqBean();
        addBean.setMethod("core.tracklist.add");
        addBean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        AddParams params = new AddParams();

        ArrayList<AddTrack> addTracks = new ArrayList<>();
        for (int i = 0; i < channelList.size(); i++) {
            AddTrack track = new AddTrack();
            Channel channel = channelList.get(i);
            track.set__model__("Track");
            track.setName(channel.getName());
            track.setMantic_last_modified(TimeUtil.getDateFromMillisecond(System.currentTimeMillis()));
            track.setUri(channel.getUri());
            track.setMantic_image(channel.getIconUrl());
            track.setLength(channel.getDuration());
            track.setMantic_real_url(channel.getPlayUrl());
            track.setTrack_no(0);
            track.setMantic_album_name(channel.getMantic_album_name());
            ArrayList<String> artists = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                artists.add(channel.getSinger());
            }
            track.setMantic_artists_name(artists);
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

    private RequestBody createUpdateRqBean(List<Channel> channelList, Context context) {
        ChannelPlayAddRqBean addBean = new ChannelPlayAddRqBean();
        addBean.setMethod("core.tracklist.update");
        addBean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        AddParams params = new AddParams();


        ArrayList<AddTrack> addTracks = new ArrayList<>();
        for (int i = 0; i < channelList.size(); i++) {
            AddTrack track = new AddTrack();
            Channel channel = channelList.get(i);
            track.set__model__("Track");
            track.setName(channel.getName());
            track.setMantic_last_modified(TimeUtil.getDateFromMillisecond(System.currentTimeMillis()));
            track.setUri(channel.getUri());
            track.setMantic_image(channel.getIconUrl());
            track.setLength(channel.getDuration());
            track.setMantic_real_url(channel.getPlayUrl());
            track.setTrack_no(0);
            if (!TextUtils.isEmpty(channel.getTimePeriods())) {
                track.setMantic_radio_length(channel.getTimePeriods());
            }
            track.setMantic_album_name(channel.getMantic_album_name());
            track.setMantic_album_uri(channel.getMantic_album_uri());
            ArrayList<String> artists = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                artists.add(channel.getSinger());
            }
            track.setMantic_artists_name(artists);

            addTracks.add(track);
            if (channel.getPlayState() == Channel.PLAY_STATE_PLAYING) {
                params.setPlay_position(i);
            }
        }

        if(channelList.get(0).getUri().contains(":radio:")) {
            params.setPlay_position(0);
        }

        params.setTracks(addTracks);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createUpdateRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private RequestBody createMoveUpdateRqBean(List<Channel> channelList, Context context) {
        ChannelPlayMoveUpdateRqBean bean = new ChannelPlayMoveUpdateRqBean();
        bean.setMethod("core.tracklist.mantic_move_update");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        UpdateParams params = new UpdateParams();
        List<UpdateTrack> tl_tracks = new ArrayList<>();

        for (int i = 0; i < channelList.size(); i++) {
            Channel channel = channelList.get(i);
            UpdateTrack track = new UpdateTrack();
            track.set__model__("TlTrack");
            track.setTlid(channel.getTlid());
            AddTrack addTrack = new AddTrack();
            addTrack.set__model__("Track");
            addTrack.setName(channel.getName());
            addTrack.setMantic_last_modified(TimeUtil.getDateFromMillisecond(System.currentTimeMillis()));
            addTrack.setUri(channel.getUri());
            addTrack.setMantic_image(channel.getIconUrl());
            addTrack.setLength(channel.getDuration());
            addTrack.setMantic_real_url(channel.getPlayUrl());
            addTrack.setTrack_no(0);
            addTrack.setMantic_album_name(channel.getMantic_album_name());
            addTrack.setMantic_album_uri(channel.getMantic_album_uri());
            ArrayList<String> artists = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                artists.add(channel.getSinger());
            }
            addTrack.setMantic_artists_name(artists);

            track.setTrack(addTrack);
            tl_tracks.add(track);
        }

        params.setTl_tracks(tl_tracks);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createMoveUpdateRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createInsertRqBean(List<Channel> channelList, Context context, int position) {
        ChannelPlayInsertRqBean addBean = new ChannelPlayInsertRqBean();
        addBean.setMethod("core.tracklist.add");
        addBean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        addBean.setJsonrpc("2.0");
        addBean.setId(1);
        InsertParams params = new InsertParams();

        params.setAt_position(position);

        ArrayList<AddTrack> addTracks = new ArrayList<>();
        for (int i = 0; i < channelList.size(); i++) {
            AddTrack track = new AddTrack();
            Channel channel = channelList.get(i);
            track.set__model__("Track");
            track.setName(channel.getName());
            track.setMantic_last_modified(TimeUtil.getDateFromMillisecond(System.currentTimeMillis()));
            track.setUri(channel.getUri());
            track.setMantic_image(channel.getIconUrl());
            track.setLength(channel.getDuration());
            track.setMantic_real_url(channel.getPlayUrl());
            track.setMantic_album_name(channel.getMantic_album_name());
            track.setMantic_album_uri(channel.getMantic_album_uri());
            track.setTrack_no(0);
            ArrayList<String> artists = new ArrayList<>();
            if (!TextUtils.isEmpty(channel.getSinger())) {
                artists.add(channel.getSinger());
            }
            track.setMantic_artists_name(artists);

            addTracks.add(track);
        }

        params.setTracks(addTracks);
        addBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(addBean);
        Glog.i("lbj", "createInsertRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createChangeRqBean(int start, int end, int to_position, Context context) {
        ChannelPlayListMoveRqBean bean = new ChannelPlayListMoveRqBean();
        bean.setMethod("core.tracklist.move");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        ChangeParams params = new ChangeParams();

        params.setStart(start);
        params.setEnd(end);
        params.setTo_position(to_position);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createChangeRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createSetCurrentRqBean(long tlid, Context context) {
        SetCurrentChannelPlayRqBean setBean = new SetCurrentChannelPlayRqBean();
        setBean.setMethod("core.playback.play");
        setBean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        setBean.setJsonrpc("2.0");
        setBean.setId(1);
        SetCurrentChannelParams params = new SetCurrentChannelParams();
        params.tlid = tlid;
        setBean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(setBean);
        Glog.i("lbj", "createSetRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private RequestBody createGetCurrentRqBean(Context context) {
        GetCurrentChannelPlayRqBean setBean = new GetCurrentChannelPlayRqBean();
        setBean.setMethod("core.playback.get_current_tl_track");
        setBean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        setBean.setJsonrpc("2.0");
        setBean.setId(1);
        Gson gson = new Gson();
        String request = gson.toJson(setBean);
        Glog.i("lbj", "createGetRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createDeleteRqBean(Context context, int tlid) {
        ChannelPlayListDeleteRqBean bean = new ChannelPlayListDeleteRqBean();
        bean.setMethod("core.tracklist.remove");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        DeleteParams params = new DeleteParams();
        List<Integer> tlids = new ArrayList<>();
        tlids.add(tlid);
        params.setTlid(tlids);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createClearRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    public RequestBody createGetRqBean(Context context) {
        ChannelPlayListDeleteRqBean clearBean = new ChannelPlayListDeleteRqBean();
        clearBean.setMethod("core.tracklist.get_tl_tracks");
        clearBean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        clearBean.setJsonrpc("2.0");
        clearBean.setId(1);

        Gson gson = new Gson();
        String request = gson.toJson(clearBean);
        Glog.i("lbj", "createGetRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createGetChannelInfoListRqBean(List<String> uris) {
        ChannelInfoListRqBean bean = new ChannelInfoListRqBean();
        bean.setMethod("core.library.manitc_get_tracks_detail");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        ChannelInfoListRqParam params = new ChannelInfoListRqParam();
        params.setUris(uris);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetChannelInfoListRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody createSetModeRqBean(Context context, int mode) {
        ChannelPlayModeSetRqBean bean = new ChannelPlayModeSetRqBean();
        bean.setMethod("core.tracklist.set_play_mode");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        ChannelPlayModeSetParams params = new ChannelPlayModeSetParams();
        params.setValue(mode); //0是顺序, 1是单曲，2是随机
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createSetModeRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private RequestBody createGetModeRqBean(Context context) {
        ChannelPlayModeGetRqBean bean = new ChannelPlayModeGetRqBean();
        bean.setMethod("core.tracklist.get_play_mode");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createGetModeRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    private RequestBody createClearRqBean(Context context) {
        ChannelPlayListClearRqBean bean = new ChannelPlayListClearRqBean();
        bean.setMethod("core.tracklist.clear");
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createClearRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }


    private RequestBody playCodeRqBean(Context context, String playMethod) {
        ChannelPlayListClearRqBean bean = new ChannelPlayListClearRqBean();
        bean.setMethod(playMethod);
        bean.setDevice_id(SharePreferenceUtil.getDeviceId(context));
        bean.setJsonrpc("2.0");
        bean.setId(1);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "playCodeRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }
}
