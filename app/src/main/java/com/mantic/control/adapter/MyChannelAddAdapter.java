package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.Channel;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;

/**
 * Created by lin on 2017/7/5.
 * 增加至新频道的
 */

public class MyChannelAddAdapter extends RecyclerView.Adapter<MyChannelAddAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Channel> channelList = new ArrayList<Channel>();

    private ItemTouchHelper itemTouchHelper;

    public interface OnDeleteChannelListener {
        void deleteChannel(int position);
    }

    private OnDeleteChannelListener onDeleteChannelListener;


    public void setOnDeleteChannelListener(OnDeleteChannelListener onDeleteChannelListener) {
        this.onDeleteChannelListener = onDeleteChannelListener;
    }


    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public MyChannelAddAdapter(Context mContext, ArrayList<Channel> channelList) {
        super();
        this.mContext = mContext;
        if (null != channelList) {
            this.channelList = channelList;
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.my_channel_add_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Channel channel = channelList.get(position);
        holder.tv_my_channel_add_item_name.setText(channel.getName());
        holder.tv_my_channel_add_item_singer.setText(channel.getSinger() == null ? channel.getAlbum() : channel.getSinger());
        if (!TextUtils.isEmpty(channel.getIconUrl())) {
            GlideImgManager.glideLoaderCircle(mContext, channel.getIconUrl(), R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, holder.iv_my_channel_add_icon);
        }


        holder.btn_my_channel_add_item_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onDeleteChannelListener) {
                    CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
                    mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
                    mBuilder.setMessage(mContext.getString(R.string.sure_delete_definition_channel));
                    mBuilder.setPositiveButton(mContext.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
                        @Override
                        public void onPositiveClick(final CustomDialog dialog) {
                            onDeleteChannelListener.deleteChannel(position);
                            dialog.dismiss();
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
            }
        });
    }

    public void setChannelList(ArrayList<Channel> channelList) {
        this.channelList = channelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return channelList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        public ImageView iv_my_channel_add_icon;
        public ImageButton btn_my_channel_add_item_del_btn;
        public ImageButton btn_my_channel_add_item_setting_btn;
        public TextView tv_my_channel_add_item_name;
        public TextView tv_my_channel_add_item_singer;

        public ViewHolder(View view) {
            super(view);
            iv_my_channel_add_icon = (ImageView) view.findViewById(R.id.iv_my_channel_add_icon);
            btn_my_channel_add_item_del_btn = (ImageButton) view.findViewById(R.id.btn_my_channel_add_item_del_btn);
            btn_my_channel_add_item_setting_btn = (ImageButton) view.findViewById(R.id.btn_my_channel_add_item_setting_btn);
            tv_my_channel_add_item_name = (TextView) view.findViewById(R.id.tv_my_channel_add_item_name);
            tv_my_channel_add_item_singer = (TextView) view.findViewById(R.id.tv_my_channel_add_item_singer);
            btn_my_channel_add_item_setting_btn.setOnLongClickListener(this);
        }


        @Override
        public boolean onLongClick(View v) {
            if (null != btn_my_channel_add_item_setting_btn)
                itemTouchHelper.startDrag(this);
            return false;
        }

    }
}
