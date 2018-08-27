package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.Channel;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jia on 2017/6/5.
 */

public class DefinitionChannelEditItemAdapter extends RecyclerView.Adapter<DefinitionChannelEditItemAdapter.ViewHolder> {
    private static final String TAG = "DefinitionChannelEditItemAdapter";
    private Context mContext;
    private ArrayList<Channel> channelList;

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;  //说明是带有头部的
    private View mHeaderView;

    private ItemTouchHelper itemTouchHelper;


    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    public interface OnDeleteChannelListener {
        void deleteChannel(int position);
    }

    private OnDeleteChannelListener onDeleteChannelListener;


    public void setOnDeleteChannelListener(OnDeleteChannelListener onDeleteChannelListener) {
        this.onDeleteChannelListener = onDeleteChannelListener;
    }

    public DefinitionChannelEditItemAdapter(Context mContext, View mHeaderView, ArrayList<Channel> channelList) {
        this.mContext = mContext;
        if (null == mHeaderView) {
            throw new RuntimeException("HeaderView can't be null!");
        }
        this.mHeaderView = mHeaderView;
        this.channelList = channelList;
    }

    @Override
    public void onBindViewHolder(DefinitionChannelEditItemAdapter.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public DefinitionChannelEditItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER && mHeaderView != null) {
            return new DefinitionChannelEditItemAdapter.ViewHolder(mHeaderView);
        } else {
            return new DefinitionChannelEditItemAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.definition_channel_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(DefinitionChannelEditItemAdapter.ViewHolder holder, final int position) {
        if (position == 0) {
            return;
        }

        Channel channel = channelList.get(position - 1);
        holder.tv_album_sync_time.setText(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
        if (channel.getDuration() != 0) {
            holder.tv_album_duration.setText(String.format(mContext.getString(R.string.album_time), TimeUtil.secondToTime((int) channel.getDuration())));
            holder.tv_album_duration.setVisibility(View.VISIBLE);
        } else {
            holder.tv_album_duration.setVisibility(View.GONE);
        }


        holder.definition_channel_item_name.setText(channel.getName());
        if (0 == channel.getLastSyncTime()) {
            holder.tv_album_sync_time.setVisibility(View.GONE);
        } else {
            holder.tv_album_sync_time.setVisibility(View.VISIBLE);
            holder.tv_album_sync_time.setText(TimeUtil.getDateFromMillisecond(channel.getLastSyncTime()));
        }

        holder.definition_channel_del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onDeleteChannelListener) {
                    CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
                    mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
                    mBuilder.setMessage(mContext.getString(R.string.sure_delete_definition_channel));
                    mBuilder.setPositiveButton(mContext.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
                        @Override
                        public void onPositiveClick(final CustomDialog dialog) {
                            onDeleteChannelListener.deleteChannel(position - 1);
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

    @Override
    public int getItemCount() {
        return channelList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_NORMAL;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, View.OnLongClickListener {
        ImageButton definition_channel_del_btn;
        ImageButton definition_channel_setting_btn;
        TextView definition_channel_item_name;
        TextView tv_album_sync_time;
        TextView tv_album_duration;
        RelativeLayout definition_channel_item_view;

        public ViewHolder(View view) {
            super(view);

            if (view == mHeaderView) {
                return;
            }
            definition_channel_item_view = (RelativeLayout) view.findViewById(R.id.definition_channel_item_view);
            definition_channel_del_btn = (ImageButton) view.findViewById(R.id.definition_channel_del_btn);
            definition_channel_setting_btn = (ImageButton) view.findViewById(R.id.definition_channel_setting_btn);
            definition_channel_item_name = (TextView) view.findViewById(R.id.definition_channel_item_name);
            tv_album_sync_time = (TextView) view.findViewById(R.id.tv_album_sync_time);
            tv_album_duration = (TextView) view.findViewById(R.id.tv_album_duration);
            definition_channel_setting_btn.setOnTouchListener(this);
            definition_channel_item_view.setOnLongClickListener(this);
        }


        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (view == definition_channel_setting_btn)
                itemTouchHelper.startDrag(this);
            return false;
        }

        @Override
        public boolean onLongClick(View v) {
            if (v == definition_channel_item_view) {
                itemTouchHelper.startDrag(this);
            }
            return false;
        }
    }
}
