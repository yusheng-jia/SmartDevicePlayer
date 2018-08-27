package com.mantic.control.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.api.sound.MopidyRsSoundBgMusicBean;
import com.mantic.control.listener.SoundSelPlayClickListener;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/26.
 * desc:
 */

public class SoundBackgroundAdapter extends RecyclerView.Adapter{
    private final String TAG = "AnchorAdapter";
    private List<MopidyRsSoundBgMusicBean.Result> anchorArrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Context mContext;
    private SoundSelPlayClickListener playClickListener;
    private BgMusicItemClickListener bgMusicItemClickListener;
    private int currentPlayItem = -1;

    /** 1: 暂停或者原始状态 2：播放状态 3：下载状态*/
    private int itemStatus = 1;

    public void setPlayClickListener(SoundSelPlayClickListener listener){
        this.playClickListener = listener;
    }

    public void setBgMusicItemClickListener(BgMusicItemClickListener listener){
        this.bgMusicItemClickListener = listener;
    }

    public SoundBackgroundAdapter(Context context){
        mContext = context;
    }

    public void setAnchorList(List<MopidyRsSoundBgMusicBean.Result> list){
        anchorArrayList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SoundBackgroundAdapter.AnchorViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.sound_anchor_item, parent, false), this.mContext);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SoundBackgroundAdapter.AnchorViewHolder){
            ((SoundBackgroundAdapter.AnchorViewHolder) holder).showItem(position);
        }
    }

    public void updatePlayState(int status,int position){
        currentPlayItem = position;
        Glog.i(TAG,"updatePlayState -> status:" + status + " ->postion: " + position) ;
        itemStatus = status;
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
                    if (bgMusicItemClickListener!=null){
                        int position = recyclerView.getChildAdapterPosition(itemView);
                        bgMusicItemClickListener.musicItemClick(position);
                    }
                }
            });
            sound_anchor_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Glog.i(TAG,"itemStatus:" + itemStatus);
                    if (playClickListener != null){
                        int position = recyclerView.getChildAdapterPosition(itemView);
                        if (itemStatus == 2){
                            playClickListener.onPauseClick(position);
                        }else if (itemStatus == 1){
                            playClickListener.onPlayClick(position,anchorArrayList.get(position).mantic_real_url);
                        }
                    }
                }
            });
        }

        void showItem(int position){
            Glog.i(TAG,"showItem...:" + position + "itemStatus: " + itemStatus);
            sound_anchor_icon.setImageResource(R.drawable.sound_background_music);
            sound_anchor_name.setText(anchorArrayList.get(position).name);
            sound_anchor_detail.setText(TimeUtil.secondToTime(anchorArrayList.get(position).mantic_length));
            if ((currentPlayItem == position) && (itemStatus == 2)){
                sound_anchor_play.setImageResource(R.drawable.anchor_stop);
            }else if ((currentPlayItem == position) && (itemStatus == 3)){ // 下载状态
                sound_anchor_play.setImageResource(R.drawable.anchor_play_dis);
            } else {
                sound_anchor_play.setImageResource(R.drawable.anchor_play);
            }
        }

    }

    public interface BgMusicItemClickListener{
        void musicItemClick(int index);
    }
}
