package com.mantic.control.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;

/**
 * Created by linbingjie on 2017/5/25.
 * mychannel适配器
 */

public class MyChannelAdapter extends RecyclerView.Adapter<MyChannelAdapter.ViewHolder>{
    private Context mContext;
    private ArrayList<MyChannel> mMyChannelList;
    private DataFactory mDataFactory;
    private View mHeaderView;


    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MyChannelAdapter(Context context, View mHeaderView) {
        mContext = context;
        if (null == mHeaderView) {
            throw new RuntimeException("HeaderView can't be null!");
        }
        this.mHeaderView = mHeaderView;
        this.mDataFactory = DataFactory.newInstance(this.mContext.getApplicationContext());
        this.mMyChannelList = this.mDataFactory.getMyChannelList();
    }

    @Override
    public MyChannelAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderView != null) {
            return new ViewHolder(mHeaderView);
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_channel_album, parent, false));
        }
    }

    public void updateMyChannelList(ArrayList<MyChannel> channels){
        this.mMyChannelList = channels;
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final MyChannelAdapter.ViewHolder holder, final int position) {
        if (position == 0) {
            return;
        }
        holder.tv_my_channel_serial_number.setText(String.valueOf(position));
        MyChannel myChannel = mMyChannelList.get(position - 1);
        GlideImgManager.glideLoaderCircle(mContext, myChannel.getChannelCoverUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_my_channel_cover);
        if (myChannel.getmTotalCount() > 0) {
            if (myChannel.getChannelType() == 2 && !TextUtils.isEmpty(myChannel.getChannelIntro())){
                holder.albumSub.setText(String.format(mContext.getString(R.string.fm_isplaying), myChannel.getChannelIntro()));
                holder.albumSub.setVisibility(View.VISIBLE);
            }else {
                holder.albumSub.setText(String.format(mContext.getString(R.string.music_count), myChannel.getmTotalCount()));
                holder.albumSub.setVisibility(View.VISIBLE);
            }
        } else {
            holder.albumSub.setVisibility(View.GONE);
        }

        holder.albumTitle.setText(myChannel.getChannelName());
        holder.rl_goto_channel_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnItemClickListener) {
                    mOnItemClickListener.onItemClick(holder.itemView, position - 1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMyChannelList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_my_channel_serial_number;
        ImageView iv_my_channel_cover;
        RelativeLayout rl_goto_channel_details;
        TextView albumSub;
        TextView albumTitle;
        public ViewHolder(View view) {
            super(view);

            if (view == mHeaderView) {
                return;
            }
            tv_my_channel_serial_number = (TextView) view.findViewById(R.id.tv_my_channel_serial_number);
            iv_my_channel_cover = (ImageView) view.findViewById(R.id.iv_my_channel_cover);
            rl_goto_channel_details = (RelativeLayout) view.findViewById(R.id.rl_goto_channel_details);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf");
            tv_my_channel_serial_number.setTypeface(typeface);
            this.albumSub = (TextView) itemView.findViewById(R.id.album_sub);
            this.albumTitle = (TextView) itemView.findViewById(R.id.album_title);
        }
    }
}
