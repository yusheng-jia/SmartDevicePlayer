package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.DataFactory;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linbingjie on 2017/5/25.
 * 我的服务适配器
 */

public class MyMusicServiceAdapter extends RecyclerView.Adapter<MyMusicServiceAdapter.ViewHolder> {
    private Context mContext;
    private final int ITEM_TYPE_HEADER = 0;
    private final int ITEM_TYPE_NORMAL = 1;
    private final int ITEM_TYPE_ADD = 2;
    private final int ITEM_TYPE_FOOTER = 3;
    private DataFactory mDataFactory;
    private List<MusicService> mMyMusicServiceList = new ArrayList<>();

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, boolean isLast);
    }


    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public MyMusicServiceAdapter(Context context, List<MusicService> mMyMusicServiceList) {
        mContext = context;
        this.mMyMusicServiceList = mMyMusicServiceList;
        mDataFactory = DataFactory.getInstance();
    }

    public void setMyMusicServiceList(List<MusicService> mMyMusicServiceList) {
        this.mMyMusicServiceList = mMyMusicServiceList;
        notifyDataSetChanged();
    }

    @Override
    public MyMusicServiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_music_service_item, parent, false));
        } else if (viewType == ITEM_TYPE_FOOTER) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_music_service_footer, parent, false));
        } else if (viewType == ITEM_TYPE_HEADER) {
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_music_service_header, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_music_service_item_add, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final MyMusicServiceAdapter.ViewHolder holder, final int position) {
        if (position == mMyMusicServiceList.size() + 1) {
            holder.tv_my_music_service_name.setText(mContext.getString(R.string.add_service));
            holder.rl_my_music_service_item_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickLitener) {
                        mOnItemClickLitener.onItemClick(holder.itemView, position, true);
                    }
                }
            });
        } else if (position == (mMyMusicServiceList.size() + 2)) {
            holder.rl_local_music.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataFactory.notifyOperatorResult(mContext.getString(R.string.not_yet_open), false);
                }
            });
        } else if (position == 0) {

        } else {
            MusicService musicService = mMyMusicServiceList.get(position - 1);
            GlideImgManager.glideLoaderCircle(mContext, musicService.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, holder.iv_my_music_service_iten_icon);
            holder.tv_my_music_service_name.setText(musicService.getName());
            holder.rl_my_music_service_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mOnItemClickLitener) {
                        mOnItemClickLitener.onItemClick(holder.itemView, position - 1, false);
                    }
                }
            });


        }

    }


    @Override
    public int getItemCount() {
        return mMyMusicServiceList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return ITEM_TYPE_HEADER;
        } else if (position == mMyMusicServiceList.size() + 1) {
            return ITEM_TYPE_ADD;
        } /*else if (position == mMyMusicServiceList.size() + 2) {
            return ITEM_TYPE_FOOTER;
        }*/ else {
            return ITEM_TYPE_NORMAL;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_my_music_service_iten_icon;
        TextView tv_my_music_service_name;
        ImageButton btn_my_music_service_detail;
        RelativeLayout rl_my_music_service_item;
        RelativeLayout rl_my_music_service_item_add;
        RelativeLayout rl_local_music;

        public ViewHolder(View view) {
            super(view);
            iv_my_music_service_iten_icon = (ImageView) view.findViewById(R.id.iv_my_music_service_iten_icon);
            tv_my_music_service_name = (TextView) view.findViewById(R.id.tv_my_music_service_name);
            btn_my_music_service_detail = (ImageButton) view.findViewById(R.id.btn_my_music_service_detail);
            rl_my_music_service_item = (RelativeLayout) view.findViewById(R.id.rl_my_music_service_item);
            rl_my_music_service_item_add = (RelativeLayout) view.findViewById(R.id.rl_my_music_service_item_add);
            rl_local_music = (RelativeLayout) view.findViewById(R.id.rl_local_music);
        }
    }
}
