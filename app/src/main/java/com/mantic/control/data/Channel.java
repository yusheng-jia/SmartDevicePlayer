package com.mantic.control.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.text.TextUtils;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.DeviceResource;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.DeviceManager;
import com.mantic.control.ManticApplication;
import com.mantic.control.api.baidu.BaiduRetrofit;
import com.mantic.control.api.baidu.BaiduServiceApi;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.antservice.baidu.listener.DeviceControlListener;
import com.mantic.antservice.m2m.WebSocketCommand;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.musicservice.IMusicServiceTrackContent;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.TimeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by chenyun on 17-5-27.
 */

public class Channel implements Serializable{
    private static final String TAG = "Channel";
    public static int PLAY_STATE_PLAYING = 0;
    public static int PLAY_STATE_PAUSE = 1;
    public static int PLAY_STATE_STOP = 2;
    private String name;
    private int mantic_fee;
    private String playUrl;
    private String iconUrl;
    private Bitmap icon;
    private String album;
    private String singer;
    private long mDuration;
    private String serviceId;
    private String uri;
    private String mantic_album_name;
    private String mantic_album_uri;
    private int tlid = -1;
    private long lastSyncTime = 0;
    private boolean isSelected = false;
    private boolean isPlaying = false;
    private int playState = PLAY_STATE_STOP;

    public void setTimePeriods(String timePeriods) {
        this.timePeriods = timePeriods;
    }

    public String getTimePeriods() {
        return timePeriods;
    }

    private String timePeriods;
    private IMusicServiceTrackContent iMusicServiceTrackContent;

    private Flowable<Channel> channelPlaytFlowable;
    private Subscriber<Channel> channelPlaySubscriber;

    //websocket
    private static WebSocketCommand command;

    @Override
    public String toString() {
        return "Channel{" +
                "channelPlaySubscriber=" + channelPlaySubscriber +
                '}';
    }
    //websocket

    //    private Channel.ChannelDeviceControlListener<TaskInfo> mChannelDeviceControlListener;
    public interface ChannelDeviceControlListenerCallBack {
        public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info);

        public void onFailed(HttpStatus code);

