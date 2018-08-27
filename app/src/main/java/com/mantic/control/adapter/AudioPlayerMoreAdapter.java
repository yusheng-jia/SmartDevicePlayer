package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.AudioHelper;
import com.mantic.control.R;
import com.mantic.control.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayson on 2017/6/26.
 */

public class AudioPlayerMoreAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<String> moreStringList = new ArrayList<String>();
    private int ITEM_TYPE_NORMAL = 1;
    private final int ITEM_TYPE_CANCEL = 2;
    private int ITEM_TYPE_TITLE = 3;
    private String mServiceID;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private AudioPlayerMoreAdapter.OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(AudioPlayerMoreAdapter.OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public AudioPlayerMoreAdapter (Context context, List<String> moreStringList, String serviceId) {
        mContext = context;
        mServiceID = serviceId;
        if (null != moreStringList) {
            this.moreStringList = moreStringList;
        }
    }


    public void setMoreStringList(List<String> moreStringList) {
        this.moreStringList = moreStringList;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_detail_more_item, parent, false));
        } else if (viewType == ITEM_TYPE_TITLE){
            return new ViewTitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_playlist_more_title, parent, false));
        }else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_detail_more_item_cancel, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            ((ViewTitleHolder) holder).tv_channel_detail_more.setTextColor(mContext.getResources().getColor(R.color.tip_red));
            ((ViewTitleHolder) holder).tv_channel_detail_more.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelOffset(R.dimen.channel_detail_more_list_item_textsize));
            ((ViewTitleHolder) holder).playlist_more_icon.setImageDrawable(mContext.getResources().getDrawable(AudioHelper.getImageDrawableFormServiceId(mServiceID)));
            ((ViewTitleHolder) holder).tv_channel_detail_more.setText(moreStringList.get(position));
            ((ViewTitleHolder) holder).tv_channel_detail_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mOnItemClickLitener) {
                        mOnItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition());
                    }
                }
            });
        }else {
            ((AudioPlayerMoreAdapter.ViewHolder)holder).tv_channel_detail_more.setTextColor(mContext.getResources().getColor(R.color.channelDetailMorelistItemTextColor));
            ((AudioPlayerMoreAdapter.ViewHolder)holder).tv_channel_detail_more.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelOffset(R.dimen.channel_detail_more_list_item_textsize));
            ((AudioPlayerMoreAdapter.ViewHolder)holder).tv_channel_detail_more.setText(moreStringList.get(position));
            ((AudioPlayerMoreAdapter.ViewHolder)holder).tv_channel_detail_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != mOnItemClickLitener) {
                        mOnItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition());
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return moreStringList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return ITEM_TYPE_TITLE;
        }else {
            return position == moreStringList.size() - 1 ? ITEM_TYPE_CANCEL : ITEM_TYPE_NORMAL;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_channel_detail_more;
        public ViewHolder(View view) {
            super(view);
            tv_channel_detail_more = (TextView) view.findViewById(R.id.tv_channel_detail_more);
        }
    }

    public static class ViewTitleHolder extends RecyclerView.ViewHolder {
        TextView tv_channel_detail_more;
        ImageView playlist_more_icon;
        public ViewTitleHolder(View view) {
            super(view);
            tv_channel_detail_more = (TextView) view.findViewById(R.id.tv_channel_detail_more);
            AudioHelper.setTextMarquee(tv_channel_detail_more);
            playlist_more_icon = (CircleImageView)view.findViewById(R.id.audio_playlist_more_icon);
        }
    }
}

