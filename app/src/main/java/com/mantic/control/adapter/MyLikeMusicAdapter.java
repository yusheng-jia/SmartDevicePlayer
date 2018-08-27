package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
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

import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.channelplay.bean.AddResult;
import com.mantic.control.api.channelplay.bean.ChannelPlayAddRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.SleepTimeSetUtils;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.SpectrumAnimatorTextView;

import java.util.ArrayList;
import java.util.List;

import static com.mantic.control.data.Channel.PLAY_STATE_PAUSE;
import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;
import static com.mantic.control.data.Channel.PLAY_STATE_STOP;

import com.baidu.iot.sdk.HttpStatus;
import com.baidu.iot.sdk.IoTException;
import com.baidu.iot.sdk.model.ControlResult;
import com.baidu.iot.sdk.model.PageInfo;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.baidu.iot.sdk.HttpStatus.PUT_OR_POST_SUCCESS;

/**
 * Created by Jia on 2017/6/5.
 */

public class MyLikeMusicAdapter extends RecyclerView.Adapter {
    private static final String TAG = "MyLikeMusicAdapter";
    private Context ctx;
    private Activity mActivity;
    private RecyclerView recyclerView;

    private final int TYPE_NORMAL = 0;
    private final int TYPE_FOOTER = 1;  //说明是带有Footer的
    private final int TYPE_FOOTER_COMPLETE = 2;  //说明是带有Footer的
    private final int TYPE_EMPTY = 3;

    //private Subscriber<ArrayList<DataFactory.Channel>> beingPlayAudioListSubscriber;

    private DataFactory.BeingPlayListListener beingPlayListListener;
    private DataFactory.ChannelControlListener channelControlListener;

    private DataFactory mDataFactory;
    private List<Channel> channelList = new ArrayList<>();

    private CustomDialog.Builder mBuilder;

    private int currentItemViewTpye = TYPE_FOOTER;


    public interface OnItemMoreClickListener {
        void moreClickListener(int position);
    }

    private OnItemMoreClickListener onItemMoreClickListener;

    public void setOnItemMoreClickListener(OnItemMoreClickListener onItemMoreClickListener) {
        this.onItemMoreClickListener = onItemMoreClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return currentItemViewTpye;
        } else {
            return TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return channelList.size() == 0 ? 0 : channelList.size() + 1;
    }


    public MyLikeMusicAdapter(Context context, Activity mActivity, List<Channel> channels) {
        this.ctx = context;
        this.mActivity = mActivity;
        mDataFactory = DataFactory.newInstance(ctx);
        if (null != channels) {
            this.channelList = channels;
        }

        this.beingPlayListListener = new DataFactory.BeingPlayListListener() {
            @Override
            public void callback(ArrayList<Channel> channels) {
                Glog.i(TAG, "beingPlayListListener callback");
                notifyDataSetChanged();
            }
        };

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
    }

    public void showLoadComplete() {
        currentItemViewTpye = TYPE_FOOTER_COMPLETE;
        notifyItemChanged(getItemCount());
    }

