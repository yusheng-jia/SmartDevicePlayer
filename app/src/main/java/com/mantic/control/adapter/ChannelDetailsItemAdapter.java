package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.beiwa.bean.BwWordsBean;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.api.mylike.bean.MyLikeAddRsBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.manager.MyLikeManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.SleepTimeSetUtils;
import com.mantic.control.utils.TextUtil;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.SpectrumAnimatorTextView;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.track.BatchTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.iot.sdk.HttpStatus.PUT_OR_POST_SUCCESS;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;

/**
 * Created by Jia on 2017/6/5.
 */

public class ChannelDetailsItemAdapter extends RecyclerView.Adapter {
    private static final String TAG = "ChannelDetailsItemAdapter";
    private Context ctx;
    private Activity mActivity;
    private RecyclerView recyclerView;
    private CustomDialog.Builder mBuilder;
    private boolean isFmChannel = false;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private static final int TYPE_FOOTER_COMPLETE = 2;  //说明是带有Footer的
    private static final int TYPE_FOOTER_EMPTY = 4;  //说明是带有Footer的
    private static final int TYPE_HEADER = 3;  //说明是带有头部的


    //private Subscriber<ArrayList<DataFactory.Channel>> beingPlayAudioListSubscriber;

    private DataFactory.ChannelControlListener channelControlListener;
    private DataFactory.OnMyLikeMusicStateListener mMyLikeMusicStateListener;

    private DataFactory mDataFactory;
    private List<Channel> channelList = new ArrayList<Channel>();


    private View mHeaderView;

    public interface OnItemMoreClickListener {
        void moreClickListener(int position);
    }

    private OnItemMoreClickListener onItemMoreClickListener;

    public void setOnItemMoreClickListener(OnItemMoreClickListener onItemMoreClickListener) {
        this.onItemMoreClickListener = onItemMoreClickListener;
    }

