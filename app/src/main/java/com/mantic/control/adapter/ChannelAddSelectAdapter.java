package com.mantic.control.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.MyChannel;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;

/**
 * Created by linbingjie on 2017/5/25.
 * mychannel适配器
 */

public class ChannelAddSelectAdapter extends RecyclerView.Adapter<ChannelAddSelectAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<MyChannel> mMyChannelList;

    public interface OnItemClickLitener {
        void onItemClick(View view, int position, boolean isSelected);
    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public ChannelAddSelectAdapter(Context context, ArrayList<MyChannel> mDefinitionChannelList) {
        mContext = context;
        this.mMyChannelList = mDefinitionChannelList;
    }

    public void setDefinitionMyChannelList(ArrayList<MyChannel> mMyChannelList) {
        this.mMyChannelList = mMyChannelList;
        notifyDataSetChanged();
    }

    @Override
    public ChannelAddSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_add_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ChannelAddSelectAdapter.ViewHolder holder, final int position) {
        holder.tv_my_channel_serial_number.setText(String.valueOf(position+1));
        MyChannel myChannel = mMyChannelList.get(position);
        GlideImgManager.glideLoaderCircle(mContext, myChannel.getChannelCoverUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_my_channel_cover);
        holder.albumSub.setText(String.format(mContext.getString(R.string.music_count), myChannel.getmTotalCount()));
        holder.albumTitle.setText(myChannel.getChannelName());
        holder.btn_my_channel_select.setVisibility(myChannel.isSelect()? View.VISIBLE : View.GONE);
        holder.rl_goto_channel_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnItemClickLitener) {
                    if (holder.btn_my_channel_select.getVisibility() == View.VISIBLE) {
                        mOnItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition(), true);
                    } else {
                        mOnItemClickLitener.onItemClick(holder.itemView, holder.getLayoutPosition(), false);
                    }

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMyChannelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_my_channel_serial_number;
        ImageView iv_my_channel_cover;
        RelativeLayout rl_goto_channel_details;
        TextView albumSub;
        TextView albumTitle;
        ImageButton btn_my_channel_select;
        public ViewHolder(View view) {
            super(view);
            tv_my_channel_serial_number = (TextView) view.findViewById(R.id.tv_my_channel_serial_number);
            iv_my_channel_cover = (ImageView) view.findViewById(R.id.iv_my_channel_cover);
            rl_goto_channel_details = (RelativeLayout) view.findViewById(R.id.rl_goto_channel_details);
            btn_my_channel_select = (ImageButton) view.findViewById(R.id.btn_my_channel_select);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf");
            tv_my_channel_serial_number.setTypeface(typeface);
            this.albumSub = (TextView) itemView.findViewById(R.id.album_sub);
            this.albumTitle = (TextView) itemView.findViewById(R.id.album_title);
        }
    }
}
