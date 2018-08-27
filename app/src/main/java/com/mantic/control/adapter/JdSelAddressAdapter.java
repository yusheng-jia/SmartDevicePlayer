package com.mantic.control.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.jd.JdAddress;
import com.mantic.control.utils.Glog;

import java.util.ArrayList;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/2.
 * desc:
 */
public class JdSelAddressAdapter extends RecyclerView.Adapter{
    private static final String TAG = "JdSelAddressAdapter";
    private Context mContext;
    private List<JdAddress.Address> listAddress = new ArrayList<>();
    private RecyclerView listAddressView;
    private RadioClickListener radioClickListener;
    private long defAddressId = 0;

    public JdSelAddressAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<JdAddress.Address> listAddress){
        this.listAddress = listAddress;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new JdSelAddressAdapter.SelAddressViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.jd_address_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SelAddressViewHolder){
            ((SelAddressViewHolder) holder).showItem(position);
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
        return listAddress.size();
    }

    public void setRadioClickListener(RadioClickListener listener){
        this.radioClickListener = listener;
    }

    public void setDefAddress(long id) {
        defAddressId = id;
    }

    private class SelAddressViewHolder extends RecyclerView.ViewHolder{
        private TextView jdName;
        private TextView jdNumber;
        private TextView jdAddress;
        private RadioButton selButton;
        SelAddressViewHolder(final View itemView) {
            super(itemView);
            jdName = (TextView) itemView.findViewById(R.id.jd_name);
            jdNumber = (TextView) itemView.findViewById(R.id.jd_number);
            jdAddress = (TextView) itemView.findViewById(R.id.jd_address);
            selButton = (RadioButton) itemView.findViewById(R.id.jd_address_sel);
            selButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = listAddressView.getChildAdapterPosition(itemView);
                    Glog.i(TAG,"RadioButton onClick ... " + position);
                    if (radioClickListener != null){
                        radioClickListener.onClick(position);
                    }
                }
            });
        }

        @SuppressLint("SetTextI18n")
        void showItem(int position){
            JdAddress.Address address = listAddress.get(position);
            jdName.setText(address.getName());
            jdNumber.setText(address.getMobile());
            jdAddress.setText( address.getFull_address() + address.getAddress_detail());
            if (listAddress.get(position).getId() == defAddressId){
                selButton.setChecked(true);
            }else {
                selButton.setChecked(false);
            }
        }
    }

    public interface RadioClickListener{
        void onClick(int index);
    }
}
