package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.fragment.ChannelManagementFragment;
import com.mantic.control.fragment.ClockFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mantic.control.fragment.AddClockFragment.RESPONSE_CODE_ADD;

/**
 * Created by lin on 2017/7/4.
 */

public class ChannelManagerAdapter extends RecyclerView.Adapter<ChannelManagerAdapter.ViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private String tag;
    private ArrayList<MyChannel> myChannelList;
    private CustomDialog.Builder mBuilder;
    private DataFactory mDataFactory;
    private String mTitleName;
    private ImageButton lastimageView;
    private int lastPosition;

    private ItemTouchHelper itemTouchHelper;


    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public ChannelManagerAdapter(Context mContext, Activity mActivity, String tag) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.tag = tag;
        mDataFactory = DataFactory.newInstance(mContext);
        this.myChannelList = mDataFactory.getMyChannelList();

    }
    public ChannelManagerAdapter(Context mContext, Activity mActivity, String tag,String titleName) {
        this(mContext,mActivity,tag);
        mTitleName=titleName;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ChannelManagementFragment.isFromClock){
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.channel_management_listitem2,parent,false),mContext);
        }else{
            return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.channel_management_listitem, parent, false), mContext);
        }


    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final MyChannel myChannel = myChannelList.get(position);
        holder.channelTitle.setText(myChannel.getChannelName());

        GlideImgManager.glideLoaderCircle(mContext, myChannel.getChannelCoverUrl(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, holder.channelIcon);
        if(ChannelManagementFragment.isFromClock){
            if (mTitleName.contains(holder.channelTitle.getText())){
                holder.channel_del_btn.setVisibility(View.VISIBLE);
                lastimageView=holder.channel_del_btn;
            }
           // holder.channelDesc.setVisibility(View.GONE);
           // holder.channelDesc.setText(String.format(mContext.getString(R.string.music_count), myChannel.getmTotalCount()));
            holder.rl_channel_management.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mOnItemClickListener!=null){
                        if (lastimageView!=null&&lastPosition!=position){
                            lastimageView.setVisibility(View.INVISIBLE);

                        }
                        holder.channel_del_btn.setVisibility(View.VISIBLE);
                        lastimageView=holder.channel_del_btn;
                        lastPosition=position;
                        if (mOnItemClickListener!=null){
                            mOnItemClickListener.onItemClick(myChannel,position);

                        }

                    }
                }
            });
        }else {
            holder.channelDesc.setText(myChannel.getChannelDescribe(mContext));
            holder.channel_del_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBuilder = new CustomDialog.Builder(mContext);
                    mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
                    mBuilder.setMessage(mContext.getString(R.string.sure_delete_my_channel));
                    mBuilder.setPositiveButton(mContext.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
                        @Override
                        public void onPositiveClick(final CustomDialog dialog) {
                            Call<MyChannelDeleteRsBean> deleteCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).postMyChannelDeleteQuest(MopidyTools.getHeaders(),Util.createDeleteRqBean(myChannel, mContext));
                            MyChannelManager.getInstance().deleteMyChannel(deleteCall, new Callback<MyChannelDeleteRsBean>() {
                                @Override
                                public void onResponse(Call<MyChannelDeleteRsBean> call, Response<MyChannelDeleteRsBean> response) {
                                    if (response.isSuccessful() && null == response.errorBody()) {
                                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.success_delete_this_channel), true);
                                        mDataFactory.removeDefinnitionMyChannel(mContext, myChannel);
                                        mDataFactory.removeMyChannelAt(position);
                                        mDataFactory.notifyMyChannelListChanged();
                                        if (null != mDataFactory.getMyChannelList() && mDataFactory.getMyChannelList().size() > 0) {
                                            notifyDataSetChanged();
                                            dialog.dismiss();
                                        } else {
                                            dialog.dismiss();
                                            if (mActivity instanceof FragmentEntrust) {
                                                ((FragmentEntrust) mActivity).popFragment(tag);
                                            }
                                        }
                                        mDataFactory.notifyMyLikeMusicStatusChange();
                                    } else {
                                        mDataFactory.notifyOperatorResult( mContext.getString(R.string.fail_delete_my_channel), false);
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyChannelDeleteRsBean> call, Throwable t) {
                                    mDataFactory.notifyOperatorResult( mContext.getString(R.string.fail_delete_my_channel), false);
                                    dialog.dismiss();
                                }
                            }, myChannel);
                        }
                    });
                    mBuilder.setNegativeButton(mContext.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                        @Override
                        public void onNegativeClick(CustomDialog dialog) {
                            dialog.dismiss();
                        }
                    });
                    mBuilder.create().show();
                }
            });
        }



    }

    @Override
    public int getItemCount() {
        return myChannelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private ImageView channelIcon;
        private TextView channelTitle;
        private TextView channelDesc;
        private RelativeLayout rl_channel_setting;
        private RelativeLayout rl_channel_management;
        private ImageButton channel_del_btn;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.channelIcon = (ImageView) itemView.findViewById(R.id.channel_icon);
            this.channelTitle = (TextView) itemView.findViewById(R.id.channel_title);

            this.channel_del_btn = (ImageButton) itemView.findViewById(R.id.channel_del_btn);
            if (!ChannelManagementFragment.isFromClock){
                this.channelDesc = (TextView) itemView.findViewById(R.id.channel_desc);
                this.rl_channel_setting = (RelativeLayout) itemView.findViewById(R.id.rl_channel_setting);
                rl_channel_setting.setOnLongClickListener(this);
            }

            this.rl_channel_management = (RelativeLayout) itemView.findViewById(R.id.rl_channel_management);

        }


        @Override
        public boolean onLongClick(View v) {
            if (v == rl_channel_setting) {
                itemTouchHelper.startDrag(this);
            }
            return false;
        }
    }
    public interface OnItemClickListener {
        void onItemClick(MyChannel channel,int position);
    }
    private ChannelManagerAdapter.OnItemClickListener mOnItemClickListener;

    public void setmOnItemClickListener(ChannelManagerAdapter.OnItemClickListener mOnItemClickListener) {
       this.mOnItemClickListener=mOnItemClickListener;
    }
}
