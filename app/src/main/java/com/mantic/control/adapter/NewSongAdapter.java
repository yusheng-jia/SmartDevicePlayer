package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 新歌适配器
 */
public class NewSongAdapter extends RecyclerView.Adapter<NewSongAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private DataFactory mDataFactory;
    private List<Channel> newSongList = new ArrayList<Channel>();
    private int currentGroup;

    public interface OnItemMoreClickListener {
        void onItemMoreClick(int position);
    }

    private OnItemMoreClickListener onItemMoreClickListener;

    public void setOnItemMoreClickListener(OnItemMoreClickListener onItemMoreClickListener) {
        this.onItemMoreClickListener = onItemMoreClickListener;
    }

    public NewSongAdapter(Context context, Activity activity, List<Channel> newSongList, int currentGroup) {
        super();
        this.mContext = context;
        this.mActivity = activity;
        this.currentGroup = currentGroup;
        mDataFactory = DataFactory.newInstance(mContext);
        if (null != newSongList) {
            this.newSongList = newSongList;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.new_song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Channel channel = newSongList.get(position);
        GlideImgManager.glideLoaderCircle(mContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_new_song_icon);
        holder.tv_new_song_name.setText(channel.getName());
        holder.tv_new_song_singer.setText(channel.getSinger());
        holder.btn_new_song_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != onItemMoreClickListener) {
                    onItemMoreClickListener.onItemMoreClick(position + currentGroup * 4);
                }
            }
        });
        holder.rl_new_song.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.rl_new_song.setClickable(false);
                playPause(channel, holder.rl_new_song);
            }
        });
    }

    public void playPause(final Channel channel, final View view) {
        AudioPlayerUtil.playOrPause(mContext, channel, view, mActivity, mDataFactory);
    }


    @Override
    public int getItemCount() {
        return newSongList.size();
    }

    public void setNewSongList(List<Channel> newSongList) {
        this.newSongList = newSongList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public RelativeLayout rl_new_song;
        public ImageView iv_new_song_icon;
        public ImageButton btn_new_song_more;
        public TextView tv_new_song_name;
        public TextView tv_new_song_singer;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_new_song = (RelativeLayout) itemView.findViewById(R.id.rl_new_song);
            iv_new_song_icon = (ImageView) itemView.findViewById(R.id.iv_new_song_icon);
            btn_new_song_more = (ImageButton) itemView.findViewById(R.id.btn_new_song_more);
            tv_new_song_name = (TextView) itemView.findViewById(R.id.tv_new_song_name);
            tv_new_song_singer = (TextView) itemView.findViewById(R.id.tv_new_song_singer);
        }
    }

}
