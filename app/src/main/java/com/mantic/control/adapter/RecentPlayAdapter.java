package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2017/7/5.
 */

public class  RecentPlayAdapter extends RecyclerView.Adapter<RecentPlayAdapter.ViewHolder> {

    private final String TAG = "RecentPlayAdapter";

    private Context mContext;
    private Activity mActivity;
    private DataFactory mDataFactory;
    private List<Channel> recentPlayList;
    private ArrayList<String> uriLists = new ArrayList<>();
    private ArrayList<Channel> beingPlayList;
    private boolean isNeedShowAddBtn = true;

    public RecentPlayAdapter(Context mContext, Activity mActivity, ArrayList<String> uriLists, boolean isNeedShowAddBtn) {
        super();
        this.mContext = mContext;
        this.mActivity = mActivity;
        mDataFactory = DataFactory.newInstance(mContext);

        if (null != uriLists) {
            this.uriLists = uriLists;
        }
        this.isNeedShowAddBtn = isNeedShowAddBtn;
        recentPlayList = Util.getRecentPlay(mContext, mDataFactory);
        beingPlayList = mDataFactory.getBeingPlayList();
    }

    public void setRecentPlayList(List<Channel> recentPlayList) {
        this.recentPlayList = recentPlayList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.track_content, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Channel channel = recentPlayList.get(position);
        holder.tv_recent_play_item_name.setText(channel.getName());
        holder.tv_album_singer.setText(TextUtils.isEmpty(channel.getSinger()) ? mContext.getString(R.string.unknown) : channel.getSinger());
        if (channel.getDuration() != 0) {
            holder.tv_album_duration.setText(TimeUtil.secondToTime((int)channel.getDuration()));
        }

//        if (!TextUtils.isEmpty(channel.getIconUrl())) {
//
//        }

        GlideImgManager.glideLoaderCircle(mContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_recent_play_icon);
        holder.iv_recent_play_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Channel> channels = new ArrayList<Channel>();
                channels.add(channel);
                mDataFactory.notifyChannelAddChange(channels);
                holder.iv_recent_play_add.setVisibility(View.INVISIBLE);
                uriLists.add(channel.getUri());
            }
        });

        if (isNeedShowAddBtn) {
            if (uriLists.contains(channel.getUri())) {
                holder.iv_recent_play_add.setVisibility(View.INVISIBLE);
            } else  {
                holder.iv_recent_play_add.setVisibility(View.VISIBLE);
            }
        } else {
            holder.iv_recent_play_add.setVisibility(View.INVISIBLE);
        }

        holder.rl_recent_play_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataFactory.notifyHideSoftKey();
                holder.rl_recent_play_item_view.setClickable(false);
                AudioPlayerUtil.playOrPause(mContext, recentPlayList.get(position), holder.rl_recent_play_item_view, mActivity, mDataFactory);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentPlayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_recent_play_icon;
        public ImageView iv_recent_play_add;
        public TextView tv_recent_play_item_name;
        public TextView tv_album_singer;
        public TextView tv_album_duration;
        public RelativeLayout rl_recent_play_item_view;

        public ViewHolder(View view) {
            super(view);
            iv_recent_play_icon = (ImageView) view.findViewById(R.id.iv_recent_play_icon);
            iv_recent_play_add = (ImageView) view.findViewById(R.id.iv_recent_play_add);
            tv_recent_play_item_name = (TextView) view.findViewById(R.id.tv_recent_play_item_name);
            tv_album_singer = (TextView) view.findViewById(R.id.tv_album_singer);
            tv_album_duration = (TextView) view.findViewById(R.id.tv_album_duration);
            rl_recent_play_item_view = (RelativeLayout) view.findViewById(R.id.rl_recent_play_item_view);
        }
    }

}