    public void showEmpty() {
        currentItemViewTpye = TYPE_EMPTY;
        notifyItemChanged(getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            return new MyLikeDetailsItenViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.my_like_details_item, parent, false), this.ctx);
        } else if (viewType == TYPE_FOOTER) {
            return new FootViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot, parent, false));
        } else if (viewType == TYPE_FOOTER_COMPLETE) {
            return new FootViewNoHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot_complete, parent, false));
        } else if (viewType == TYPE_EMPTY) {
            return new EmptyHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot_empty, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyLikeDetailsItenViewHolder) {
            ((MyLikeDetailsItenViewHolder) holder).showDetailsItem(position);
            final Channel channel = channelList.get(position);
            ((MyLikeDetailsItenViewHolder) holder).channel_details_item_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ((MyLikeDetailsItenViewHolder) holder).channel_details_item_index.setVisibility(View.INVISIBLE);
                            break;

                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (mDataFactory.channelIsCurrent(channel) && (channel.getPlayState() == Channel.PLAY_STATE_PLAYING) && !channel.getUri().contains("radio")) {
                                ((MyLikeDetailsItenViewHolder) holder).channel_details_item_index.setVisibility(View.VISIBLE);
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
        mDataFactory.registerBeingPlayListListener(this.beingPlayListListener);
        mDataFactory.addChannelControlListener(this.channelControlListener);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
        mDataFactory.unregisterBeingPlayListListener(this.beingPlayListListener);
        if (this.channelControlListener != null) {
            mDataFactory.removeChannelControlListener(this.channelControlListener);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof MyLikeDetailsItenViewHolder) {
            Channel channel = channelList.get(holder.getAdapterPosition());
            if (channel.getPlayState() == PLAY_STATE_PLAYING) {
                ((MyLikeDetailsItenViewHolder) holder).showSpectrum();
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        this.hideSpectrum(holder);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        this.hideSpectrum(holder);
    }

    private void hideSpectrum(RecyclerView.ViewHolder holder) {
        if (holder instanceof MyLikeDetailsItenViewHolder) {
            ((MyLikeDetailsItenViewHolder) holder).hideSpectrum();
        }
    }

    public void onStart() {
        for (int i = 0; i < channelList.size(); i++) {
            Channel currChannel = channelList.get(i);
            if (currChannel.getPlayState() == PLAY_STATE_PLAYING) {
                if (!isFooterView(i)) {
                    MyLikeDetailsItenViewHolder viewHolder = (MyLikeDetailsItenViewHolder) this.recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder != null) {
                        viewHolder.showSpectrum();
                    }
                }
                break;
            }
        }
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
        this.notifyDataSetChanged();
    }

    public void onStop() {
        for (int i = 0; i < this.getItemCount(); i++) {
            if (!isFooterView(i)) {
                MyLikeDetailsItenViewHolder viewHolder = (MyLikeDetailsItenViewHolder) this.recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null) {
                    viewHolder.hideSpectrum();
                }
            }
        }
    }

    private boolean isFooterView(int currentItem) {
        return currentItem == getItemCount() - 1;
    }

    public void playPause(final int position, final View view) {
        final Channel channel = channelList.get(position);

        Channel currChannel = mDataFactory.getCurrChannel();
        if (null != currChannel && currChannel.getName().equals(channel.getName()) && currChannel.getUri().equals(channel.getUri())) {
            if (currChannel.getPlayState() == Channel.PLAY_STATE_PLAYING) {
                ((MainActivity) mActivity).setBottomSheetExpanded();
                if (null != view) {
                    view.setClickable(true);
                }
                return;
            }

        }

        if (!mDataFactory.channelIsInBeingPlayList(channel)) {
            if (((ManticApplication) mActivity.getApplication()).isReplacePlayingList()) {
                mBuilder = new CustomDialog.Builder(ctx);
                mBuilder.setTitle(ctx.getString(R.string.the_playlist_was_edited));
                mBuilder.setMessage(ctx.getString(R.string.this_play_will_replace_the_playlist));
                mBuilder.setPositiveButton(ctx.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
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
                mBuilder.create().show();
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
                        if (channel.getUri().contains("baidu")) {
                            if (!TextUtils.isEmpty(SharePreferenceUtil.getSharePreferenceData(ctx, "Mantic", "baiduTempDuration"))) {
                                channel.setDuration(Long.parseLong(SharePreferenceUtil.getSharePreferenceData(ctx, "Mantic", "baiduTempDuration")));
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
                                                    ((MainActivity) mActivity).setFmChannel(false);
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

    private class MyLikeDetailsItenViewHolder extends RecyclerView.ViewHolder {
        private Context ctx;
        private SpectrumAnimatorTextView channel_details_item_index;
        private TextView channel_details_item_name;
        private View channel_details_item_view;
        private TextView tv_album_sync_time;
        private TextView tv_album_duration;
        private LinearLayout ll_channel_details_item_album_info;
        private ImageButton my_like_details_item_more_btn;
        private ImageView iv_my_like_icon;

        MyLikeDetailsItenViewHolder(final View itemView, Context context) {
            super(itemView);
            this.ctx = context;
            this.channel_details_item_index = (SpectrumAnimatorTextView) itemView.findViewById(R.id.channel_details_item_index);
            this.channel_details_item_name = (TextView) itemView.findViewById(R.id.channel_details_item_name);
            this.iv_my_like_icon = (ImageView) itemView.findViewById(R.id.iv_my_like_icon);
            this.ll_channel_details_item_album_info = (LinearLayout) itemView.findViewById(R.id.ll_channel_details_item_album_info);
            this.tv_album_sync_time = (TextView) itemView.findViewById(R.id.tv_album_sync_time);
            this.tv_album_duration = (TextView) itemView.findViewById(R.id.tv_album_duration);
            this.channel_details_item_view = itemView.findViewById(R.id.channel_details_item_view);
            this.my_like_details_item_more_btn = (ImageButton) itemView.findViewById(R.id.my_like_details_item_more_btn);
            this.channel_details_item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = recyclerView.getChildAdapterPosition(itemView);
                    if (channel_details_item_view.isClickable() && position != -1) {
                        channel_details_item_view.setClickable(false);
                        playPause(position, channel_details_item_view);
                    }

                }
            });

            this.my_like_details_item_more_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemMoreClickListener) {
                        final int position = recyclerView.getChildAdapterPosition(itemView);
                        onItemMoreClickListener.moreClickListener(position);
                    }
                }
            });
        }

        void showDetailsItem(int position) {
            Channel channel = channelList.get(position);

            if (null != mDataFactory.getCurrChannel()) {
                if (channel.getUri().equals(mDataFactory.getCurrChannel().getUri()) && channel.getUri().equals(mDataFactory.getCurrChannel().getName())) {
                    channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                    channel.setIsPlaying(mDataFactory.getCurrChannel().getIsPlaying());
                } else {
                    channel.setPlayState(PLAY_STATE_STOP);
                }
            } else {
                channel.setPlayState(PLAY_STATE_STOP);
            }


            if (channel.getPlayState() == PLAY_STATE_PLAYING) {
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


            if (0 == channel.getLastSyncTime() && channel.getDuration() == 0) {
                ll_channel_details_item_album_info.setVisibility(View.GONE);
            } else {
                ll_channel_details_item_album_info.setVisibility(View.VISIBLE);
            }

            GlideImgManager.glideLoaderCircle(ctx, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, iv_my_like_icon);

            Glog.i(TAG, "showDetailsItem: " + channel.getTlid());
        }

        void showSpectrum() {
            this.channel_details_item_name.setTextColor(Color.parseColor("#ca203d"));
//            this.channel_details_item_index.setVisibility(View.VISIBLE);
            if (this.channel_details_item_index.isHide()) {
                this.channel_details_item_index.showAndPlay();
            }
        }

        void hideSpectrum() {
            this.channel_details_item_name.setTextColor(Color.BLACK);
            this.channel_details_item_index.setVisibility(View.INVISIBLE);
            if (!this.channel_details_item_index.isHide()) {
                this.channel_details_item_index.hideAndStop();
            }
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

    private class EmptyHolder extends RecyclerView.ViewHolder {
        private TextView mView;

        EmptyHolder(View view) {
            super(view);
//            view.setVisibility(View.GONE);
        }
    }
}
