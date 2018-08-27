package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mantic.control.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linbingjie on 2017/5/25.
 * 频道详情更多适配器
 */

public class ChannelDetailMoreAdapter extends RecyclerView.Adapter<ChannelDetailMoreAdapter.ViewHolder>{
    private Context mContext;
    private List<String> moreStringList = new ArrayList<String>();
    private int ITEM_TYPE_NORMAL = 1;
    private final int ITEM_TYPE_CANCEL = 2;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public ChannelDetailMoreAdapter (Context context, List<String> moreStringList) {
        mContext = context;
        if (null != moreStringList) {
            this.moreStringList = moreStringList;
        }
    }


    public void setMoreStringList(List<String> moreStringList) {
        this.moreStringList = moreStringList;
        this.notifyDataSetChanged();
    }

    @Override
    public ChannelDetailMoreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE_NORMAL) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_detail_more_item, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_detail_more_item_cancel, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final ChannelDetailMoreAdapter.ViewHolder holder, final int position) {
        if (position == 0) {
            holder.tv_channel_detail_more.setTextColor(mContext.getResources().getColor(R.color.audioPlayerPlaylistItemSingerNameTextColor));
            holder.tv_channel_detail_more.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelOffset(R.dimen.channel_detail_more_list_title_textsize));
        }else {
            holder.tv_channel_detail_more.setTextColor(mContext.getResources().getColor(R.color.channelDetailMorelistItemTextColor));
            holder.tv_channel_detail_more.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelOffset(R.dimen.channel_detail_more_list_item_textsize));
        }
        holder.tv_channel_detail_more.setText(moreStringList.get(position));
        holder.tv_channel_detail_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnItemClickLitener) {
                    mOnItemClickLitener.onItemClick(holder.itemView, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return moreStringList.size();
    }

   @Override
    public int getItemViewType(int position) {
        return position == moreStringList.size() - 1 ? ITEM_TYPE_CANCEL : ITEM_TYPE_NORMAL;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_channel_detail_more;
        public ViewHolder(View view) {
            super(view);
            tv_channel_detail_more = (TextView) view.findViewById(R.id.tv_channel_detail_more);
        }
    }
}
