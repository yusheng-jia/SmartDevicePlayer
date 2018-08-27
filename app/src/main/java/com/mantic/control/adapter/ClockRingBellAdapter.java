package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.MyChannel;
import com.mantic.control.fragment.AddClockFragment;
import com.mantic.control.fragment.ChannelManagementFragment;
import com.mantic.control.fragment.ClockCallbacks;
import com.mantic.control.utils.GlideImgManager;

import java.util.List;

public class ClockRingBellAdapter extends RecyclerView.Adapter<ClockRingBellAdapter.ViewHolder> {
    private Context mContext;
    private List<MyChannel> channelList;
    private Activity mActivity;

    public ClockRingBellAdapter(List<MyChannel> myChannels, Activity activity, Fragment fragment){
        channelList=myChannels;
        mActivity=activity;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(mContext==null){
            mContext=parent.getContext();
        }
        View view= LayoutInflater.from(mContext).inflate(R.layout.my_channel_album,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyChannel channel=channelList.get(position);
        holder.channelTitle.setText(channel.getChannelName());
        GlideImgManager.glideLoaderCircle(mContext, channel.getChannelCoverUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.channel_icon);
       holder.layout.setBackgroundColor(Color.TRANSPARENT);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                   onItemClickListener.OnItemClick();
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return channelList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView channelTitle;
        private ImageView channel_icon;
        private TextView channelSubTitle;
        private RelativeLayout layout;
        public ViewHolder(View view){
            super(view);
            layout=(RelativeLayout)view.findViewById(R.id.rl_goto_channel_details) ;
            channelTitle=(TextView)view.findViewById(R.id.album_title);
            channel_icon=(ImageView)view.findViewById(R.id.iv_my_channel_cover);
            channelSubTitle=(TextView)view.findViewById(R.id.album_sub);
        }
    }

    public interface OnItemClickListener{
        public void OnItemClick();
    }
    public ClockRingBellAdapter.OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener=listener;
    }
}
