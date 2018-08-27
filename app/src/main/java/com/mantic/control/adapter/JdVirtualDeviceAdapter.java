package com.mantic.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.jd.JdDeviceResult;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/2.
 * desc:
 */
public class JdVirtualDeviceAdapter extends RecyclerView.Adapter{
    private static final String TAG = "JdSelAddressAdapter";
    private Context mContext;
    private List<JdDeviceResult.JdDevice> listDevice = new ArrayList<>();
    private RecyclerView listAddressView;

    public JdVirtualDeviceAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<JdDeviceResult.JdDevice> listDevice){
        this.listDevice = listDevice;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JdVirtualDeviceAdapter.SelAddressViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.jd_device_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof JdVirtualDeviceAdapter.SelAddressViewHolder){
            ((JdVirtualDeviceAdapter.SelAddressViewHolder) holder).showItem(position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view) {
        super.onAttachedToRecyclerView(listAddressView);
        listAddressView = view;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView view) {
        super.onDetachedFromRecyclerView(view);
        listAddressView = null;
    }

    @Override
    public int getItemCount() {
        return listDevice.size();
    }


    private class SelAddressViewHolder extends RecyclerView.ViewHolder{
        private ImageView deviceIcon;
        private TextView deviceName;
        private TextView deviceOpen;
        private TextView deviceClose;
        SelAddressViewHolder(final View itemView) {
            super(itemView);
            deviceIcon = (ImageView) itemView.findViewById(R.id.jd_device_icon);
            deviceName = (TextView) itemView.findViewById(R.id.jd_device_name);
            deviceOpen = (TextView) itemView.findViewById(R.id.jd_device_open);
            deviceClose = (TextView) itemView.findViewById(R.id.jd_device_close);

        }

        @SuppressLint("SetTextI18n")
        void showItem(int position){
            JdDeviceResult.JdDevice jdDevice = listDevice.get(position);
            GlideImgManager.glideLoader(mContext,jdDevice.getP_img_url(),R.drawable.mantic_update_icon,R.drawable.mantic_update_icon,deviceIcon);
            deviceName.setText(jdDevice.getDevice_name());
            deviceOpen.setText("“打开" + jdDevice.getDevice_name() + "”");
            deviceClose.setText("“关闭" + jdDevice.getDevice_name() + "”");
        }
    }

}
