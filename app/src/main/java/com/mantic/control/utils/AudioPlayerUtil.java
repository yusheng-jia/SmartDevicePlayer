package com.mantic.control.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.AudioPlayer;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayInsertRsBean;
import com.mantic.control.api.channelplay.bean.ChannelPlayListMoveRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.listener.DeviceStateController;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.widget.CustomDialog;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.iot.sdk.HttpStatus.PUT_OR_POST_SUCCESS;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;

/**
 * Created by lin on 2017/6/13.
 */

public class AudioPlayerUtil {

    public final static int NEXT_INSERT = 1;
    public final static int LAST_INSERT = 2;

    public static void playByPosition(final Context mContext, final DataFactory mDataFactory, final int position, final Context context) {
        int currChannelIndex = mDataFactory.getCurrChannelIndex();
        if (position != currChannelIndex) {
            final Channel channel = mDataFactory.getBeingPlayList().get(position);

           Glog.i("AudioPlayerUtil", "channel.getTlid = " + channel.getTlid());
            channel.playPause(mContext, channel, new Channel.ChannelDeviceControlListenerCallBack() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    Glog.i("AudioPlayerUtil", "HttpStatus: " + code + " name: " + obj.name + " context: " + obj.content);
                    if (code == PUT_OR_POST_SUCCESS) {
                        ChannelPlayListManager.getInstance().setCurrentChannelPlay(new Callback<SetCurrentChannelPlayRsBean>() {
                            @Override
                            public void onResponse(Call<SetCurrentChannelPlayRsBean> call, Response<SetCurrentChannelPlayRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {

                                    if (channel.getUri().contains("baidu")) {
                                        if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(context, "Mantic", "baiduTempDuration"))) {
                                            channel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(context, "Mantic", "baiduTempDuration")));
                                        }
                                    }
                                    channel.setPlayState(PLAY_STATE_PLAYING);
                                    mDataFactory.setCurrChannel(channel);
                                    mDataFactory.addRecentPlay(mContext, channel);
                                    mDataFactory.getBeingPlayList().set(position, channel);
                                    Glog.i("AudioPlayerUtil", " get state: " + channel.getPlayState() + " pos: " + position);
                                    int size = mDataFactory.getBeingPlayList().size();
                                    for (int i = 0; i < size; i++) {
                                        if (i != position) {
                                            mDataFactory.getBeingPlayList().get(i).setPlayState(PLAY_STATE_STOP);
                                            mDataFactory.getBeingPlayList().set(i, mDataFactory.getBeingPlayList().get(i));

                                        }
                                    }

                                    mDataFactory.notifyBeingPlayListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                    mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                                }
                            }

                            @Override
                            public void onFailure(Call<SetCurrentChannelPlayRsBean> call, Throwable t) {

                            }
                        }, channel.getTlid(), context);

                    }
                }

                @Override
                public void onFailed(HttpStatus code) {

                }

                @Override
                public void onError(IoTException error) {

                }
            });
        } else {

        }
    }


    public static void playCode(final DataFactory mDataFactory, final int playCode, final Activity mActivity) {
        playCodeByPlayScrZero(mActivity, null, playCode);
    }

    public static void insertChannel(final DataFactory mDataFactory,int code, final Context mContext, final  List<Channel> channelList) {
        switch (code) {
            case NEXT_INSERT:
                if (Util.beingListTypeIsRadio(mDataFactory, mContext)) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.current_play_list_is_broadcast_next), false);
                    break;
                }

                if (null == channelList || channelList.size() <= 0) {
//                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.no_support_add_album_to_next_play), false);
                    break;
                }
                final Channel channel = channelList.get(0);

                if (mDataFactory.channelIsCurrent(channel)) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.the_song_played_cannot_add_to_the_next_one), false);
                    break;
                }

                Channel nextPlayChannel = mDataFactory.getNextPlayChannel();
                if (null != nextPlayChannel && nextPlayChannel.getName().equals(channel.getName())//如果已经是下一首了
                        && nextPlayChannel.getUri().equals(channel.getUri())) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.the_song_added_is_already_the_next_one), false);
                    break;
                }


                if (mDataFactory.channelIsInBeingPlayList(channel)) {
                    final int index = mDataFactory.channelPositionInBeingPlayList(channel);
                    if (index != -1) {
                        ChannelPlayListManager.getInstance().changeChannelPlayListPosition(new Callback<ChannelPlayListMoveRsBean>() {
                            @Override
                            public void onResponse(Call<ChannelPlayListMoveRsBean> call, Response<ChannelPlayListMoveRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    ((ManticApplication)mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.already_add_to_the_next_one), true);
                                    ArrayList<Channel> beingPlayList = mDataFactory.getBeingPlayList();
                                    beingPlayList.remove(index);
                                    beingPlayList.add(mDataFactory.getCurrChannelIndex() + 1, channel);
                                    mDataFactory.setBeingPlayList(beingPlayList);
                                    mDataFactory.notifyBeingPlayListChange();
                                } else {
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                                }
                            }

                            @Override
                            public void onFailure(Call<ChannelPlayListMoveRsBean> call, Throwable t) {
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                            }
                        }, mContext, index, index + 1, mDataFactory.getCurrChannelIndex() + 1);
                    }

                    break;
                }

                if (null == mDataFactory.getBeingPlayList() || mDataFactory.getBeingPlayList().size() == 0) {//如果播放列表为空，则重置播放列表
                    ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                        @Override
                        public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                ((ManticApplication)mContext.getApplicationContext()).setPlaySrcZero(false);
                                ((ManticApplication) mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                List<AddResult> result = response.body().getResult();

                                List<Channel> channels = new ArrayList<Channel>();
                                for (int i = 0; i < result.size(); i++) {
                                    channelList.get(i).setTlid(result.get(i).getTlid());
                                    channels.add(channelList.get(i));
                                }

                                mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                mDataFactory.setCurrChannel(channels.get(0));
                                mDataFactory.notifyBeingPlayListChange();
                                mDataFactory.notifyMyLikeMusicStatusChange();
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.already_add_to_the_next_one), true);
                            } else {
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                        }
                    }, channelList, mContext);
                } else if (null != mDataFactory.getCurrChannel() &&
                        (mDataFactory.getCurrChannel().getUri().contains(":audio:") || ((ManticApplication)mContext.getApplicationContext()).isPlaySrcZero())) {
                    CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
                    mBuilder.setTitle(mContext.getString(R.string.the_playlist_was_edited));
                    mBuilder.setMessage(mContext.getString(R.string.insert_this_play_will_replace_the_playlist));
                    mBuilder.setPositiveButton(mContext.getString(R.string.play), new CustomDialog.Builder.DialogPositiveClickListener() {
                        @Override
                        public void onPositiveClick(CustomDialog dialog) {
                            dialog.dismiss();

                            ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                                @Override
                                public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                    if (response.isSuccessful() && null == response.errorBody()) {
                                        ((ManticApplication)mContext.getApplicationContext()).setPlaySrcZero(false);
                                        ((ManticApplication) mContext.getApplicationContext()).setIsReplacePlayingList(false);
                                        List<AddResult> result = response.body().getResult();

                                        List<Channel> channels = new ArrayList<Channel>();
                                        for (int i = 0; i < result.size(); i++) {
                                            channelList.get(i).setTlid(result.get(i).getTlid());
                                            channels.add(channelList.get(i));
                                        }

                                        final Channel newChannel = channels.get(0);
                                        final List<Channel> newchannels = channels;
                                        newChannel.playPause(mContext, newChannel, new Channel.ChannelDeviceControlListenerCallBack() {
                                            @Override
                                            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                                                newChannel.setPlayState(PLAY_STATE_PLAYING);
                                                mDataFactory.addRecentPlay(mContext, newChannel);
                                                mDataFactory.setBeingPlayList((ArrayList<Channel>) newchannels);
                                                mDataFactory.setCurrChannel(newChannel);
                                                mDataFactory.notifyBeingPlayListChange();
                                                mDataFactory.notifyMyLikeMusicStatusChange();
                                                mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                                            }

                                            @Override
                                            public void onFailed(HttpStatus code) {
                                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.play_failed), false);
                                            }

                                            @Override
                                            public void onError(IoTException error) {
                                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.play_failed), false);
                                            }
                                        });
                                    } else {
                                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                                }
                            }, channelList, mContext);
                        }
                    });
                    mBuilder.setNegativeButton(mContext.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                        @Override
                        public void onNegativeClick(CustomDialog dialog) {
                            dialog.dismiss();

                        }
                    });
                    CustomDialog customDialog = mBuilder.create();
                    customDialog.show();
                } else {
                    ChannelPlayListManager.getInstance().insertChannelPlayList(new Callback<ChannelPlayInsertRsBean>() {
                        @Override
                        public void onResponse(Call<ChannelPlayInsertRsBean> call, Response<ChannelPlayInsertRsBean> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                ((ManticApplication)mContext.getApplicationContext()).setPlaySrcZero(false);
                                ((ManticApplication) mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                channel.setTlid(response.body().getResult().get(0).getTlid());
                                if (null == mDataFactory.getBeingPlayList() || mDataFactory.getBeingPlayList().size() == 0) {
                                    mDataFactory.setCurrChannel(channel);
                                }
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.already_add_to_the_next_one), true);
                                mDataFactory.getBeingPlayList().add(mDataFactory.getCurrChannelIndex() + 1, channel);
                                mDataFactory.notifyBeingPlayListChange();
                            } else {
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ChannelPlayInsertRsBean> call, Throwable t) {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                        }
                    }, channelList, mContext, mDataFactory.getCurrChannelIndex() + 1);
                }
                break;

            case LAST_INSERT:

                if (Util.beingListTypeIsRadio(mDataFactory, mContext)) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.current_play_list_is_broadcast_last), false);
                    break;
                }

                if (null == channelList || channelList.size() <= 0) {
//                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.no_support_add_album_to_last_play), false);
                    break;
                }
                final Channel channel1 = channelList.get(0);

                if (mDataFactory.channelIsCurrent(channel1)) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.the_song_played_cannot_add_to_the_last_one), false);
                    break;
                }

                Channel lastPlayChannel = mDataFactory.getLastPlayChannel();
                if (null != lastPlayChannel && lastPlayChannel.getName().equals(channel1.getName())//如果已经是最后一首了
                        && lastPlayChannel.getUri().equals(channel1.getUri())) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.the_song_added_is_already_the_last_one), false);
                    break;
                }


                if (mDataFactory.channelIsInBeingPlayList(channel1)) {
                    final int index = mDataFactory.channelPositionInBeingPlayList(channel1);
                    if (index != -1) {
                        ChannelPlayListManager.getInstance().changeChannelPlayListPosition(new Callback<ChannelPlayListMoveRsBean>() {
                            @Override
                            public void onResponse(Call<ChannelPlayListMoveRsBean> call, Response<ChannelPlayListMoveRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    ((ManticApplication)mContext.getApplicationContext()).setPlaySrcZero(false);
                                    ((ManticApplication)mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.already_add_to_the_last_one), true);
                                    ArrayList<Channel> beingPlayList = mDataFactory.getBeingPlayList();
                                    beingPlayList.remove(index);
                                    beingPlayList.add(channel1);
                                    mDataFactory.setBeingPlayList(beingPlayList);
                                    mDataFactory.notifyBeingPlayListChange();
                                } else {
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_last_one), false);
                                }
                            }

                            @Override
                            public void onFailure(Call<ChannelPlayListMoveRsBean> call, Throwable t) {
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_last_one), false);
                            }
                        }, mContext, index, index + 1, mDataFactory.getBeingPlayList().size());
                    }

                    break;
                }

                if (null == mDataFactory.getBeingPlayList() || mDataFactory.getBeingPlayList().size() == 0) {//如果播放列表为空，则重置播放列表
                    ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                        @Override
                        public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                if (null == response.errorBody()) {
                                    ((ManticApplication) mContext.getApplicationContext()).setPlaySrcZero(false);
                                    ((ManticApplication) mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                    List<AddResult> result = response.body().getResult();

                                    List<Channel> channels = new ArrayList<Channel>();
                                    for (int i = 0; i < result.size(); i++) {
                                        channelList.get(i).setTlid(result.get(i).getTlid());
                                        channels.add(channelList.get(i));
                                    }

                                    mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                    mDataFactory.setCurrChannel(channels.get(0));
                                    mDataFactory.notifyBeingPlayListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.already_add_to_the_last_one), true);
                                } else {
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_last_one), false);
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_last_one), false);
                        }
                    }, channelList, mContext);
                } else if (null != mDataFactory.getCurrChannel() &&
                        (mDataFactory.getCurrChannel().getUri().contains(":audio:") || ((ManticApplication)mContext.getApplicationContext()).isPlaySrcZero())) {
                    CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
                    mBuilder.setTitle(mContext.getString(R.string.the_playlist_was_edited));
                    mBuilder.setMessage(mContext.getString(R.string.insert_this_play_will_replace_the_playlist));
                    mBuilder.setPositiveButton(mContext.getString(R.string.play), new CustomDialog.Builder.DialogPositiveClickListener() {
                        @Override
                        public void onPositiveClick(CustomDialog dialog) {
                            dialog.dismiss();

                            ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                                @Override
                                public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                    if (response.isSuccessful() && null == response.errorBody()) {
                                        ((ManticApplication)mContext.getApplicationContext()).setPlaySrcZero(false);
                                        ((ManticApplication) mContext.getApplicationContext()).setIsReplacePlayingList(false);
                                        List<AddResult> result = response.body().getResult();

                                        List<Channel> channels = new ArrayList<Channel>();
                                        for (int i = 0; i < result.size(); i++) {
                                            channelList.get(i).setTlid(result.get(i).getTlid());
                                            channels.add(channelList.get(i));
                                        }

                                        final Channel newChannel = channels.get(0);
                                        final List<Channel> newchannels = channels;
                                        newChannel.playPause(mContext, newChannel, new Channel.ChannelDeviceControlListenerCallBack() {
                                            @Override
                                            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                                                newChannel.setPlayState(PLAY_STATE_PLAYING);
                                                mDataFactory.addRecentPlay(mContext, newChannel);
                                                mDataFactory.setBeingPlayList((ArrayList<Channel>) newchannels);
                                                mDataFactory.setCurrChannel(newChannel);
                                                mDataFactory.notifyBeingPlayListChange();
                                                mDataFactory.notifyMyLikeMusicStatusChange();
                                                mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());
                                            }

                                            @Override
                                            public void onFailed(HttpStatus code) {
                                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.play_failed), false);
                                            }

                                            @Override
                                            public void onError(IoTException error) {
                                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.play_failed), false);
                                            }
                                        });
                                    } else {
                                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {
                                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_next_one), false);
                                }
                            }, channelList, mContext);
                        }
                    });
                    mBuilder.setNegativeButton(mContext.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                        @Override
                        public void onNegativeClick(CustomDialog dialog) {
                            dialog.dismiss();

                        }
                    });
                    CustomDialog customDialog = mBuilder.create();
                    customDialog.show();
                } else {
                    ChannelPlayListManager.getInstance().insertChannelPlayList(new Callback<ChannelPlayInsertRsBean>() {
                        @Override
                        public void onResponse(Call<ChannelPlayInsertRsBean> call, Response<ChannelPlayInsertRsBean> response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                ((ManticApplication)mContext.getApplicationContext()).setPlaySrcZero(false);
                                ((ManticApplication)mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                channel1.setTlid(response.body().getResult().get(0).getTlid());
                                if (null == mDataFactory.getBeingPlayList() || mDataFactory.getBeingPlayList().size() == 0) {
                                    mDataFactory.setCurrChannel(channel1);
                                }
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.already_add_to_the_last_one), true);
                                mDataFactory.getBeingPlayList().add(mDataFactory.getBeingPlayList().size(), channel1);
                                mDataFactory.notifyBeingPlayListChange();
                            } else {
                                mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_last_one), false);
                            }
                        }

                        @Override
                        public void onFailure(Call<ChannelPlayInsertRsBean> call, Throwable t) {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_add_to_the_last_one), false);
                        }
                    }, channelList, mContext, mDataFactory.getBeingPlayList().size());
                }
                break;

            default:
                break;
        }
    }


    public static void playOrPause(final Context mContext, final Channel channel, final View view, final Activity mActivity, final DataFactory mDataFactory) {
        Channel currChannel = mDataFactory.getCurrChannel();
        if (null != currChannel && currChannel.getName().equals(channel.getName()) && currChannel.getUri().equals(channel.getUri())) {
            ((MainActivity) mActivity).setBottomSheetExpanded();
            if (null != view) {
                view.setClickable(true);
            }
            return;
        }

        channel.playPause(mContext, channel, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (null != view) {
                    view.setClickable(true);
                }
                if (obj.content != null) {
                    if (code == PUT_OR_POST_SUCCESS) {
                        Channel tempChannel = channel;

                        if (tempChannel.getUri().contains("baidu")) {
                            if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration"))) {
                                tempChannel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(mActivity, "Mantic", "baiduTempDuration")));
                            }
                        }
                        final Channel newChannel = tempChannel;

                        newChannel.setPlayState(PLAY_STATE_PLAYING);
                        mDataFactory.addRecentPlay(mContext, newChannel);
                        mDataFactory.setCurrChannel(newChannel);
                        final ArrayList<Channel> beingPlayList = mDataFactory.getBeingPlayList();
                        if (mDataFactory.channelIsInBeingPlayList(newChannel)) {//如果播放列表存在当前点击播放的歌曲就不替换播放列表
                            final int index = mDataFactory.channelPositionInBeingPlayList(newChannel);
                            newChannel.setTlid(mDataFactory.getBeingPlayList().get(index).getTlid());
                            ChannelPlayListManager.getInstance().setCurrentChannelPlay(new Callback<SetCurrentChannelPlayRsBean>() {
                                @Override
                                public void onResponse(Call<SetCurrentChannelPlayRsBean> call, Response<SetCurrentChannelPlayRsBean> response) {
                                    if (response.isSuccessful() && null == response.errorBody()) {
                                        for (int i = 0; i < beingPlayList.size(); i++) {
                                            Channel channel1 = beingPlayList.get(i);
                                            if (channel1.getName().equals(newChannel.getName()) && channel1.getUri().equals(newChannel.getUri())) {
                                                channel1.setPlayState(PLAY_STATE_PLAYING);
                                            } else {
                                                channel1.setPlayState(PLAY_STATE_STOP);
                                            }
                                            beingPlayList.set(i, channel1);
                                        }

                                        mDataFactory.setBeingPlayList(beingPlayList);
                                        mDataFactory.notifyBeingPlayListChange();
                                        mDataFactory.notifyMyLikeMusicStatusChange();
                                        mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());

                                        ArrayList<DataFactory.ChannelControlListener> channelControlListeners = mDataFactory.getChannelControlListeners();
                                        for (int i = 0; i < channelControlListeners.size(); i++) {
                                            DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
                                            listener.afterChannelControl();
                                            listener.beginChannelControl(index);
                                        }

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                ((MainActivity) mActivity).setBottomSheetExpanded();
                                                ((MainActivity) mActivity).setFmChannel(false);
                                            }
                                        }, 150);
                                    }
                                }

                                @Override
                                public void onFailure(Call<SetCurrentChannelPlayRsBean> call, Throwable t) {

                                }
                            }, newChannel.getTlid(), mActivity);

                        } else {
                            final ArrayList<Channel> list = new ArrayList<Channel>();
                            list.add(newChannel);
                            if (Util.beingListTypeIsRadio(mDataFactory, mActivity) || null == mDataFactory.getBeingPlayList()
                                    || mDataFactory.getBeingPlayList().size() == 0 || ((ManticApplication)mActivity.getApplication()).isPlaySrcZero()) {
                                ChannelPlayListManager.getInstance().updateChannelPlayList(new Callback<ChannelPlayAddRsBean>() {
                                    @Override
                                    public void onResponse(Call<ChannelPlayAddRsBean> call, Response<ChannelPlayAddRsBean> response) {
                                        if (response.isSuccessful() && null == response.errorBody()) {
                                            ((ManticApplication)mActivity.getApplication()).setPlaySrcZero(false);
                                            ((MainActivity) mActivity).setFmChannel(false);
                                            List<AddResult> result = response.body().getResult();
                                            List<Channel> channels = new ArrayList<Channel>();
                                            for (int i = 0; i < result.size(); i++) {
                                                list.get(i).setTlid(result.get(i).getTlid());
                                                channels.add(list.get(i));
                                            }
                                            mDataFactory.setBeingPlayList((ArrayList<Channel>) channels);
                                            mDataFactory.notifyBeingPlayListChange();
                                            mDataFactory.notifyMyLikeMusicStatusChange();
                                            mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((MainActivity) mActivity).setBottomSheetExpanded();
                                                    ((MainActivity) mActivity).setFmChannel(false);
                                                }
                                            }, 150);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {

                                    }
                                }, list, mActivity);
                            } else {
                                ChannelPlayListManager.getInstance().insertChannelPlayList(new Callback<ChannelPlayInsertRsBean>() {
                                    @Override
                                    public void onResponse(Call<ChannelPlayInsertRsBean> call, Response<ChannelPlayInsertRsBean> response) {
                                        if (response.isSuccessful() && null == response.errorBody()) {
                                            for (int i = 0; i < beingPlayList.size(); i++) {
                                                Channel channel1 = beingPlayList.get(i);
                                                channel1.setPlayState(PLAY_STATE_STOP);
                                                beingPlayList.set(i, channel1);
                                            }
                                            ((MainActivity) mActivity).setFmChannel(false);
                                            newChannel.setTlid(response.body().getResult().get(0).getTlid());
                                            beingPlayList.add(0, newChannel);
                                            mDataFactory.setBeingPlayList(beingPlayList);
                                            mDataFactory.notifyBeingPlayListChange();
                                            mDataFactory.notifyMyLikeMusicStatusChange();
                                            mDataFactory.notifyRecentPlayListChange(mDataFactory.getRecentPlayList());

                                            ArrayList<DataFactory.ChannelControlListener> channelControlListeners = mDataFactory.getChannelControlListeners();
                                            for (int i = 0; i < channelControlListeners.size(); i++) {
                                                DataFactory.ChannelControlListener listener = channelControlListeners.get(i);
                                                listener.afterChannelControl();
                                                listener.beginChannelControl(0);
                                            }


                                            ChannelPlayListManager.getInstance().setCurrentChannelPlay(new Callback<SetCurrentChannelPlayRsBean>() {
                                                @Override
                                                public void onResponse(Call<SetCurrentChannelPlayRsBean> call, Response<SetCurrentChannelPlayRsBean> response) {
                                                    if (response.isSuccessful() && null == response.errorBody()) {
                                                        ((MainActivity) mActivity).setBottomSheetExpanded();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<SetCurrentChannelPlayRsBean> call, Throwable t) {

                                                }
                                            }, newChannel.getTlid(), mActivity);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ChannelPlayInsertRsBean> call, Throwable t) {

                                    }
                                }, list, mActivity, 0);
                            }
                        }

                    }
                }
            }

            @Override
            public void onFailed(HttpStatus code) {
                if (null != view) {
                    view.setClickable(true);
                }
            }

            @Override
            public void onError(IoTException error) {
                if (null != view) {
                    view.setClickable(true);
                }
            }
        });
    }


    private static void aaa(Activity mActivity) {
        CustomDialog.Builder mBuilder = new CustomDialog.Builder(mActivity);
        mBuilder.setTitle(mActivity.getString(R.string.the_playlist_was_edited));
        mBuilder.setMessage(mActivity.getString(R.string.insert_this_play_will_replace_the_playlist));
        mBuilder.setPositiveButton(mActivity.getString(R.string.play), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomDialog dialog) {

            }
        });
        mBuilder.setNegativeButton(mActivity.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                dialog.dismiss();

            }
        });
        CustomDialog customDialog = mBuilder.create();
        customDialog.show();
    }

    public static void playCodeByPlayScrZero(final Context context, final Channel.ChannelDeviceControlListenerCallBack callBack, int code) {
        final Channel.ChannelDeviceControlListener<ControlResult> listener = new Channel.ChannelDeviceControlListener<ControlResult>();
        Channel.ChannelDeviceControlListenerCallBack currCallBack = new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (callBack != null) {
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
                if(callBack != null) {
                    callBack.onError(error);
                }
            }
        };
        listener.setChannelDeviceControlListenerCallBack(currCallBack);

        switch (code) {
            case AudioPlayer.PLAY_PRE:
                DeviceStateController.getInstance(context, SharePreferenceUtil.getDeviceId(context)).sendPreMusic(listener);
                break;
            case AudioPlayer.PLAY_NEXT:
                DeviceStateController.getInstance(context, SharePreferenceUtil.getDeviceId(context)).sendNextMusic(listener);
                break;
            case AudioPlayer.PLAY_COOMAAN_PRE:
                DeviceStateController.getInstance(context, SharePreferenceUtil.getDeviceId(context)).sendCoomaanPreMusic(listener);
                break;
            case AudioPlayer.PLAY_COOMAAN_NEXT:
                DeviceStateController.getInstance(context, SharePreferenceUtil.getDeviceId(context)).sendCoomaanNextMusic(listener);
                break;
        }
    }
}