    private int currentItemViewTpye = TYPE_FOOTER;

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (position + 1 == getItemCount()) {
            return currentItemViewTpye;
        } else {
            return TYPE_NORMAL;
        }
    }

    public void setHeaderView(View headerView) {
        mHeaderView = headerView;
        notifyItemInserted(0);
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    @Override
    public int getItemCount() {
        return ((null == channelList || channelList.size() == 0) ? 1 : channelList.size() + 2);
    }


//    private void updateMyChannelList() {
//        for (int i = 0; i < channelList.size(); i++) {
//            Glog.i(TAG, "updateMyChannelList: " + channelList.get(i).getName());
//            if (channelList.get(i).getName().equals(mDataFactory.getCurrChannel().getName()) && channelList.get(i).getUri().equals(mDataFactory.getCurrChannel().getUri())) {
//                channelList.get(i).setPlayState(mDataFactory.getCurrChannel().getPlayState());
//            } else {
//                channelList.get(i).setPlayState(PLAY_STATE_STOP);
//            }
//        }
//
////        setChannelList(channelList);
//    }


    public ChannelDetailsItemAdapter(Context context, Activity mActivity, List<Channel> channels, View mHeaderView, boolean isFmChannel) {
        this.ctx = context;
        this.mActivity = mActivity;
        this.isFmChannel = isFmChannel;
        mDataFactory = DataFactory.newInstance(ctx);
        if (null != channels) {
            this.channelList = channels;
        }

        if (null == mHeaderView) {
            throw new RuntimeException("HeaderView can't be null!");
        }
        this.mHeaderView = mHeaderView;

        this.channelControlListener = new DataFactory.ChannelControlListener() {
            @Override
            public void preChannelControl() {
                recyclerView.setClickable(false);
            }

            @Override
            public void afterChannelControl() {
                recyclerView.setClickable(true);
            }

            @Override
            public void beginChannelControl(int index) {

            }
        };

        this.mMyLikeMusicStateListener = new DataFactory.OnMyLikeMusicStateListener() {
            @Override
            public void changeMyLikeMusicState() {
                updateMyLikeBtnStatus(recyclerView);
            }
        };
    }

    public ChannelDetailsItemAdapter(Context context, List<Channel> channels, Activity activity ,boolean isFmChannel) {
        this.isFmChannel = isFmChannel;
        this.ctx = context;
        mActivity = activity;
        mDataFactory = DataFactory.newInstance(ctx);
        if (null != channels) {
            this.channelList = channels;
        }


        this.channelControlListener = new DataFactory.ChannelControlListener() {
            @Override
            public void preChannelControl() {
                recyclerView.setClickable(false);
            }

            @Override
            public void afterChannelControl() {
                recyclerView.setClickable(true);
            }

            @Override
            public void beginChannelControl(int index) {

            }
        };

        this.mMyLikeMusicStateListener = new DataFactory.OnMyLikeMusicStateListener() {
            @Override
            public void changeMyLikeMusicState() {
                updateMyLikeBtnStatus(recyclerView);
            }
        };
    }


    private void updateMyLikeBtnStatus(RecyclerView recyclerView) {
        for (int i = 0; i < channelList.size(); i++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i + 1);
            Channel channel = channelList.get(i);
            if (null == channel) {
                return;
            }
            if (viewHolder instanceof ChannelDetailsItemViewHolder) {
                if (mDataFactory.isExistMyLikeMusic(channel)) {
                    ((ChannelDetailsItemViewHolder) viewHolder).iv_item_like.setImageResource(R.drawable.audio_player_love_btn_pre);
                } else {
                    ((ChannelDetailsItemViewHolder) viewHolder).iv_item_like.setImageResource(R.drawable.audio_player_love_btn_nor);
                }
            }
        }
    }


    public void showLoadMore() {
        currentItemViewTpye = TYPE_FOOTER;
        notifyItemChanged(getItemCount());
    }

    public void showLoadComplete() {
        currentItemViewTpye = TYPE_FOOTER_COMPLETE;
        notifyItemChanged(getItemCount());
    }

    public void showLoadEmpty() {
        currentItemViewTpye = TYPE_FOOTER_EMPTY;
        notifyItemChanged(getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderView != null) {
            return new HeaderViewHolder(mHeaderView);
        } else if (viewType == TYPE_NORMAL) {
            return new ChannelDetailsItemViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.channel_details_item, parent, false), this.ctx);
        } else if (viewType == TYPE_FOOTER) {
            return new FootViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot, parent, false));
        } else if (viewType == TYPE_FOOTER_COMPLETE) {
            return new FootViewNoHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot_complete, parent, false));
        } else if (viewType == TYPE_FOOTER_EMPTY) {
            return new FootViewNoHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot_empty, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ChannelDetailsItemViewHolder) {
            ((ChannelDetailsItemViewHolder) holder).showDetailsItem(position - 1);

            final Channel channel = channelList.get(position - 1);
            ((ChannelDetailsItemViewHolder) holder).channel_details_item_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ((ChannelDetailsItemViewHolder) holder).channel_details_item_index.setVisibility(View.INVISIBLE);
                            break;

                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (mDataFactory.channelIsCurrent(channel) && (channel.getPlayState() == Channel.PLAY_STATE_PLAYING) && !channel.getUri().contains("radio")) {
                                ((ChannelDetailsItemViewHolder) holder).channel_details_item_index.setVisibility(View.VISIBLE);
                            }
                            break;
                    }

                    return false;
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
        //mDataFactory.registerBeingPlayListSubscriber(this.beingPlayAudioListSubscriber);
        Glog.i(TAG, "onAttachedToRecyclerView");

        mDataFactory.addChannelControlListener(this.channelControlListener);
        mDataFactory.registerMyLikeMusiceStatusListener(mMyLikeMusicStateListener);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
        //mDataFactory.unRegisterBeingPlayListSubscriber(this.beingPlayAudioListSubscriber);
        mDataFactory.registerMyLikeMusiceStatusListener(mMyLikeMusicStateListener);
        if (this.channelControlListener != null) {
            mDataFactory.removeChannelControlListener(this.channelControlListener);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
//            Glog.i(TAG,"ChannelDetailsItemAdapter onViewRecycled");
        this.hideSpectrum(holder);
    }

    private void hideSpectrum(RecyclerView.ViewHolder holder) {
        if (holder instanceof ChannelDetailsItemViewHolder) {
            ((ChannelDetailsItemViewHolder) holder).hideSpectrum();
        }
    }

    public void onStart() {
        for (int i = 0; i < channelList.size(); i++) {
            Channel currChannel = channelList.get(i);
            if (currChannel.getPlayState() == PLAY_STATE_PLAYING) {
                if (!isFooterView(i)) {
                    ChannelDetailsItemViewHolder viewHolder = (ChannelDetailsItemViewHolder) this.recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder != null) {
                        viewHolder.showSpectrum();
                    }
                }
                break;
            }
        }
    }

    public void setChannelList(List<Channel> channelList) {
//        if (null != channelList) {
        this.channelList = channelList;
        this.notifyDataSetChanged();
//        }
    }

    public void onStop() {
        for (int i = 0; i < this.getItemCount(); i++) {
            if (!isFooterView(i)) {
                ChannelDetailsItemViewHolder viewHolder = (ChannelDetailsItemViewHolder) this.recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    viewHolder.hideSpectrum();
                }
            }
        }
    }

    private boolean isFooterView(int currentItem) {
        return currentItem == getItemCount() - 1 || currentItem == 0;
    }

    public void playPause(final int position, final View view) {
        final Channel channel = channelList.get(position);

        if (mDataFactory.channelIsCurrent(channel)) {
            ((MainActivity) mActivity).setBottomSheetExpanded();
            if (null != view) {
                view.setClickable(true);
            }
            return;
        }

        if (!mDataFactory.channelIsInBeingPlayList(channel)) {
            if (((ManticApplication) mActivity.getApplication()).isReplacePlayingList()) {
                mBuilder = new CustomDialog.Builder(ctx);
                mBuilder.setTitle(ctx.getString(R.string.the_playlist_was_edited));
                mBuilder.setMessage(ctx.getString(R.string.this_play_will_replace_the_playlist));
                mBuilder.setPositiveButton(ctx.getString(R.string.play), new CustomDialog.Builder.DialogPositiveClickListener() {
                    @Override
                    public void onPositiveClick(CustomDialog dialog) {
                        dialog.dismiss();
                        resetBeingPlayList(channel, view, position);
                    }
                });
                mBuilder.setNegativeButton(ctx.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                    @Override
                    public void onNegativeClick(CustomDialog dialog) {
                        dialog.dismiss();
                        if (null != view) {
                            view.setClickable(true);
                        }
                    }
                });
                CustomDialog customDialog = mBuilder.create();

                customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (null != view) {
                            view.setClickable(true);
                        }
                    }
                });
                customDialog.show();
            } else {
                resetBeingPlayList(channel, view, position);
            }
        } else {//如果播放列表存在当前点击播放的歌曲就不替换播放列表
            channel.playPause(ctx, channel, new Channel.ChannelDeviceControlListenerCallBack() {
                @Override
                public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                    if (null != view) {
                        view.setClickable(true);
                    }
                    Glog.i(TAG, "HttpStatus: " + code + " code " + obj.code + " name: " + obj.name + " context: " + obj.content);

                    if (obj.content != null) {
                        if (code == PUT_OR_POST_SUCCESS) {
                            Channel tempChannel = channel;

                            if (tempChannel.getUri().contains("baidu")) {
                                if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(ctx, "Mantic", "baiduTempDuration"))) {
                                    tempChannel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(ctx, "Mantic", "baiduTempDuration")));
                                }
                            }
                            final Channel newChannel = tempChannel;

                            ChannelPlayListManager.getInstance().setCurrentChannelPlay(new Callback<SetCurrentChannelPlayRsBean>() {
                                @Override
                                public void onResponse(Call<SetCurrentChannelPlayRsBean> call, Response<SetCurrentChannelPlayRsBean> response) {
                                    if (response.isSuccessful() && null == response.errorBody()) {
                                        newChannel.setPlayState(PLAY_STATE_PLAYING);
                                        mDataFactory.addRecentPlay(ctx, newChannel);
                                        mDataFactory.setCurrChannel(newChannel);
                                        channelList.set(position, newChannel);
                                        int size = channelList.size();
                                        for (int i = 0; i < size; i++) {
                                            if (i != position) {
                                                channelList.get(i).setPlayState(PLAY_STATE_STOP);
                                            }
                                        }

                                        ArrayList<Channel> beingPlayList = mDataFactory.getBeingPlayList();
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
                                            listener.beginChannelControl(position);
                                        }

                                        ((MainActivity) mActivity).setBottomSheetExpanded();
                                        Glog.i("getCurrChannel().getPlayState()", mDataFactory.getCurrChannel().getPlayState() + "");
                                    }
                                }

                                @Override
                                public void onFailure(Call<SetCurrentChannelPlayRsBean> call, Throwable t) {

                                }
                            }, newChannel.getTlid(), ctx);
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
    }

    private void resetBeingPlayList(final Channel oldChannel, final View view, final int position) {

        oldChannel.playPause(ctx, oldChannel, new Channel.ChannelDeviceControlListenerCallBack() {
            @Override
            public void onSuccess(HttpStatus code, ControlResult obj, PageInfo info) {
                if (null != view) {
                    view.setClickable(true);
                }
                Glog.i(TAG, "HttpStatus: " + code + " code " + obj.code + " name: " + obj.name + " context: " + obj.content);
                if (obj.content != null) {
                    if (code == PUT_OR_POST_SUCCESS) {
                        Channel channel = oldChannel;
                        if (oldChannel.getUri().contains("baidu")) {
                            if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(ctx, "Mantic", "baiduTempDuration"))) {
                                oldChannel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(ctx, "Mantic", "baiduTempDuration")));
                            }
                        }

                        channel.setPlayState(PLAY_STATE_PLAYING);
                        mDataFactory.addRecentPlay(ctx, channel);

                        mDataFactory.setCurrChannel(channel);
                        channelList.set(position, channel);
                        int size = channelList.size();
                        for (int i = 0; i < size; i++) {
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
                                            ((ManticApplication) mActivity.getApplication()).setIsReplacePlayingList(false);
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

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ((MainActivity) mActivity).setBottomSheetExpanded();
                                                    ((MainActivity) mActivity).setFmChannel(isFmChannel);
                                                }
                                            }, 150);

                                        } else {
                                            mDataFactory.notifyOperatorResult("设置播放列表失败", false);
                                        }

                                    }
                                }

                                @Override
                                public void onFailure(Call<ChannelPlayAddRsBean> call, Throwable t) {

                                }
                            }, channelList, ctx);
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



    private class ChannelDetailsItemViewHolder extends RecyclerView.ViewHolder {
        private Context ctx;
        //private TextView channel_details_item_index;
        private SpectrumAnimatorTextView channel_details_item_index;
        private ImageView iv_item_like;
        private ImageButton channel_details_item_more_btn;
        private TextView channel_details_item_name;
        private View channel_details_item_view;
        private TextView tv_album_sync_time;
        private TextView tv_album_duration;
        private LinearLayout ll_channel_details_item_album_info;
        private TextView time_periods;//FM
        private TextView fm_zhibo;//FM

        ChannelDetailsItemViewHolder(final View itemView, Context context) {
            super(itemView);
            this.ctx = context;
            //this.channel_details_item_index = (TextView) itemView.findViewById(R.id.channel_details_item_index);
            this.channel_details_item_index = (SpectrumAnimatorTextView) itemView.findViewById(R.id.channel_details_item_index);
            this.channel_details_item_name = (TextView) itemView.findViewById(R.id.channel_details_item_name);
            this.ll_channel_details_item_album_info = (LinearLayout) itemView.findViewById(R.id.ll_channel_details_item_album_info);
            this.channel_details_item_more_btn = (ImageButton) itemView.findViewById(R.id.channel_details_item_more_btn);
            this.tv_album_sync_time = (TextView) itemView.findViewById(R.id.tv_album_sync_time);
            this.tv_album_duration = (TextView) itemView.findViewById(R.id.tv_album_duration);
            this.channel_details_item_view = itemView.findViewById(R.id.channel_details_item_view);
            this.time_periods = (TextView) itemView.findViewById(R.id.time_periods);
            this.fm_zhibo = (TextView) itemView.findViewById(R.id.text_fm_zhibo);
            this.iv_item_like = (ImageView) itemView.findViewById(R.id.iv_item_like);
            this.channel_details_item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = recyclerView.getChildAdapterPosition(itemView) - 1;
                    if (channel_details_item_view.isClickable() && position != -1) {
                        if (isFmChannel) {
                            if (position == 0) {
                                channel_details_item_view.setClickable(false);
                                playPause(position, channel_details_item_view);
                            }
                        } else {
                            channel_details_item_view.setClickable(false);
                            playPause(position, channel_details_item_view);
                        }
                    }

                }
            });

            this.channel_details_item_more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemMoreClickListener) {
                        final int position = recyclerView.getChildAdapterPosition(itemView) - 1;
                        onItemMoreClickListener.moreClickListener(position);
                    }
                }
            });

            this.iv_item_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = recyclerView.getChildAdapterPosition(itemView) - 1;
                    final Channel channel = channelList.get(position);
                    if (mDataFactory.isExistMyLikeMusic(channel)) {
                        MyLikeManager.getInstance().deleteMyLike(new Callback<MyLikeDeleteRsBean>() {
                            @Override
                            public void onResponse(Call<MyLikeDeleteRsBean> call, Response<MyLikeDeleteRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    removeLikeMusic(channel);
                                    iv_item_like.setImageResource(R.drawable.audio_player_love_btn_nor);
                                    ToastUtils.showShortSafe(mActivity.getString(R.string.delete_favourite));
                                    mDataFactory.notifyMyLikeMusicListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                } else {
                                    ToastUtils.showShortSafe(mActivity.getString(R.string.failed_delete_favourite));
                                }
                            }

                            @Override
                            public void onFailure(Call<MyLikeDeleteRsBean> call, Throwable t) {
                                ToastUtils.showShortSafe(mActivity.getString(R.string.failed_delete_favourite));
                            }
                        }, channel, mActivity);


                    } else {
                        MyLikeManager.getInstance().addMyLike(new Callback<MyLikeAddRsBean>() {
                            @Override
                            public void onResponse(Call<MyLikeAddRsBean> call, Response<MyLikeAddRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    addLikeMusic(channel);
                                    iv_item_like.setImageResource(R.drawable.audio_player_love_btn_pre);
                                    ToastUtils.showShortSafe(mActivity.getString(R.string.add_favourite));
                                    mDataFactory.notifyMyLikeMusicListChange();
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                } else {
                                    ToastUtils.showShortSafe(mActivity.getString(R.string.failed_add_favourite));
                                }
                            }

                            @Override
                            public void onFailure(Call<MyLikeAddRsBean> call, Throwable t) {
                                ToastUtils.showShortSafe(mActivity.getString(R.string.failed_add_favourite));
                            }
                        }, channel, mActivity);


                    }
                }
            });
        }


        void removeLikeMusic(Channel channel) {
            if (null != channel) {
                ArrayList<Channel> myLikeMusicList = mDataFactory.getMyLikeMusicList();
                for (int i = myLikeMusicList.size() - 1; i >= 0; i--) {
                    String likeUri = myLikeMusicList.get(i).getUri();
                    if (likeUri != null && likeUri.equals(channel.getUri()) && myLikeMusicList.get(i).getName().equals(channel.getName())) {
                        mDataFactory.getMyLikeMusicList().remove(i);
                    }
                }

            }
        }

        void addLikeMusic(Channel channel) {
            if (null != channel) {
                mDataFactory.getMyLikeMusicList().add(channel);
            }
        }

        void showDetailsItem(int position) {
            Channel channel = channelList.get(position);

            if (null != mDataFactory.getCurrChannel()) {
                if (channel.getName().equals(mDataFactory.getCurrChannel().getName()) && channel.getUri().equals(mDataFactory.getCurrChannel().getUri())) {
                    channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                    channel.setIsPlaying(mDataFactory.getCurrChannel().getIsPlaying());
                } else {
                    if (channel.getPlayState() != PLAY_STATE_STOP) {
                        channel.setPlayState(PLAY_STATE_STOP);
                    }
                }
            }

            //if(channel.getIsPlaying()){
//                Glog.i(TAG,"show detail item: " + position);
            if (mDataFactory.channelIsCurrent(channel)) {
                this.showSpectrum();
            } else {
                this.hideSpectrum();
            }
            this.channel_details_item_name.setText(channel.getName());
            if (0 == channel.getLastSyncTime()) {
                tv_album_sync_time.setVisibility(View.GONE);
            } else {
                tv_album_sync_time.setVisibility(View.VISIBLE);
                tv_album_sync_time.setText(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
            }

            if (channel.getDuration() != 0) {
                this.tv_album_duration.setText(String.format(ctx.getString(R.string.album_time), TimeUtil.secondToTime((int) channel.getDuration())));
                tv_album_duration.setVisibility(View.VISIBLE);
            } else {
                tv_album_duration.setVisibility(View.GONE);
            }

            if (mDataFactory.isExistMyLikeMusic(channel)) {
                iv_item_like.setImageResource(R.drawable.audio_player_love_btn_pre);
            } else {
                iv_item_like.setImageResource(R.drawable.audio_player_love_btn_nor);
            }


            if (0 == channel.getLastSyncTime() && channel.getDuration() == 0) {
                ll_channel_details_item_album_info.setVisibility(View.GONE);
            } else {
                ll_channel_details_item_album_info.setVisibility(View.VISIBLE);
            }
            if (isFmChannel) {//FM
                this.time_periods.setVisibility(View.VISIBLE);
                this.time_periods.setText(channel.getTimePeriods());
                this.channel_details_item_more_btn.setVisibility(View.INVISIBLE);
                this.iv_item_like.setVisibility(View.INVISIBLE);
                if (channel.getSinger() != null && channel.getSinger() != "") {
                    tv_album_duration.setText(channel.getSinger());
                } else {
                    tv_album_duration.setVisibility(View.GONE);
                }
                if (position == 0) {
                    fm_zhibo.setVisibility(View.VISIBLE);
                    fm_zhibo.setText(R.string.zhibo);
                    fm_zhibo.setTextColor(ContextCompat.getColor(ctx, R.color.white));
                    this.channel_details_item_name.setTextColor(Color.parseColor("#ca203d"));
                } else {
                    fm_zhibo.setVisibility(View.GONE);
                    this.channel_details_item_name.setTextColor(Color.BLACK);
                }
            }
        }

        void showSpectrum() {
            if (isFmChannel) {
                channel_details_item_index.setVisibility(View.INVISIBLE);
                return;
            }
            this.channel_details_item_name.setTextColor(Color.parseColor("#ca203d"));
//            this.channel_details_item_index.setVisibility(View.VISIBLE);

            if (mDataFactory.getCurrChannel().getPlayState() == Channel.PLAY_STATE_PLAYING) {
                if (this.channel_details_item_index.isHide()) {
                    this.channel_details_item_index.showAndPlay();
                }
            } else {
                this.channel_details_item_index.setVisibility(View.INVISIBLE);
            }
        }

        void hideSpectrum() {
            this.channel_details_item_name.setTextColor(Color.BLACK);
            this.channel_details_item_index.setVisibility(View.INVISIBLE);
            if (!this.channel_details_item_index.isHide()) {
                this.channel_details_item_index.hideAndStop();
            }
            //FM
//            fm_zhibo.setVisibility(View.GONE);
        }

//        private void showFmZhibo(String time) {
//            String [] timeArray = null;
//            timeArray = time.split("-");
//            String timeStart = timeArray[0];
//            String timeEnd = timeArray[1];
////            Glog.i("jiayusheng","timeStart: " + timeStart + "timeEnd: " + timeEnd);
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
//            Date currentDate = new Date(System.currentTimeMillis());//获取当前时间
//            try {
//                Date startDate = format.parse(timeStart);
//                Date endData = format.parse(timeEnd);
//                String currentDataString = format.format(currentDate);
//                Date curDate = format.parse(currentDataString);
////                Glog.i("jiayusheng","startDate: " + startDate + "curDate: " + curDate + "endData: " + endData);
////                Glog.i("jiayusheng","startDate.getTime(): " + startDate.getTime() + "curDate.getTime(): " + curDate.getTime() + "endData.getTime(): " + endData.getTime());
//                if (endData.getTime() > curDate.getTime() && curDate.getTime() >= startDate.getTime()){
//                    fm_zhibo.setVisibility(View.VISIBLE);
//                    fm_zhibo.setText(R.string.zhibo);
//                    fm_zhibo.setTextColor(ContextCompat.getColor(ctx,R.color.white));
//                    this.channel_details_item_name.setTextColor(Color.parseColor("#ca203d"));
//                }else {
//                    fm_zhibo.setVisibility(View.GONE);
//                    this.channel_details_item_name.setTextColor(Color.BLACK);
//                }
//            } catch (ParseException e) {
//                fm_zhibo.setVisibility(View.GONE);
//                this.channel_details_item_name.setTextColor(Color.BLACK);
//                e.printStackTrace();
//            }
//        }

    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {
        HeaderViewHolder(View view) {
            super(view);
        }

    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        private TextView mView;

        FootViewHolder(View view) {
            super(view);
        }

        public void setNoMore() {
        }
    }

    private class FootViewNoHolder extends RecyclerView.ViewHolder {
        private TextView mView;

        FootViewNoHolder(View view) {
            super(view);
//            view.setVisibility(View.GONE);
        }

        public void setNoMore() {
        }
    }

}