        public void onError(IoTException error);
    }

    public Channel() {

    }
    public Channel(String channelName, String playUrl, Bitmap icon, String channelAlbum, String channelSinger) {
        this.name = channelName;
        this.playUrl = playUrl;
        this.icon = icon;
        this.album = channelAlbum;
        this.singer = channelSinger;
    }

    public void setName(String na) {
        this.name = na;
    }


    public int getMantic_fee() {
        return mantic_fee;
    }

    public void setMantic_fee(int mantic_fee) {
        this.mantic_fee = mantic_fee;
    }

    public void setIconUrl(String ul) {
        this.iconUrl = ul;
    }

    public String getIconUrl() {
        return this.iconUrl;
    }

    public String getName() {
        return this.name;
    }

    public void setPlayUrl(String url) {
        this.playUrl = url;
    }

    public String getPlayUrl() {
        String url = null;
        if (this.iMusicServiceTrackContent != null) {
            int urlType = this.iMusicServiceTrackContent.getUrlType();
            if (urlType == IMusicServiceTrackContent.DYNAMIC_URL) {
                url = this.iMusicServiceTrackContent.getPlayUrl();
            } else if (urlType == IMusicServiceTrackContent.STATIC_URL) {
                url = this.playUrl;
            }
        } else {
            url = this.playUrl;
        }
        return url;
    }

    public Bitmap getIcon() {
        return this.icon;
    }

    public String getAlbum() {
        return this.album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setSinger(String sg) {
        this.singer = sg;
    }

    public String getSinger() {
        return this.singer;
    }

    public void setDuration(long duration) {
        mDuration = duration;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMantic_album_name() {
        return mantic_album_name;
    }

    public void setMantic_album_name(String mantic_album_name) {
        this.mantic_album_name = mantic_album_name;
    }

    public String getMantic_album_uri() {
        return mantic_album_uri;
    }

    public void setMantic_album_uri(String mantic_album_uri) {
        this.mantic_album_uri = mantic_album_uri;
    }

    public int getTlid() {
        return tlid;
    }

    public void setTlid(int tlid) {
        this.tlid = tlid;
    }

    public long getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(long lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void registerChannelPlaySubscriber(Subscriber<Channel> channelSubscriber) {
        if (this.channelPlaytFlowable == null) {
            this.channelPlaytFlowable = Flowable.just(this);
        }
        this.channelPlaySubscriber = channelSubscriber;
    }

    private void notifyChannelPlayStop() {
        if (this.channelPlaySubscriber != null) {
            this.channelPlaytFlowable.subscribe(new Consumer<Channel>() {
                @Override
                public void accept(Channel channel) throws Exception {
                    channelPlaySubscriber.onNext(channel);
                    channelPlaySubscriber.onComplete();
                }
            });
        }
    }

    public boolean deviceStop(Context mContext) {
        boolean result = true;
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendStopMusic(null);
        } else {

        }
        if (result) {
            this.setIsPlaying(false);
        }
        return result;
    }

    //new start
    private void devicePause(Context mContext, Channel.ChannelDeviceControlListener<ControlResult> listener) {
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendPauseMusic(listener);
        } else {
//            WebSocketController.getInstance().mConnection.sendTextMessage(command.controlMusicCommand("Pause"),listener);
        }
    }

    private void devicePlay(Context mContext, Channel.ChannelDeviceControlListener<ControlResult> listener) {
        int playState = this.getPlayState();
//        Glog.i(TAG,"devicePlay playState = "+playState);
        if (playState == PLAY_STATE_PAUSE) {
            if (DeviceManager.idRda) {
                DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendResumeMusic(listener);
            } else {
//                WebSocketController.getInstance().mConnection.sendTextMessage(command.controlMusicCommand("Resume"),listener);
            }
        } else if (playState == PLAY_STATE_STOP) {
            if (DeviceManager.idRda) {
                Glog.i("jys","CurrentUri: " + this.getUri());
                String service[] = this.getUri().split(":");
//                if (service[0].equals("qingting") || service[0].equals("netease") || service[0].equals("idaddy") || getUri().contains("organized:east")){
//                    playGetUri(listener);
//                } else if (service[0].equals("baidu") || getUri().contains("baidu")) {
//                    playGetBaiduUri(getUri(), listener, true, null, null);
//                } else {
//                    DeviceStateController.getInstance(mContext,MainActivity.deviceId).sendPlayMusic(this.getPlayUrl(), listener);
//                }
                if (service[0].equals("baidu") || getUri().contains("baidu")) {
                    playGetBaiduUri(mContext, getUri(), listener, true, null, null);
                } else {
                    playGetUri(mContext, listener);
                }

            } else {
//                WebSocketController.getInstance().mConnection.sendTextMessage(command.playMusicCommand(this.getPlayUrl()),listener);
            }
        }
    }


    private void playGetUri(final Context mContext, final Channel.ChannelDeviceControlListener<ControlResult> listener){
        final List<String> uris = new ArrayList<String>();
        uris.add(uri);
        RequestBody body = MopidyTools.createTrackDetail(uris);
        Call<ResponseBody> call = MainActivity.mpServiceApi.postMopidyTrackDetails(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    try {
                        JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                        JSONObject resuleObject = mainObject.getJSONObject("result"); // result json 主体
                        JSONArray imageArray = resuleObject.getJSONArray(uri); //获取第一个item 作为整个TRACKLIST封面
                        final JSONObject image = imageArray.getJSONObject(imageArray.length()-1);
                        String playUrl = image.optString("mantic_real_url");
                        Glog.i("jys","playUrl --- >: " + playUrl);

                        if (!TextUtils.isEmpty(playUrl)){
                            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendPlayMusic(playUrl, listener);
                        }else {
                            listener.onFailed(HttpStatus.URL_NOT_FOUND);
                            ToastUtils.showShortSafe(mContext.getString(R.string.play_address_empty));
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtils.showShortSafe(mContext.getString(R.string.play_failed));
            }
        });
    }

    public static void playGetBaiduUri(final Context mContext, String uri, final Channel.ChannelDeviceControlListener<ControlResult> listener, final boolean isPlayChannel, final DataFactory mDataFactory, final Activity activity) {
        if (!uri.contains("baidu")) {
            return;
        }
        String[] service = uri.split(":");
        if (null == service || service.length <= 0) {
            return;
        }
        Call<ResponseBody> getTrackInfoCall= BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackInfoBySongId("http://s.xiaodu.baidu.com/v20161223/resource/musicdetail?song_id=" + service[service.length - 1]);

        getTrackInfoCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject data = jsonObject.optJSONObject("data");
                    final String playUrl = data.optString("streaming_vedio_url");
                    Glog.i(TAG, "streaming_vedio_url: " + playUrl);
                    if (!TextUtils.isEmpty(playUrl) && !playUrl.contains("kuwo")){
                        final int duration = data.optInt("duration");
                        SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "baiduTempDuration", "" + duration * 1000);
                        if (null != mDataFactory) {
                            if (null != activity) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Channel currChannel = mDataFactory.getCurrChannel();
                                        if (null != currChannel) {
                                            currChannel.setDuration(duration * 1000);
                                            mDataFactory.setCurrChannel(currChannel);
                                            mDataFactory.notifyBeingPlayListChange();
                                            mDataFactory.notifyUpdateAudioPlayerByBaiduDuration();
                                        }
                                    }
                                });
                            }
                        }

                        /* new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    MediaPlayer mediaPlayer = new MediaPlayer();
                                    mediaPlayer.setDataSource(playUrl);
                                    mediaPlayer.prepare();
                                    final int duration = mediaPlayer.getDuration();
                                    SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "baiduTempDuration", "" + duration);
                                    if (null != mDataFactory) {
                                        if (null != activity) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Channel currChannel = mDataFactory.getCurrChannel();
                                                    if (null != currChannel) {
                                                        currChannel.setDuration(duration);
                                                        mDataFactory.setCurrChannel(currChannel);
                                                        mDataFactory.notifyBeingPlayListChange();
                                                        mDataFactory.notifyUpdateAudioPlayerByBaiduDuration();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                    mediaPlayer.stop();
                                    mediaPlayer = null;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();*/

                        if (isPlayChannel) {
                            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendPlayMusic(playUrl, listener);
//                            ToastUtils.showShortSafe("播放歌曲: " + data.optString("name"));
                        }
                    }else {
                        if (isPlayChannel) {
//                            ToastUtils.showShortSafe(mContext.getString(R.string.play_failed));
                            listener.onFailed(HttpStatus.URL_NOT_FOUND);
                            ToastUtils.showShortSafe(mContext.getString(R.string.play_address_empty));
                        }
                    }
                } catch (Exception e) {
                    Glog.i("getTrackInfoCall", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Glog.i("getTrackInfoCall", t.getMessage());
            }
        });
    }

    private void channelPause(Context mContext, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (callBack != null) {
                    callBack.onSuccess(code, obj, info);
//                    DataFactory dataFactory = DataFactory.getInstance();
//                    ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
//                    for(int i = 0;i < channelControlListeners.size();i++){
//                        DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
//                        listener.afterChannelControl();
//                    }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if(callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);

        this.devicePause(mContext, listener);
    }

    private void channelPlay(final Context mContext, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        final Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (callBack != null) {
                    callBack.onSuccess(code, obj, info);
                    if (null != mContext) {
                        ((ManticApplication)mContext.getApplicationContext()).setChannelStop(false);
                    }

//                DataFactory dataFactory = DataFactory.getInstance();
//                ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
//                for(int i = 0;i < channelControlListeners.size();i++){
//                    DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
//                    int index = dataFactory.getBeingPlayList().indexOf(channel);
//                    listener.beginChannelControl(index);
//                    listener.afterChannelControl();
//                }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if(callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);

        this.devicePlay(mContext, listener);
    }

    public void playPause(final Context mContext, final Channel channel, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        DeviceControlListener.showToast = true;
        if (SharePreferenceUtil.getDeviceMode(mContext) == 0 || SharePreferenceUtil.getDeviceMode(mContext) == -1) {
            Glog.i(TAG, "play pause playing: " + isPlaying + " state: " + channel.getPlayState());
            if (channel.getPlayState() == PLAY_STATE_PLAYING ) {
                channelPause(mContext, callBack);
            } else if (channel.getPlayState() == PLAY_STATE_STOP || channel.getPlayState() == PLAY_STATE_PAUSE) {
                channelPlay(mContext, callBack);
            }
        } else {
            getDevicePlayMode(mContext, new ChannelDeviceControlListenerCallBack() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    try {
                        JSONObject jsonObject = new JSONObject(obj.content);
                        int mode = jsonObject.optInt("mode");
                        SharePreferenceUtil.setDeviceMode(mContext, mode);
                        if (mode == 0) {
                            if (channel.getPlayState() == PLAY_STATE_PLAYING ) {
                                channelPause(mContext, callBack);
                            } else if (channel.getPlayState() == PLAY_STATE_STOP || channel.getPlayState() == PLAY_STATE_PAUSE) {
                                channelPlay(mContext, callBack);
                            }
                        } else {
                            ToastUtils.showShortSafe(R.string.operate_the_application_in_network_mode);
                            return;
                        }
                    } catch (Exception e) {
                        ToastUtils.showShortSafe(R.string.operate_the_application_in_network_mode);
                        return;
                    }
                }

                @Override
                public void onFailed(HttpStatus code) {
                    ToastUtils.showShortSafe(R.string.operate_the_application_in_network_mode);
                    return;
                }

                @Override
                public void onError(IoTException error) {
                    ToastUtils.showShortSafe(R.string.operate_the_application_in_network_mode);
                    return;
                }
            });
        }

//        DataFactory dataFactory = DataFactory.getInstance();
//        ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
//        for(int i = 0;i < channelControlListeners.size();i++){
//            DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
//            listener.preChannelControl();
//        }

    }

    public void playPauseByOnlyOne(Context mContext, Channel channel, final Channel.ChannelDeviceControlListenerCallBack callBack) {
//        DataFactory dataFactory = DataFactory.getInstance();
//        ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
//        for(int i = 0;i < channelControlListeners.size();i++){
//            DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
//            listener.preChannelControl();
//        }
        if (SharePreferenceUtil.getDeviceMode(mContext) == 0 || SharePreferenceUtil.getDeviceMode(mContext) == -1) {
            Glog.i(TAG, "play pause playing: " + isPlaying + " state: " + channel.getPlayState());
            channelPlay(mContext, callBack);
        } else {
            ToastUtils.showShortSafe(R.string.operate_the_application_in_network_mode);
            return;
        }

    }


    public static void getPlayVolume(Context mContext, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if(callBack != null) {
                    callBack.onSuccess(code, obj, info);
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).getDeviceVolume(listener);
        } else {
        }
    }

    public static void getPlayProgress(Context mContext, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if(callBack != null) {
                    callBack.onSuccess(code, obj, info);
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).getDeviceProgress(listener);
        } else {
        }
    }

    public static void sendPlayVolume(Context mContext, int volume, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        DataFactory dataFactory = DataFactory.getInstance();
        ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
        for (int i = 0; i < channelControlListeners.size(); i++) {
            DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
            listener.preChannelControl();
        }

        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (callBack != null) {
                    callBack.onSuccess(code, obj, info);
                    DataFactory dataFactory = DataFactory.getInstance();
                    ArrayList<DataFactory.ChannelControlListener> channelControlListeners = dataFactory.getChannelControlListeners();
                    for (int i = 0; i < channelControlListeners.size(); i++) {
                        DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
                        listener.afterChannelControl();
                    }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).sendPlayVolume(volume, listener);
        } else {
//            WebSocketController.getInstance().mConnection.sendTextMessage(command.writeVolCommand(volume),listener);
        }
    }


    public static void getDevicePlayMode(Context mContext, final Channel.ChannelDeviceControlListenerCallBack callBack) {
        Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if(callBack != null) {
                    callBack.onSuccess(code, obj, info);
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (callBack != null) {
                    callBack.onFailed(code);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);
        if (DeviceManager.idRda) {
            DeviceStateController.getInstance(mContext,SharePreferenceUtil.getDeviceId(mContext)).getDevicePlayMode(listener);
        } else {
        }
    }

    public void setPlayState(int state) {
        playState = state;
    }

    public int getPlayState() {
        return this.playState;
    }
    //new end

    public boolean getIsSelected() {
        return this.isSelected;
    }

    public void setIsSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean getIsPlaying() {
        return this.isPlaying;
    }

    public void setIsPlaying(boolean playing) {
//        this.isPlaying = playing;
    }

    public void setIMusicServiceTrackContent(IMusicServiceTrackContent iMSTC) {
        this.iMusicServiceTrackContent = iMSTC;
    }

    public boolean equals(Channel channel) {
        boolean result = false;
//        Glog.i(TAG,"Channel equals = "+"this.iconUrl = "+this.iconUrl+"---channel.iconUrl = "+channel.iconUrl+"---this.name = "+this.name+"---channel.name = "+channel.name+"---this.album = "+this.album+"---channel.album = "+channel.album);
        if ((this.iconUrl != null && channel.iconUrl != null && this.iconUrl.equals(channel.iconUrl))
                && (this.name != null && channel.name != null && this.name.equals(channel.name))) {
            result = true;
        }
//        Glog.i(TAG,"Channel equals result = "+result);
        return result;
    }

    public static class ChannelDeviceControlListener<ControlResult> extends DeviceControlListener<ControlResult> {
        private Channel.ChannelDeviceControlListenerCallBack channelDeviceControlListenerCallBack;

        public ChannelDeviceControlListener() {
            super();
        }

        public ChannelDeviceControlListener(Context context, DeviceResource.Resource resource) {
            super(context, resource);
        }

        public void setChannelDeviceControlListenerCallBack(Channel.ChannelDeviceControlListenerCallBack callBack) {
            this.channelDeviceControlListenerCallBack = callBack;
        }

        @Override
        public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
            com.baidu.iot.sdk.model.ControlResult result = (com.baidu.iot.sdk.model.ControlResult) obj;
            super.onSuccess(code, obj, info);
            if (this.channelDeviceControlListenerCallBack != null) {
                this.channelDeviceControlListenerCallBack.onSuccess(code, (com.baidu.iot.sdk.model.ControlResult) obj, info);
            }
        }

        @Override
        public void onFailed(HttpStatus code) {
            super.onFailed(code);
            if (this.channelDeviceControlListenerCallBack != null) {
                this.channelDeviceControlListenerCallBack.onFailed(code);
            }
        }

        @Override
        public void onError(IoTException error) {
            super.onError(error);
            if (this.channelDeviceControlListenerCallBack != null) {
                this.channelDeviceControlListenerCallBack.onError(error);
            }
        }
    }
}