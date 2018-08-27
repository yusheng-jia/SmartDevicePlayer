package com.mantic.control.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.MyChannel;
import com.mantic.control.fragment.ChannelDetailsFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 网友歌曲适配器
 */
public class EntertainmentNetizenAdapter extends RecyclerView.Adapter<EntertainmentNetizenAdapter.ViewHolder> {

    private Context mContext;
    private List<MyChannel> netizenList = new ArrayList<MyChannel>();
    private WindowManager wm;
    private int width = 0;

    public EntertainmentNetizenAdapter(Context context) {
        super();
        this.mContext = context;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.entertainment_netizen_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyChannel myChannel = netizenList.get(position);
        ViewGroup.LayoutParams layoutParams = holder.iv_entertainment_netizen.getLayoutParams();
        layoutParams.height = layoutParams.width = (width - DensityUtils.dip2px(mContext, 42)) / 3;
        holder.iv_entertainment_netizen.setLayoutParams(layoutParams);
        holder.tv_netizen_describe.setText(myChannel.getChannelName());
        GlideImgManager.glideLoaderCircle(mContext, myChannel.getChannelCoverUrl(), R.drawable.audio_player_album_cover_default,
                R.drawable.audio_player_album_cover_default, holder.iv_entertainment_netizen);
        holder.ll_entertainment_netizen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChannelDetailsFragment fragment = new ChannelDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID, myChannel.getMusicServiceId());
                bundle.putLong(ChannelDetailsFragment.CHANNEL_TOTAL_COUNT, myChannel.getmTotalCount());
                bundle.putString(ChannelDetailsFragment.CHANNEL_ID, myChannel.getChannelId());
                bundle.putString(ChannelDetailsFragment.CHANNEL_NAME, myChannel.getChannelName());
                bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "qingting");
                bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
                bundle.putString(ChannelDetailsFragment.ALBUM_ID, myChannel.getAlbumId());
                bundle.putString(ChannelDetailsFragment.MAIN_ID, myChannel.getMainId());
                bundle.putString(ChannelDetailsFragment.CHANNEL_COVER_URL, myChannel.getChannelCoverUrl());
                bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, myChannel.getChannelIntro());
                bundle.putString(ChannelDetailsFragment.CHALLEL_SINGER, myChannel.getSingerName());
                fragment.setArguments(bundle);
                if (mContext instanceof FragmentEntrust) {
                    ((FragmentEntrust) mContext).pushFragment(fragment, "EntertainmentFragment");
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return netizenList.size() > 3 ? 3 : netizenList.size();
    }


    public void setNetizenList(List<MyChannel> netizenList) {
        this.netizenList = netizenList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout ll_entertainment_netizen;
        public ImageView iv_entertainment_netizen;
        public TextView tv_netizen_describe;

        public ViewHolder(View itemView) {
            super(itemView);

            ll_entertainment_netizen = (LinearLayout) itemView.findViewById(R.id.ll_entertainment_netizen);
            iv_entertainment_netizen = (ImageView) itemView.findViewById(R.id.iv_entertainment_netizen);
            tv_netizen_describe = (TextView) itemView.findViewById(R.id.tv_netizen_describe);
        }
    }

}
