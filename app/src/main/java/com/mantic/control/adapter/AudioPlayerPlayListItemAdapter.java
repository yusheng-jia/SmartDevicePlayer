package com.mantic.control.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.api.channelplay.bean.ChannelPlayListDeleteRsBean;
import com.mantic.control.api.channelplay.bean.SetCurrentChannelPlayRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.manager.ChannelPlayListManager;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mantic.control.data.Channel.PLAY_STATE_PLAYING;

/**
 * Created by lin on 2017/7/13.
 */

public class AudioPlayerPlayListItemAdapter extends RecyclerView.Adapter<AudioPlayerPlayListItemAdapter.ViewHolder> {
    private final String TAG = "AudioPlayerPlayListItemAdapter";
    private DataFactory mDataFactory;
    private Context mContext;
    private ArrayList<Channel> mAudioPlayList;
    private CustomDialog.Builder mBuilder;
    private int index = -1;
//    private boolean isFmChannel = false;


    private ItemTouchHelper itemTouchHelper;


    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public interface OnAudioPlayerListDismissListener {
        void dismiss();
    }

    private OnAudioPlayerListDismissListener onAudioPlayerListDismissListener;

    public void setOnAudioPlayerListDismissListener(OnAudioPlayerListDismissListener onAudioPlayerListDismissListener) {
        this.onAudioPlayerListDismissListener = onAudioPlayerListDismissListener;
    }

    public AudioPlayerPlayListItemAdapter(Context mContext) {
        this.mContext = mContext;
        mDataFactory = DataFactory.newInstance(mContext);
        mAudioPlayList = mDataFactory.getBeingPlayList();
        mBuilder = new CustomDialog.Builder(mContext);
        mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
    }

