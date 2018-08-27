package com.mantic.control.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.entiy.ClassificationBean;
import com.mantic.control.fragment.ChannelDetailsFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.widget.RecyclerTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2018/1/17.
 */

public class MusicRankAdapter extends RecyclerTabLayout.Adapter<MusicRankAdapter.MusicRankHolder> {

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    protected List<String> musicRankList = new ArrayList<>();
    private Context mContext;



    public MusicRankAdapter(ViewPager viewPager, Context context) {
        super(viewPager);
        musicRankList.add("全球音乐榜");
        musicRankList.add("虾米音乐");
        musicRankList.add("网易云音乐");
        musicRankList.add("QQ音乐");
        musicRankList.add("酷我音乐");
        musicRankList.add("酷狗音乐");
        mContext = context;
    }

//    public MusicRankAdapter(Context context) {
//        mContext = context;
//        musicRankList.add("全球音乐榜");
//        musicRankList.add("虾米音乐");
//        musicRankList.add("网易云音乐");
//        musicRankList.add("QQ音乐");
//        musicRankList.add("酷我音乐");
//        musicRankList.add("酷狗音乐");
//    }

    public void setMusicRankList(List<String> musicRankList) {
        this.musicRankList = musicRankList;
    }

    @Override
    public void onBindViewHolder(MusicRankHolder holder, final int position) {
        String rankName = musicRankList.get(position);
        holder.tv_music_rank_name.setText(rankName);
        holder.rl_music_rank_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(position);
                }
                getViewPager().setCurrentItem(position);
            }
        });

        if (position == getCurrentIndicatorPosition()) {
            holder.tv_music_rank_name.setTextColor(mContext.getResources().getColor(R.color.rank_select));
            holder.rl_music_rank_item.setBackgroundResource(R.drawable.child_education_item_selected_background);
        } else {
            holder.tv_music_rank_name.setTextColor(mContext.getResources().getColor(R.color.rank_normal));
            holder.rl_music_rank_item.setBackgroundResource(R.drawable.child_education_item_background);
        }
    }

    @Override
    public MusicRankHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MusicRankHolder(LayoutInflater.from(mContext).inflate(R.layout.music_rank_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return musicRankList.size();
    }

    public class MusicRankHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rl_music_rank_item;
        private TextView tv_music_rank_name;

        public MusicRankHolder(View itemView) {
            super(itemView);
            tv_music_rank_name = (TextView) itemView.findViewById(R.id.tv_music_rank_name);
            rl_music_rank_item = (RelativeLayout) itemView.findViewById(R.id.rl_music_rank_item);
        }
    }
}
