package com.mantic.control.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mantic.control.R;

import java.util.List;

/**
 * Created by wujiangxia on 2017/5/3.
 */
public class FirmwareInnerAdapter extends BaseAdapter {
   private Context context;
    private List<String> list;
    public FirmwareInnerAdapter(List<String> key, Context context) {
        this.list= key;
        this.context = context;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (view != null){
            viewHolder = (ViewHolder)view.getTag();
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.firmware_inner_item, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.info = (TextView) view.findViewById(R.id.update_info);
            view.setTag(viewHolder);
        }
        viewHolder.info.setText(list.get(i));
        return view;
    }

    public class ViewHolder {
        private TextView info;


    }
}
