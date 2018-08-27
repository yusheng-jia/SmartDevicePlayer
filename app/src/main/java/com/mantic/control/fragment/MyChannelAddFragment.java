package com.mantic.control.fragment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.MyChannelAddAdapter;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.MyChannelAddItemDecoration;
import com.mantic.control.itemtouch.DefaultItemTouchHelpCallback;
import com.mantic.control.itemtouch.DefaultItemTouchHelper;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.ScrollEditText;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/7/5.
 */

public class MyChannelAddFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener,
        View.OnClickListener, DataFactory.OnAddChannelListener, MyChannelAddAdapter.OnDeleteChannelListener {

    private TitleBar tb_my_channel_add;
    private LinearLayout ll_new_my_channel_add;
    private LinearLayout ll_new_my_channel_info;
    private ScrollEditText edit_new_my_channel_name;
    private RecyclerView rcv_my_channel_add;
    private MyChannelAddAdapter myChannelAddAdapter;

    private ArrayList<Channel> channelList = new ArrayList<Channel>();

    /**
     * 滑动拖拽的帮助类
     */
    private DefaultItemTouchHelper itemTouchHelper;
    private Vibrator vibrator;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        mDataFactory.registerAddChannelListener(this);
        myChannelAddAdapter = new MyChannelAddAdapter(mContext, null);
        myChannelAddAdapter.setOnDeleteChannelListener(this);
        rcv_my_channel_add.setAdapter(myChannelAddAdapter);

        edit_new_my_channel_name.setText(Util.getEdit_new_channel_name(mContext, mDataFactory.getDefinitionMyChannelList()));
        // 把ItemTouchHelper和itemTouchHelper绑定
        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(rcv_my_channel_add);
        myChannelAddAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(true);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tb_my_channel_add = (TitleBar) view.findViewById(R.id.tb_my_channel_add);
        tb_my_channel_add.setOnButtonClickListener(this);
        tb_my_channel_add.getRightTextView().setEnabled(false);
        tb_my_channel_add.getRightTextView().setTextColor(mContext.getResources().getColor(R.color.tv_channel_add_save_unable_color));
        ll_new_my_channel_add = (LinearLayout) view.findViewById(R.id.ll_new_my_channel_add);
        ll_new_my_channel_info = (LinearLayout) view.findViewById(R.id.ll_new_my_channel_info);
        edit_new_my_channel_name = (ScrollEditText) view.findViewById(R.id.edit_new_my_channel_name);
        ll_new_my_channel_info.setVisibility(View.GONE);
        ll_new_my_channel_add.setOnClickListener(this);
        rcv_my_channel_add = (RecyclerView) view.findViewById(R.id.rcv_my_channel_add);
        rcv_my_channel_add.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_my_channel_add.addItemDecoration(new MyChannelAddItemDecoration(mContext));
        rcv_my_channel_add.setNestedScrollingEnabled(false);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_my_channel_add;
    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            return;
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (channelList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(channelList, srcPosition, targetPosition);
                vibrator.vibrate(80);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                myChannelAddAdapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }

        @Override
        public void onMoveEnd() {

        }
    };


    @Override
    public void onDestroy() {
        hideSoftKey();
        mDataFactory.unregisterAddChannelListener(this);
        super.onDestroy();
    }


    @Override
    public void deleteChannel(int position) {
        channelList.remove(position);
        if (null == channelList || channelList.size() == 0) {
            ll_new_my_channel_info.setVisibility(View.GONE);
            tb_my_channel_add.getRightTextView().setEnabled(false);
            tb_my_channel_add.getRightTextView().setTextColor(mContext.getResources().getColor(R.color.tv_channel_add_save_unable_color));
        }
        myChannelAddAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLeftClick() {
        hideSoftKey();
        if (mActivity instanceof FragmentEntrust) {
            ((FragmentEntrust) mActivity).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {
        hideSoftKey();
        if (TextUtils.isEmpty(edit_new_my_channel_name.getText().toString().trim()) || isExistChannelName()) {
            if (isAdded()) {
                mDataFactory.notifyOperatorResult(getString(R.string.channel_name_cannot_be_null_or_repeat), false);
            }
            return;
        }

        final MyChannel myChannel = new MyChannel();
        myChannel.setChannelName(edit_new_my_channel_name.getText().toString().trim());
        myChannel.setmTotalCount(channelList.size());
        myChannel.setSelfDefinition(true);
        myChannel.setChannelType(101);
//                        myChannel.getChannelList().addAll(channelList);
        myChannel.setChannelList(channelList);
//                        myChannel.setChannelId("selfDefinition" + myChannel.getChannelName());
        myChannel.setChannelId(channelList.get(0).getIconUrl());
        myChannel.setAlbumId(myChannel.getChannelId());
        myChannel.setChannelCoverUrl(channelList.get(0).getIconUrl());
        myChannel.setChannelId(channelList.get(0).getIconUrl());
        myChannel.setMusicServiceId("definition");

        Call<MyChannelAddRsBean> addCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).
                postMyChannelAddQuest(MopidyTools.getHeaders(),Util.createAddRqBean(myChannel, mContext));
        MyChannelManager.addMyChannel(addCall, new Callback<MyChannelAddRsBean>() {
            @Override
            public void onResponse(Call<MyChannelAddRsBean> call, Response<MyChannelAddRsBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(String.format(getString(R.string.already_add_new_channel), myChannel.getChannelName()), true);
                    }
                    myChannel.setUrl(response.body().result.uri);
                    mDataFactory.addMyChannel(myChannel);
                    mDataFactory.getDefinitionMyChannelList().add(myChannel);
                } else {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.fail_add_new_channel), false);
                    }

                }
            }

            @Override
            public void onFailure(Call<MyChannelAddRsBean> call, Throwable t) {
                if (isAdded()) {
                    mDataFactory.notifyOperatorResult(getString(R.string.fail_add_new_channel), false);
                }
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).popFragment(getTag());
                }
            }
        }, myChannel);

        if (mActivity instanceof FragmentEntrust) {
            ((FragmentEntrust) mActivity).popFragment(getTag());
        }

    }


    private void hideSoftKey() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
    }


    private boolean isExistChannelName() {
        for (int i = 0; i < mDataFactory.getDefinitionMyChannelList().size(); i++) {
            if (mDataFactory.getDefinitionMyChannelList().get(i).getChannelName().equals(edit_new_my_channel_name.getText().toString().trim())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addChannel(List<Channel> list) {


        if (null != list && list.size() == 1) {
            for (int i = 0; i < channelList.size(); i++) {
                if (channelList.get(i).getName().equals(list.get(0).getName()) && channelList.get(i).getUri().equals(list.get(0).getUri())) {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.already_add_new_content), false);
                    }
                    return;
                }
            }
        }

        channelList.addAll(list);
        Glog.i(TAG, "addChannel: " + channelList.size());
        channelList = removeDuplicate(channelList);
        ll_new_my_channel_info.setVisibility(View.VISIBLE);
        tb_my_channel_add.getRightTextView().setEnabled(true);
        Resources resource = mContext.getResources();
        ColorStateList csl = resource.getColorStateList(R.color.titlebar_text_background);
        tb_my_channel_add.getRightTextView().setTextColor(csl);

        myChannelAddAdapter.setChannelList(channelList);
        if (isAdded()) {
            mDataFactory.notifyOperatorResult(mContext.getString(R.string.success_add_new_content), true);
        }
    }

    public ArrayList<Channel> removeDuplicate(ArrayList<Channel> list) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getName().equals(list.get(i).getName()) && list.get(j).getUri().equals(list.get(i).getUri())) {
                    list.remove(j);
                }
            }
        }
        return list;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_new_my_channel_add:
                SearchResultFragment searchFragment = new SearchResultFragment();
                Bundle bundle = new Bundle();
                bundle.putString("searchFrom", "MyChannelAddFragment");
                ArrayList<String> uriList = new ArrayList<String>();
                for (int i = 0; i < channelList.size(); i++) {
                    uriList.add(channelList.get(i).getUri());
                }
                
                bundle.putStringArrayList("uriList", uriList);
                searchFragment.setArguments(bundle);
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(searchFragment, searchFragment.getClass().getSimpleName());
                }
                break;
        }
    }
}
