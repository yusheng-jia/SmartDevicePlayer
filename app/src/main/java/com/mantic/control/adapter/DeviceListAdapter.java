package com.mantic.control.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.fragment.DeviceAllFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayson on 2017/6/20.
 */

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceListHolder>{
    private Context mContext;
    private List<String> listDevice  = new ArrayList<String>();
    private OnItemClickListener itemClickListener = null;
    private Fragment curFragment;
    private RecyclerView deviceView;

    public DeviceListAdapter(Context context, Fragment fragment){
        mContext = context;
//        listDevice = list;
        listDevice = DeviceAllFragment.devices;

        curFragment = fragment;
    }

    /**
     *  Item 点击接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    public void setItemClickListener(OnItemClickListener listener){
        itemClickListener = listener;
    }
    @Override
    public DeviceListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DeviceListHolder(LayoutInflater.from(mContext).inflate(R.layout.device_list_item, parent,false));
    }

    @Override
    public void onBindViewHolder(DeviceListHolder holder, int position) {
        holder.deviceName.setText(listDevice.get(position));
        holder.deviceStatus.setText("设备在线");
    }

    @Override
    public int getItemCount() {
        return listDevice.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        deviceView = recyclerView;
    }

    public void updateRightImage(boolean hide){
        if (hide){
            for (int i = 0 ; i < listDevice.size(); i++){
                DeviceListAdapter.DeviceListHolder viewHolder  = (DeviceListAdapter.DeviceListHolder)deviceView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null)
                viewHolder.updateRightImage(true);
            }
        }else {
            for (int i = 0 ; i < listDevice.size(); i++){
                DeviceListAdapter.DeviceListHolder viewHolder  = (DeviceListAdapter.DeviceListHolder)deviceView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null)
                viewHolder.updateRightImage(false);
            }
        }
    }
    public class DeviceListHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView deviceStatus;
        ImageView imageView;

        DeviceListHolder(final View view) {
            super(view);
            deviceName = (TextView) view.findViewById(R.id.device_name);
            deviceStatus = (TextView) view.findViewById(R.id.device_statis);
            imageView = (ImageView) view.findViewById(R.id.device_list_item_icon);
            if (curFragment instanceof DeviceAllFragment){
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_ok));
            }else {
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.goto_enter));
            }
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null){
                        itemClickListener.onItemClick(view,getLayoutPosition());
                    }
                }
            });
        }

        public void updateRightImage(boolean hide){
            if (hide){
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_ok));
            }else {
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.goto_enter));
            }
        }
    }
}
