package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.manager.DragLayoutManager;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.manager.MyLikeManager;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/7/4.
 */

public class MyLikeMusicManagerAdapter extends RecyclerView.Adapter<MyLikeMusicManagerAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private String tag;
    private ArrayList<Channel> myLikeMusicList;
    private DataFactory mDataFactory;


    private ItemTouchHelper itemTouchHelper;


    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public MyLikeMusicManagerAdapter(Context mContext, Activity mActivity, String tag) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.tag = tag;
        mDataFactory = DataFactory.newInstance(mContext);
        this.myLikeMusicList = mDataFactory.getMyLikeMusicList();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_like_management_listitem, parent, false), mContext);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Channel channel = myLikeMusicList.get(position);
        holder.tv_my_like_item_name.setText(channel.getName());
        if (0 == channel.getLastSyncTime()) {
            holder.tv_album_sync_time.setVisibility(View.GONE);
        } else {
            holder.tv_album_sync_time.setVisibility(View.VISIBLE);
            holder.tv_album_sync_time.setText(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
        }

        if (channel.getDuration() != 0) {
            holder.tv_album_duration.setText(String.format(mContext.getString(R.string.album_time), TimeUtil.secondToTime((int) channel.getDuration())));
            holder.tv_album_duration.setVisibility(View.VISIBLE);
        } else {
            holder.tv_album_duration.setVisibility(View.GONE);
        }


        if (0 == channel.getLastSyncTime() && channel.getDuration() == 0) {
            holder.ll_my_like_item_album_info.setVisibility(View.GONE);
        } else {
            holder.ll_my_like_item_album_info.setVisibility(View.VISIBLE);
        }

        GlideImgManager.glideLoaderCircle(mContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.iv_my_like_item_icon);

        holder.iv_my_like_item_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLikeManager.getInstance().deleteMyLike(new Callback<MyLikeDeleteRsBean>() {
                    @Override
                    public void onResponse(Call<MyLikeDeleteRsBean> call, Response<MyLikeDeleteRsBean> response) {
                        if (response.isSuccessful() && null == response.errorBody()) {
                            myLikeMusicList.remove(channel);
                            mDataFactory.setMyLikeMusicList(myLikeMusicList);
                            notifyDataSetChanged();
                            mDataFactory.notifyMyLikeMusicStatusChange();
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.delete_favourite), true);
                            mDataFactory.notifyMyLikeMusicListChange();
                            if (null == myLikeMusicList || myLikeMusicList.size() == 0) {
                                if(mActivity instanceof FragmentEntrust){
                                    ((FragmentEntrust) mActivity).popAllFragment();
                                    DragLayoutManager.getAppManager().removeAllDragLyout();
                                }
                            }
                        } else {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_delete_favourite), false);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyLikeDeleteRsBean> call, Throwable t) {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.failed_delete_favourite), false);
                    }
                }, channel, mContext);
            }
        });
    }

    @Override
    public int getItemCount() {
        return myLikeMusicList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private ImageView iv_my_like_item_icon;
        private ImageView iv_my_like_item_delete;
        private ImageView iv_my_like_item_setting;
        private TextView tv_my_like_item_name;
        private TextView tv_album_sync_time;
        private TextView tv_album_duration;
        private LinearLayout ll_my_like_item_album_info;

        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.iv_my_like_item_icon = (ImageView) itemView.findViewById(R.id.iv_my_like_item_icon);
            this.iv_my_like_item_delete = (ImageView) itemView.findViewById(R.id.iv_my_like_item_delete);
            this.iv_my_like_item_setting = (ImageView) itemView.findViewById(R.id.iv_my_like_item_setting);
            this.tv_my_like_item_name = (TextView) itemView.findViewById(R.id.tv_my_like_item_name);
            this.tv_album_sync_time = (TextView) itemView.findViewById(R.id.tv_album_sync_time);
            this.tv_album_duration = (TextView) itemView.findViewById(R.id.tv_album_duration);
            this.ll_my_like_item_album_info = (LinearLayout) itemView.findViewById(R.id.ll_my_like_item_album_info);

            this.iv_my_like_item_setting.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            if (v == iv_my_like_item_setting) {
                itemTouchHelper.startDrag(this);
            }
            return false;
        }
    }
}
