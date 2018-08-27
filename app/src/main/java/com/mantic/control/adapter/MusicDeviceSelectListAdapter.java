package com.mantic.control.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.data.DataFactory;
import com.mantic.control.entiy.ManticDeviceInfo;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.ResourceUtils;

import java.util.ArrayList;

/**
 * Created by linbingjie on 2017/5/25.
 * 音响列表适配器
 */

public class MusicDeviceSelectListAdapter extends RecyclerView.Adapter<MusicDeviceSelectListAdapter.ViewHolder> {
    private Context mContext;
    private DataFactory mDataFactory;
    private ArrayList<ManticDeviceInfo> manticDeviceInfoList = null;


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);
    }


    private OnItemClickLitener mOnItemClickLitener;

    public void setmOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public MusicDeviceSelectListAdapter(Context context, ArrayList<ManticDeviceInfo> manticDeviceInfoList) {
        mContext = context;
        this.manticDeviceInfoList = manticDeviceInfoList;
    }

    @Override
    public MusicDeviceSelectListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.music_device_select_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final MusicDeviceSelectListAdapter.ViewHolder holder, final int position) {
        final ManticDeviceInfo manticDeviceInfo = manticDeviceInfoList.get(position);
        holder.tv_music_device_name.setText(manticDeviceInfo.getDeviceName());
        if (manticDeviceInfo.isOnLine()) {
            holder.v_music_device_state.setBackground(mContext.getResources().getDrawable(R.drawable.music_device_online));

        } else {
            holder.v_music_device_state.setBackground(mContext.getResources().getDrawable(R.drawable.music_device_offline));
        }

        if (manticDeviceInfo.isBind()) {
            holder.ll_music_device.setBackground(mContext.getResources().getDrawable(R.drawable.music_device_selected_border));
            holder.tv_music_device_name.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holder.ll_music_device.setBackground(null);
            holder.tv_music_device_name.setTextColor(mContext.getResources().getColor(R.color.music_device_offline_text));
        }

        holder.ll_music_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manticDeviceInfo.isOnLine()) {
                    if (null != mOnItemClickLitener) {
                        mOnItemClickLitener.onItemClick(holder.itemView, position);
                    }
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.device_offline), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }


    @Override
    public int getItemCount() {
        return manticDeviceInfoList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        View v_music_device_state;
        TextView tv_music_device_name;
        LinearLayout ll_music_device;
        public ViewHolder(View view) {
            super(view);
            v_music_device_state = view.findViewById(R.id.v_music_device_state);
            tv_music_device_name = (TextView) view.findViewById(R.id.tv_music_device_name);
            ll_music_device = (LinearLayout) view.findViewById(R.id.ll_music_device);
        }
    }
}
