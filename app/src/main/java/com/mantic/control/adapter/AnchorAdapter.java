package com.mantic.control.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.AnchorSelActivity;
import com.mantic.control.api.sound.MopidyRsAnchorBean;
import com.mantic.control.dubbing.Anchor;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;


/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/26.
 * desc:
 */

public class AnchorAdapter extends RecyclerView.Adapter {
    private final String TAG = "AnchorAdapter";
    private List<MopidyRsAnchorBean.Result> anchorArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context mContext;
    private Handler mHandler;
    private int playIndex;
    private boolean isPlaying = false;

    public AnchorAdapter(Context context, Handler handler){
        mContext = context;
        mHandler = handler;
    }

    public void setAnchorList(List<MopidyRsAnchorBean.Result> list){
        anchorArrayList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AnchorAdapter.AnchorViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.sound_anchor_item, parent, false), this.mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AnchorViewHolder){
            ((AnchorViewHolder) holder).showItem(position);
        }
    }

    @Override
    public int getItemCount() {
        return anchorArrayList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView = view;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView view) {
        super.onDetachedFromRecyclerView(view);
        recyclerView = null;
    }

    public void setItemPlay(boolean playStatus,int currPlayIndex) {
        isPlaying = playStatus;
        playIndex = currPlayIndex;
    }


    private class AnchorViewHolder extends RecyclerView.ViewHolder{
        private View curr_item_view;
        private ImageView sound_anchor_icon;
        private TextView sound_anchor_name;
        private TextView sound_anchor_detail;
        private ImageButton sound_anchor_play;

        AnchorViewHolder(final View itemView, Context context) {
            super(itemView);
            curr_item_view = itemView;
            sound_anchor_icon = (ImageView)itemView.findViewById(R.id.sound_anchor_icon);
            sound_anchor_name = (TextView)itemView.findViewById(R.id.sound_anchor_name);
            sound_anchor_detail = (TextView)itemView.findViewById(R.id.sound_anchor_detail);
            sound_anchor_play = (ImageButton)itemView.findViewById(R.id.sound_anchor_play);
            curr_item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = recyclerView.getChildAdapterPosition(itemView);
                    Message msg = new Message();
                    msg.what = AnchorSelActivity.ANCHOR_CLICK;
                    msg.arg1 = position;
                    mHandler.sendMessage(msg);
                }
            });
            sound_anchor_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                if (!isPlaying){
                    final int position = recyclerView.getChildAdapterPosition(itemView);
                    Message msg = new Message();
                    msg.what = AnchorSelActivity.ANCHOR_PLAY;
                    msg.arg1 = position;
                    mHandler.sendMessage(msg);
                }
                }
            });

        }

        void showItem(int position){
            GlideImgManager.glideLoader(mContext,anchorArrayList.get(position).mantic_podcaster_avater,R.drawable.sound_people,R.drawable.sound_people,sound_anchor_icon);
//            sound_anchor_icon.setImageResource(R.drawable.sound_people);
            if (position == playIndex && isPlaying){
                sound_anchor_play.setImageResource(R.drawable.anchor_stop);
            }else {
                sound_anchor_play.setImageResource(R.drawable.anchor_play);
            }
            sound_anchor_name.setText(anchorArrayList.get(position).name);
            sound_anchor_detail.setText(anchorArrayList.get(position).mantic_describe);
        }
    }
}