    public void setAudioPlayList(ArrayList<Channel> mAudioPlayList) {
        this.mAudioPlayList = mAudioPlayList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.audio_player_playlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Channel channel = mAudioPlayList.get(position);

        int playState = channel.getPlayState();
//        if (playState == PLAY_STATE_PLAYING) {
        if (mDataFactory.channelIsCurrent(channel)) {
            holder.audio_player_playlist_item_name.setTextColor(mContext.getResources().getColor(R.color.tip_red));
            holder.audio_player_playlist_item_singer_name.setTextColor(mContext.getResources().getColor(R.color.tip_red));
            //this.rl_audio_player_playlist_item.setBackgroundColor(Color.parseColor("#a3c4c8"));
        } else {
            holder.rl_audio_player_playlist_item.setBackgroundColor(mContext.getResources().getColor(R.color.audioPlayerItemNormalColor));
            holder.audio_player_playlist_item_name.setTextColor(mContext.getResources().getColor(R.color.audioPlayerPlaylistItemNameTextColor));
            holder.audio_player_playlist_item_singer_name.setTextColor(mContext.getResources().getColor(R.color.audioPlayerPlaylistItemNameTextColor));
        }

        holder.audio_player_playlist_item_name.setText(channel.getName());
        holder.audio_player_playlist_item_singer_name.setText(channel.getSinger());

        holder.rl_audio_player_playlist_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getFmChannel()){
                    AudioPlayerUtil.playByPosition(mContext, mDataFactory, position, mContext);
                }
            }
        });

        holder.audio_player_playlist_item_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBuilder.setMessage(String.format(mContext.getString(R.string.sure_delete_source), mAudioPlayList.get(position).getName()));
                mBuilder.setPositiveButton(mContext.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
                    @Override
                    public void onPositiveClick(final CustomDialog dialog) {
                        final boolean isCurrentChannel = mDataFactory.channelIsCurrent(mAudioPlayList.get(position));
                        ChannelPlayListManager.getInstance().deleteChannelPlayList(new Callback<ChannelPlayListDeleteRsBean>() {
                            @Override
                            public void onResponse(Call<ChannelPlayListDeleteRsBean> call, Response<ChannelPlayListDeleteRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    ((ManticApplication)mContext.getApplicationContext()).setIsReplacePlayingList(true);
                                    if (mAudioPlayList.size() > 1) {
                                        if (position == (mAudioPlayList.size() - 1)) {
                                            index = 0;
                                        } else {
                                            index = position;
                                        }
                                        mAudioPlayList.remove(position);

                                        if (isCurrentChannel) {
                                            AudioPlayerUtil.playByPosition(mContext, mDataFactory, index, mContext);
                                        } else {
                                            mDataFactory.notifyBeingPlayListChange();
                                            mDataFactory.notifyMyLikeMusicStatusChange();
                                        }

                                        notifyDataSetChanged();
                                    } else {

                                        channel.deviceStop(mContext);

                                        mAudioPlayList.remove(position);
                                        notifyDataSetChanged();
                                        mDataFactory.setBeingPlayList(new ArrayList<Channel>());
                                        mDataFactory.setCurrChannel(null);
                                        mDataFactory.notifyBeingPlayListChange();
                                        mDataFactory.notifyMyLikeMusicStatusChange();

                                        if (null != onAudioPlayerListDismissListener) {
                                            onAudioPlayerListDismissListener.dismiss();
                                        }
                                    }


                                } else {
                                    ToastUtils.showShortSafe(mContext.getString(R.string.delete_fail));
                                }

                                dialog.dismiss();
                            }

                            @Override
                            public void onFailure(Call<ChannelPlayListDeleteRsBean> call, Throwable t) {
                                ToastUtils.showShortSafe(mContext.getString(R.string.delete_fail));
                                dialog.dismiss();
                            }
                        }, mContext, mAudioPlayList.get(position).getTlid());
                    }
                });

                mBuilder.setNegativeButton(mContext.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                    @Override
                    public void onNegativeClick(CustomDialog dialog) {
                        dialog.dismiss();
                    }
                });

                mBuilder.create().show();
            }
        });

        if (getFmChannel()){
            holder.audio_player_playlist_item_del_btn.setVisibility(View.INVISIBLE);
            holder.audio_player_playlist_item_setting_btn.setVisibility(View.INVISIBLE);
            holder.time_periods.setVisibility(View.VISIBLE);
            holder.time_periods.setText(channel.getTimePeriods());
            if (playState == PLAY_STATE_PLAYING){
                holder.fm_zhibo.setVisibility(View.VISIBLE);
                holder.fm_zhibo.setText(R.string.zhibo);
                holder.fm_zhibo.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            }else {
                holder.fm_zhibo.setVisibility(View.GONE);
            }
        }else {
            holder.audio_player_playlist_item_del_btn.setVisibility(View.VISIBLE);
            holder.audio_player_playlist_item_setting_btn.setVisibility(View.VISIBLE);
            holder.time_periods.setVisibility(View.GONE);
            holder.fm_zhibo.setVisibility(View.GONE);
        }

    }

    public void setFmChannel(boolean isFmChannel){
//        this.isFmChannel = isFmChannel;
        SharePreferenceUtil.setFmChannel(mContext,isFmChannel);
    }

    public boolean getFmChannel(){
//        return SharePreferenceUtil.getFmChannel(mContext);
        if (null != mDataFactory.getCurrChannel() && mDataFactory.getCurrChannel().getUri().contains("radio")){
            return true;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return mAudioPlayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView audio_player_playlist_item_name;
        private TextView audio_player_playlist_item_singer_name;
        private RelativeLayout rl_audio_player_playlist_item;
        private ImageButton audio_player_playlist_item_del_btn;
        private ImageButton audio_player_playlist_item_setting_btn;
        private TextView time_periods;//FM
        private TextView fm_zhibo;//FM

        public ViewHolder(final View itemView) {
            super(itemView);
            this.audio_player_playlist_item_name = (TextView) itemView.findViewById(R.id.audio_player_playlist_item_name);
            this.audio_player_playlist_item_singer_name = (TextView) itemView.findViewById(R.id.audio_player_playlist_item_singer_name);
            this.audio_player_playlist_item_del_btn = (ImageButton) itemView.findViewById(R.id.audio_player_playlist_item_del_btn);
            this.audio_player_playlist_item_setting_btn = (ImageButton) itemView.findViewById(R.id.audio_player_playlist_item_setting_btn);
            this.rl_audio_player_playlist_item = (RelativeLayout) itemView.findViewById(R.id.rl_audio_player_playlist_item);
            this.time_periods = (TextView) itemView.findViewById(R.id.time_periods);
            this.fm_zhibo = (TextView) itemView.findViewById(R.id.text_fm_zhibo);
            this.audio_player_playlist_item_setting_btn.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            if (null != audio_player_playlist_item_setting_btn) {
                itemTouchHelper.startDrag(this);
            }

            return false;
        }
    }
}
