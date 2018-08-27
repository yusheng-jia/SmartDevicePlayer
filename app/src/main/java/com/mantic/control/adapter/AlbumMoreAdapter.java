package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.MyChannel;
import com.mantic.control.fragment.ChannelDetailsFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CircleImageView;

import java.util.ArrayList;

/**
 * Created by lin on 2017/7/7.
 */

public class AlbumMoreAdapter extends RecyclerView.Adapter<AlbumMoreAdapter.ViewHolder> {

    public final static int TYPE_SECTION = 1;
    public final static int TYPE_ITEM = 0;
    private final int TYPE_FOOTER = 2;
    private final int TYPE_FOOTER_COMPLETE = 3;  //说明是带有Footer的
    private static final int TYPE_FOOTER_EMPTY = 4;

    private int currentItemViewTpye = TYPE_FOOTER;

    private Context mContext;
    private Activity mActivity;
    private ArrayList<MyChannel> myChannelList = new ArrayList<>();
    private String albumType;


    public interface OnTextMoreClickListener {
        void moreTextClickListener();
    }

    private OnTextMoreClickListener onTextMoreClickListener;

    public void setOnTextMoreClickListener(OnTextMoreClickListener onTextMoreClickListener) {
        this.onTextMoreClickListener = onTextMoreClickListener;
    }


    public AlbumMoreAdapter(Context mContext, Activity mActivity, ArrayList<MyChannel> myChannelList, String albumType) {
        super();
        this.mContext = mContext;
        this.mActivity = mActivity;
        if (null != myChannelList) {
            this.myChannelList = myChannelList;
        }
        this.albumType = albumType;
    }


    public void setMyChannelList(ArrayList<MyChannel> myChannelList) {
        this.myChannelList = myChannelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_SECTION;
        } else if (position + 1 == getItemCount()) {
            return currentItemViewTpye;
        } else {
            return TYPE_ITEM;
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
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.author_header_back, parent, false));
        } else if (viewType == TYPE_FOOTER) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_foot, parent, false));
        } else if (viewType == TYPE_FOOTER_COMPLETE) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_foot_complete, parent, false));
        } else if (viewType == TYPE_FOOTER_EMPTY) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_foot_empty, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.author_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 0) {
            holder.tv_search_more.setText("返回");
            holder.tv_author_from_name.setText(albumType);
            if ("网易云音乐".equals(albumType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wyyyy_music_service_icon));
            } else if ("蜻蜓FM".equals(albumType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qingting_music_service_icon));
            } else if ("喜马拉雅".equals(albumType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ximalaya_music_service_icon));
            } else if ("贝瓦".equals(albumType)) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.beiwa_music_service_icon));
            } else if ("工程师爸爸".equals(albumType)){
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.idaddy_music_service_icon));
            }

            holder.tv_search_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onTextMoreClickListener) {
                        onTextMoreClickListener.moreTextClickListener();
                    }
                }
            });
            return;
        } else if (position == myChannelList.size() + 1){
            return;
        }

        final MyChannel myChannel = myChannelList.get(position - 1);
        GlideImgManager.glideLoaderCircle(mContext, myChannel.getChannelCoverUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_author_icon);
        holder.tv_author_name.setText(myChannel.getChannelName());
        if (myChannel.getmTotalCount() > 0) {
            holder.tv_author_singer.setText(String.format(mContext.getString(R.string.album_total_size), myChannel.getmTotalCount()));
            holder.tv_author_singer.setVisibility(View.VISIBLE);
        } else {
            holder.tv_author_singer.setVisibility(View.GONE);
        }

        holder.rl_author_goto_channel_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelDetailsFragment cdFragment = new ChannelDetailsFragment();
                String channelName = myChannel.getChannelName();
                String musicServiceId = myChannel.getMusicServiceId();
                String channnelId = myChannel.getChannelId();
                Bundle bundle = new Bundle();
                bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, musicServiceId);
                bundle.putLong(ChannelDetailsFragment.CHANNEL_TOTAL_COUNT, myChannel.getmTotalCount());
                bundle.putString(ChannelDetailsFragment.CHANNEL_ID, channnelId);
                bundle.putString(ChannelDetailsFragment.CHANNEL_NAME, channelName);
                bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "SearchResultFragment");
                bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
                bundle.putString(ChannelDetailsFragment.ALBUM_ID, myChannel.getAlbumId());
                bundle.putString(ChannelDetailsFragment.MAIN_ID, myChannel.getMainId());
                bundle.putString(ChannelDetailsFragment.CHANNEL_COVER_URL, myChannel.getChannelCoverUrl());
                bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, myChannel.getChannelIntro());
                bundle.putString(ChannelDetailsFragment.CHALLEL_SINGER, myChannel.getSingerName());


                cdFragment.setArguments(bundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).pushFragment(cdFragment, musicServiceId + channnelId + channelName);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return (myChannelList.size() == 0) ? 0 : (myChannelList.size() + 2);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_author_goto_channel_details;
        public ImageView iv_author_icon;
        public TextView tv_author_name;
        public TextView tv_author_singer;
        public TextView tv_search_more;
        public TextView tv_author_from_name;
        public CircleImageView iv_author_from;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_author_goto_channel_details = (RelativeLayout) itemView.findViewById(R.id.rl_author_goto_channel_details);
            iv_author_icon = (ImageView) itemView.findViewById(R.id.iv_author_icon);
            tv_author_name = (TextView) itemView.findViewById(R.id.tv_author_name);
            tv_author_singer = (TextView) itemView.findViewById(R.id.tv_author_singer);
            tv_search_more = (TextView) itemView.findViewById(R.id.tv_search_more);
            tv_author_from_name = (TextView) itemView.findViewById(R.id.tv_author_from_name);
            iv_author_from = (CircleImageView) itemView.findViewById(R.id.iv_author_from);
        }
    }
}
