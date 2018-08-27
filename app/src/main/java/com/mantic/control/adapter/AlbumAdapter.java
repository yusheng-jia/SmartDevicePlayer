package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.api.searchresult.bean.AlbumSearchResultBean;
import com.mantic.control.data.DataFactory;
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

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{

    public final static int TYPE_SECTION = 1;
    public final static int TYPE_ITEM = 0;

    private Context mContext;
    private Activity mActivity;
    private DataFactory mDataFactory;
    private ArrayList<MyChannel> myChannelList = new ArrayList<>();
    private ArrayList<AlbumSearchResultBean> aulbumSearchResultBeanList = new ArrayList<AlbumSearchResultBean>();



    public interface OnTextMoreClickListener {
        void moreTextClickListener(int position);
    }

    private OnTextMoreClickListener onTextMoreClickListener;

    public void setOnTextMoreClickListener(OnTextMoreClickListener onTextMoreClickListener) {
        this.onTextMoreClickListener = onTextMoreClickListener;
    }


    public AlbumAdapter(Context mContext, Activity mActivity) {
        super();
        this.mContext = mContext;
        this.mActivity = mActivity;
        mDataFactory = DataFactory.newInstance(mContext);
    }


    public void setMyChannelList(ArrayList<AlbumSearchResultBean> aulbumSearchResultBeanList) {
        this.aulbumSearchResultBeanList = aulbumSearchResultBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (!TextUtils.isEmpty(aulbumSearchResultBeanList.get(position).getResultType())) {
            return TYPE_SECTION;
        }
        return TYPE_ITEM;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_SECTION) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.author_header, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.author_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AlbumSearchResultBean authorSearchResultBean = aulbumSearchResultBeanList.get(position);
        if (!TextUtils.isEmpty(authorSearchResultBean.getResultType())) {
            holder.tv_author_from_name.setText(authorSearchResultBean.getResultType());
            if (authorSearchResultBean.getResultType().equals("蜻蜓FM")) {
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.qingting_music_service_icon));
            } else if (authorSearchResultBean.getResultType().equals("网易云音乐")){
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wyyyy_music_service_icon));
            } else if (authorSearchResultBean.getResultType().equals("喜马拉雅")){
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ximalaya_music_service_icon));
            } else if (authorSearchResultBean.getResultType().equals("贝瓦")){
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.beiwa_music_service_icon));
            } else if (authorSearchResultBean.getResultType().equals("工程师爸爸")){
                holder.iv_author_from.setImageDrawable(mContext.getResources().getDrawable(R.drawable.idaddy_music_service_icon));
            }

            if (authorSearchResultBean.getResultSize() < 3) {
                holder.ll_search_more.setVisibility(View.GONE);
            } else {
                holder.ll_search_more.setVisibility(View.VISIBLE);
                holder.ll_search_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onTextMoreClickListener) {
                            onTextMoreClickListener.moreTextClickListener(position);
                            mDataFactory.notifyHideSoftKey();
                        }
                    }
                });
            }
            return;
        }

        final MyChannel myChannel = authorSearchResultBean.getMyChannel();
        if (null == myChannel) {
            return;
        }
        GlideImgManager.glideLoaderCircle(mContext, myChannel.getChannelCoverUrl(), R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, holder.iv_author_icon);

        holder.tv_author_name.setText(myChannel.getChannelName());
        if(myChannel.getmTotalCount() > 0) {
            holder.tv_author_singer.setText(String.format(mContext.getString(R.string.album_total_size), myChannel.getmTotalCount()));
            holder.tv_author_singer.setVisibility(View.VISIBLE);
        } else {
            holder.tv_author_singer.setVisibility(View.GONE);
        }

        holder.rl_author_goto_channel_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDataFactory.notifyHideSoftKey();
                ChannelDetailsFragment cdFragment = new ChannelDetailsFragment();
                String channelName = myChannel.getChannelName();
                String musicServiceId = myChannel.getMusicServiceId();
                String channnelId = myChannel.getChannelId();
                Bundle bundle = new Bundle();
                bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID,musicServiceId);
                bundle.putLong(ChannelDetailsFragment.CHANNEL_TOTAL_COUNT, myChannel.getmTotalCount());
                bundle.putString(ChannelDetailsFragment.CHANNEL_ID,channnelId);
                bundle.putString(ChannelDetailsFragment.CHANNEL_NAME,channelName);
                bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "SearchResultFragment");
                bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
                bundle.putString(ChannelDetailsFragment.ALBUM_ID, myChannel.getAlbumId());
                bundle.putString(ChannelDetailsFragment.MAIN_ID, myChannel.getMainId());
                bundle.putString(ChannelDetailsFragment.CHANNEL_COVER_URL, myChannel.getChannelCoverUrl());
                bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, myChannel.getChannelIntro());
                bundle.putString(ChannelDetailsFragment.CHALLEL_SINGER,myChannel.getSingerName());


                cdFragment.setArguments(bundle);
                if(mActivity instanceof FragmentEntrust){
                    ((FragmentEntrust) mActivity).pushFragment(cdFragment,musicServiceId+channnelId+channelName);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (null == aulbumSearchResultBeanList || aulbumSearchResultBeanList.size() == 0) {
            return 0;
        }
        return aulbumSearchResultBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_author_goto_channel_details;
        public ImageView iv_author_icon;
        public TextView tv_author_name;
        public TextView tv_author_singer;
        public LinearLayout ll_search_more;
        public TextView tv_author_from_name;
        public CircleImageView iv_author_from;

        public ViewHolder(View itemView) {
            super(itemView);

            rl_author_goto_channel_details = (RelativeLayout) itemView.findViewById(R.id.rl_author_goto_channel_details);
            iv_author_icon = (ImageView) itemView.findViewById(R.id.iv_author_icon);
            tv_author_name = (TextView) itemView.findViewById(R.id.tv_author_name);
            tv_author_singer = (TextView) itemView.findViewById(R.id.tv_author_singer);
            ll_search_more = (LinearLayout) itemView.findViewById(R.id.ll_search_more);
            tv_author_from_name = (TextView) itemView.findViewById(R.id.tv_author_from_name);
            iv_author_from = (CircleImageView) itemView.findViewById(R.id.iv_author_from);
        }
    }
}
