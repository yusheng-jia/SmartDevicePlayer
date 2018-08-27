package com.mantic.control.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.baidu.BaiduRetrofit;
import com.mantic.control.api.baidu.BaiduServiceApi;
import com.mantic.control.api.baidu.bean.BaiduTrackList;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.musicservice.IMusicServiceTrackContent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.iot.sdk.HttpStatus.PUT_OR_POST_SUCCESS;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;

/**
 * Created by lin on 2017/12/26.
 * 自动播放的工具类
 */

public class AutomaticUtil {
    public static void getAutomticChannelLsit(final Context context, final DataFactory mDataFactory, final Activity mActivity, final MainActivity.AutomaticCallback callback) {
        String nonce = Util.randomString(16);
        String access_token = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        String securityAppKey = Util.getSecurityAppKey(nonce, access_token);

        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.put("X-IOT-APP", "d3S3SbItdlYDj4KaOB1qIfuM");
        headersMap.put("X-IOT-Signature", nonce + ":" + securityAppKey);
        headersMap.put("X-IOT-Token", access_token);

        Map<String, String> paramsMap = new LinkedHashMap<>();
        paramsMap.put("tag", "蓝调");
        paramsMap.put("page", "1");
        paramsMap.put("page_size", "20");
        Call<BaiduTrackList> getTrackListCall = BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackListByTagName(headersMap, paramsMap);
        getTrackListCall.enqueue(new Callback<BaiduTrackList>() {
            @Override
            public void onResponse(Call<BaiduTrackList> call, Response<BaiduTrackList> response) {
                if (null != response.body() && null != response.body().data) {
                    Glog.i("getTrackListCall", response.body().toString());
                    BaiduTrackList.TrackItem trackItem = response.body().data;
                    final List<Channel> channelList = new ArrayList<>();
                    if (null != trackItem.list && trackItem.list.size() > 0) {
                        ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                        for (int i = 0; i < trackItem.list.size(); i++) {
                            final BaiduTrackList.TrackItem.Track track = trackItem.list.get(i);
                            Channel channel = new Channel();
                            channel.setIconUrl(track.head_image_url);
                            channel.setName(track.name);
                            channel.setUri("baidu:track:" + track.id);

                            String artist_name = "";
                            if (track.singer_name != null) {
                                for (int j = 0; j < track.singer_name.size(); j++) {
                                    String singer = track.singer_name.get(j);
                                    artist_name = singer + " ";
                                }
                            }
                            channel.setSinger(artist_name);
                            channelList.add(channel);
                        }
                    }

                    if (channelList.size() > 0) {
                        final int position = (int)(Math.random() * channelList.size());
                        final Channel oldChannel = channelList.get(position);
                        oldChannel.playPause(context, oldChannel, new Channel.ChannelDeviceControlListenerCallBack() {
                            @Override
                            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                                if (obj.content != null) {
                                    if (code == PUT_OR_POST_SUCCESS) {
                                        Channel channel = oldChannel;
                                        if (oldChannel.getUri().contains("baidu")) {
                                            if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(context, "Mantic", "baiduTempDuration"))) {
                                                oldChannel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(context, "Mantic", "baiduTempDuration")));
                                            }
                                        }

                                        channel.setPlayState(PLAY_STATE_PLAYING);
                                        mDataFactory.addRecentPlay(context, channel);

                                        mDataFactory.setCurrChannel(channel);
                                        channelList.set(position, channel);
                                        int size = channelList.size();
                                        for (int i = position; i < size; i++) {
                                            if (i != position) {
                                                channelList.get(i).setPlayState(PLAY_STATE_STOP);
                                                channelList.set(i, channelList.get(i));
                                            }
                                        }

                                        if (mDataFactory != null && channel.getPlayState() == PLAY_STATE_PLAYING) {
                                            ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                                                @Override
                                                public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                                    if (response.isSuccessful()) {
                                                        if (null == response.errorBody()) {
                                                            SharePreferenceUtil.setSharePreferenceData(context, "Mantic", "isAutoMaticPlay", "false");
                                                            List<AddResult> result = response.body().getResult();

                                                            List<Channel> channels = new ArrayList<Channel>();
                                                            for (int i = 0; i < result.size(); i++) {
                                                                channelList.get(i).setTlid(result.get(i).getTlid());
                                                                channels.add(channelList.get(i));
                                                            }

                                                            mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                                            mDataFactory.notifyBeingPlayListChange();
                                                            mDataFactory.notifyMyLikeMusicStatusChange();
                                                            mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());


                                                            ArrayList<DataFactory.ChannelControlListener> channelControlListeners = mDataFactory.getChannelControlListeners();
                                                            for (int i = 0; i < channelControlListeners.size(); i++) {
                                                                DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
                                                                listener.afterChannelControl();
                                                                listener.beginChannelControl(position);
                                                            }

                                                            callback.callback();
                                                      /*      new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    ((MainActivity) mActivity).setBottomSheetExpanded();
                                                                    ((MainActivity) mActivity).setFmChannel(false);
                                                                }
                                                            }, 150);*/

                                                        } else {
                                                            mDataFactory.notifyOperatorResult("设置播放列表失败", false);
                                                        }

                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {

                                                }
                                            }, channelList, context);
                                        }
                                    }


                                }
                            }

                            @Override
                            public void onFailed(HttpStatus code) {

                            }

                            @Override
                            public void onError(IoTException error) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onFailure(Call<BaiduTrackList> call, Throwable t) {
                Glog.i("getTrackListCall", t.toString());
            }
        });
    }
}
