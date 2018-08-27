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

import java.util.ArrayList;

/**
 * Created by linbingjie on 2017/5/25.
 * 服务列表适配器
 */

public class MusicServiceListAdapter extends RecyclerView.Adapter<MusicServiceListAdapter.ViewHolder> {
    private Context mContext;
    private DataFactory mDataFactory;
    private ArrayList<MusicService> mMusicServiceList = null;


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }


    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public MusicServiceListAdapter(Context context) {
        mContext = context;
        mDataFactory = DataFactory.getInstance();
        mMusicServiceList = mDataFactory.getMusicServiceList();
    }

    @Override
    public MusicServiceListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_service_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MusicServiceListAdapter.ViewHolder holder, final int position) {

        final MusicService musicService = mMusicServiceList.get(position + 2);
        holder.iv_music_service_item_icon.setImageBitmap(musicService.getIcon());
        holder.tv_music_service_name.setText(musicService.getName());
        holder.rl_music_service_list_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnItemClickLitener) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });


        holder.tv_music_service_desc.setText(musicService.getIntroduction());
        if (musicService.getIsActive()) {
            holder.tv_music_service_item_operator.setVisibility(View.VISIBLE);
            holder.tv_music_service_item_operator.setText(mContext.getString(R.string.added));
            holder.tv_music_service_item_operator.setTextColor(mContext.getResources().getColor(R.color.mainTabIndicatorColor));
        } else {
//            holder.tv_music_service_item_operator.setText(mContext.getString(R.string.add));
//            holder.tv_music_service_item_operator.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.tv_music_service_item_operator.setVisibility(View.GONE);
        }

//        holder.tv_music_service_item_operator.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mContext.getString(R.string.added).equals(holder.tv_music_service_item_operator.getText())) {
//                    musicService.setActive(false);
//                    mDataFactory.notifyBMyMusicServiceListChange(musicService, false, false);
//                    holder.tv_music_service_item_operator.setText(mContext.getString(R.string.add));
//                    holder.tv_music_service_item_operator.setTextColor(mContext.getResources().getColor(R.color.black));
//                } else if (mContext.getString(R.string.add).equals(holder.tv_music_service_item_operator.getText())){
//                    musicService.setActive(true);
//                    mDataFactory.notifyBMyMusicServiceListChange(musicService, true, false);
//                    holder.tv_music_service_item_operator.setText(mContext.getString(R.string.added));
//                    holder.tv_music_service_item_operator.setTextColor(mContext.getResources().getColor(R.color.mainTabIndicatorColor));
//                }
//
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return mMusicServiceList.size() - 2;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_music_service_item_icon;
        TextView tv_music_service_item_operator;
        TextView tv_music_service_name;
        TextView tv_music_service_desc;
        ImageButton btn_my_music_service_detail;
        RelativeLayout rl_music_service_list_item;
        public ViewHolder(View view) {
            super(view);
            iv_music_service_item_icon = (ImageView) view.findViewById(R.id.iv_music_service_item_icon);
            tv_music_service_item_operator = (TextView) view.findViewById(R.id.tv_music_service_item_operator);
            tv_music_service_name = (TextView) view.findViewById(R.id.tv_music_service_name);
            tv_music_service_desc = (TextView) view.findViewById(R.id.tv_music_service_desc);
            btn_my_music_service_detail = (ImageButton) view.findViewById(R.id.btn_my_music_service_detail);
            rl_music_service_list_item = (RelativeLayout) view.findViewById(R.id.rl_music_service_list_item);
        }
    }
}
