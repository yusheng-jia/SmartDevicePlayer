package com.mantic.control.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.activity.DeviceDetailActivity;
import com.mantic.control.adapter.DeviceListAdapter;
import com.mantic.control.decoration.DeviceItemDecoration;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.TitleBar;

import org.codehaus.jackson.map.deser.ValueInstantiators;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jayson on 2017/6/20.
 */

public class DeviceAllFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener,DeviceListAdapter.OnItemClickListener {
    private RecyclerView deviceView ;
    private View view;
    DeviceListAdapter deviceAdapter;
    private TitleBar device_all_titlebar;
    private static volatile DeviceAllFragment instance;
    LinearLayout addLayout;
    public static List<String> devices = new ArrayList<String>();
    private boolean hideAdd = true;
    private ImageView deadline;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        devices.clear();
        String name = SharePreferenceUtil.getDeviceName(mActivity);
        if (name.isEmpty()){
            name = SharePreferenceUtil.getDeviceId(mActivity);
        }
        devices.add(name);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        addLayout = (LinearLayout)view.findViewById(R.id.device_add);
        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),R.string.onedevice_support,Toast.LENGTH_SHORT).show();
            }
        });
        device_all_titlebar = (TitleBar) view.findViewById(R.id.device_all_titlebar);
        device_all_titlebar.setOnButtonClickListener(this);
        deviceView = (RecyclerView) view.findViewById(R.id.device_list);
        deadline = (ImageView)view.findViewById(R.id.deadline);
        deviceView.setLayoutManager(new LinearLayoutManager(mActivity));
        deviceView.addItemDecoration(new DeviceItemDecoration(mActivity));
        deviceAdapter = new DeviceListAdapter(getActivity(),this);
        deviceAdapter.setItemClickListener(this);
        deviceView.setAdapter(deviceAdapter);
        hideAddView(hideAdd);
    }

    private void hideAddView(boolean hide) {
        if (hide){
            addLayout.setVisibility(View.GONE);
            deadline.setVisibility(View.GONE);
            device_all_titlebar.getRightTextView().setText(getString(R.string.edit));
        }else {
            addLayout.setVisibility(View.VISIBLE);
            deadline.setVisibility(View.VISIBLE);
            device_all_titlebar.getRightTextView().setText(getString(R.string.complete));
        }
        deviceAdapter.updateRightImage(hide);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.device_all_frag;
    }

    public DeviceAllFragment(){
    }

    public static DeviceAllFragment getInstance(){
        if (instance == null){
            synchronized (DeviceAllFragment.class){
                if (instance == null){
                    instance = new DeviceAllFragment();
                }
            }
        }
        return instance;
    }


    @Override
    public void onItemClick(View view, int position) {
        //后边多设备根据 position 判断
        if (!hideAdd)
        startActivity(new Intent(getActivity(), DeviceDetailActivity.class));
    }

    @Override
    public void onLeftClick() {
        if (mActivity instanceof FragmentEntrust) {
            ((FragmentEntrust) mActivity).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {
        if (hideAdd){
            hideAddView(false);
            hideAdd = false;
        }else {
            hideAddView(true);
            hideAdd = true;
        }
    }
}
