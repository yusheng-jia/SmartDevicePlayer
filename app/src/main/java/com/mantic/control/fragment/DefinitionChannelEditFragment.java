package com.mantic.control.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.DefinitionChannelEditItemAdapter;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelSaveRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.DefinitionChannelDetailsItemDecoration;
import com.mantic.control.itemtouch.DefaultItemTouchHelpCallback;
import com.mantic.control.itemtouch.DefaultItemTouchHelper;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.ScrollEditText;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/7/3.
 * 自定义频道编辑
 */

public class DefinitionChannelEditFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener, DefinitionChannelEditItemAdapter.OnDeleteChannelListener {
    private RelativeLayout rl_channel_detail_header_view;
    private ScrollEditText edit_definition_channel_detail_channel_name;
    private TextView tv_definition_channel_detail_singer_name;
    private TextView tv_definition_channel_detail_album_name;
    private TextView tv_channel_last_sync_time;
    private ImageView iv_definition_channel_detail_cover;
    private EditText edit_channel_desc;
    private RecyclerView rcv_definition_channel_detail;
    private DefinitionChannelEditItemAdapter adapter;
    private TitleBar tb_definition_channel;
    private CustomDialog.Builder mBuilder;
    private ProgressDialog dialog;

    private ArrayList<Channel> channelList;

    private IFragmentBackHandled backHandled;
    private Vibrator vibrator;


    private String channelName;
    private String singerName;
    private String syncTime;
    private String channelCoverUrl;
    private String albumTags;
    private String channelInfo;
    private String mainId;
    private String albumId;
    private String url;

    /**
     * 滑动拖拽的帮助类
     */
    private DefaultItemTouchHelper itemTouchHelper;

    public interface  IFragmentBackHandled {
        void setSelectedFragment(DefinitionChannelEditFragment selectedFragment);
    }

    public interface OnRefreshMyChannelListener {
        void refreshMyChannel(MyChannel myChannel);
        void refreshFail(String content);
    }

    private OnRefreshMyChannelListener onRefreshMyChannelListener;

    public void setRefreshMyChannelListener(OnRefreshMyChannelListener onRefreshMyChannelListener) {
        this.onRefreshMyChannelListener = onRefreshMyChannelListener;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IFragmentBackHandled) {
            backHandled = (IFragmentBackHandled) activity;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        backHandled.setSelectedFragment(this);
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        channelName = arguments.getString("channelName");
        singerName = arguments.getString("singerName");
        syncTime = arguments.getString("syncTime");
        channelCoverUrl = arguments.getString("channelCoverUrl");
        albumTags = arguments.getString("albumTags");
        channelInfo = arguments.getString("channelInfo");
        mainId = arguments.getString("mainId");
        albumId = arguments.getString("albumId");
        url = arguments.getString("url");
        channelList = (ArrayList<Channel>) arguments.getSerializable("channelList");
        if (!TextUtils.isEmpty(channelInfo)) {
            edit_channel_desc.setText(channelInfo);
        }
        if (!TextUtils.isEmpty(syncTime)) {
            tv_channel_last_sync_time.setVisibility(View.VISIBLE);
            tv_channel_last_sync_time.setText(syncTime);
        } else {
            tv_channel_last_sync_time.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(albumTags)) {
            tv_definition_channel_detail_album_name.setVisibility(View.VISIBLE);
            tv_definition_channel_detail_album_name.setText(albumTags);
        } else {
            tv_definition_channel_detail_album_name.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(channelName)) {
            edit_definition_channel_detail_channel_name.setVisibility(View.VISIBLE);
            edit_definition_channel_detail_channel_name.setText(channelName);
        } else {
            edit_definition_channel_detail_channel_name.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(singerName)) {
            tv_definition_channel_detail_singer_name.setVisibility(View.VISIBLE);
            tv_definition_channel_detail_singer_name.setText(singerName);
        } else {
            tv_definition_channel_detail_singer_name.setVisibility(View.GONE);
        }

        if (channelCoverUrl != null && !channelCoverUrl.isEmpty()) {
            GlideImgManager.glideLoaderCircle(mContext, channelCoverUrl, R.drawable.fragment_channel_detail_cover,
                    R.drawable.fragment_channel_detail_cover, iv_definition_channel_detail_cover);
        }


        adapter = new DefinitionChannelEditItemAdapter(mContext, rl_channel_detail_header_view, channelList);
        adapter.setOnDeleteChannelListener(this);
        rcv_definition_channel_detail.setAdapter(adapter);
        rcv_definition_channel_detail.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_definition_channel_detail.addItemDecoration(new DefinitionChannelDetailsItemDecoration(mContext));


        mBuilder = new CustomDialog.Builder(mContext);
        mBuilder.setTitle(getString(R.string.dialog_btn_prompt));
        mBuilder.setMessage(getString(R.string.sure_exit_definition_channel));
        mBuilder.setPositiveButton(getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
            @Override
            public void onPositiveClick(final CustomDialog dialog) {
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).popFragment(getTag());
                }
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton(getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomDialog dialog) {
                dialog.dismiss();
            }
        });

        dialog = new ProgressDialog(mContext);
        dialog.setMessage(getString(R.string.saving));

        vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(rcv_definition_channel_detail);
        adapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(true);

        Selection.selectAll(edit_channel_desc.getText());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               showSoftKeyboard(edit_channel_desc, mContext);
                           }

                       },
                500);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rl_channel_detail_header_view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.channel_edit_header_view, null, false);
        initHeadView(rl_channel_detail_header_view);
        rcv_definition_channel_detail = (RecyclerView) view.findViewById(R.id.rcv_definition_channel_detail);
        tb_definition_channel = (TitleBar) view.findViewById(R.id.tb_definition_channel);
        tb_definition_channel.setOnButtonClickListener(this);

        ((MainActivity) getActivity()).setAudioPlayerVisible(false);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_definition_channel_edit;
    }

    public void cancelEdit() {
        mBuilder.create().show();
    }

    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onDestroy() {
        ((MainActivity) getActivity()).setAudioPlayerVisible(true);
        backHandled.setSelectedFragment(null);
        hideSoftKeyboard(edit_channel_desc, mContext);

        if (mBuilder != null) {
            mBuilder = null;
        }
        super.onDestroy();
    }

    private void initHeadView(RelativeLayout view) {
        edit_definition_channel_detail_channel_name = (ScrollEditText) view.findViewById(R.id.edit_definition_channel_detail_channel_name);
        iv_definition_channel_detail_cover = (ImageView) view.findViewById(R.id.iv_definition_channel_detail_cover);
        tv_definition_channel_detail_singer_name = (TextView) view.findViewById(R.id.tv_definition_channel_detail_singer_name);
        tv_definition_channel_detail_album_name = (TextView) view.findViewById(R.id.tv_definition_channel_detail_album_name);
        tv_channel_last_sync_time = (TextView) view.findViewById(R.id.tv_channel_last_sync_time);
        edit_channel_desc = (EditText) view.findViewById(R.id.edit_channel_desc);
    }



    public void hideSoftKeyboard(EditText editText, Context context) {
        if (editText != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(EditText editText, Context context) {
        if (editText != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, 0);
        }

    }

    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            return;
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            int src = srcPosition - 1;
            int target = targetPosition - 1;
            if (channelList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(channelList, src, target);
                vibrator.vibrate(80);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                adapter.notifyItemMoved(srcPosition, targetPosition);
                return true;
            }
            return false;
        }

        @Override
        public void onMoveEnd() {

        }
    };


    @Override
    public void onLeftClick() {
        mBuilder.create().show();
    }

    @Override
    public void onRightClick() {
        if (TextUtils.isEmpty(edit_definition_channel_detail_channel_name.getText().toString().trim())) {
            mDataFactory.notifyOperatorResult(getString(R.string.channel_name_cannot_null), false);
            return;
        }

        if (Util.isExistMyChannelName(edit_definition_channel_detail_channel_name.getText().toString().trim(), url,mDataFactory.getDefinitionMyChannelList())) {
            mDataFactory.notifyOperatorResult(getString(R.string.channel_name_cannot_repeat), false);
            return;
        }


        dialog.show();

        final MyChannel myChannel = new MyChannel();
        myChannel.setSelfDefinition(true);
        if (null != channelList && channelList.size() > 0) {
            myChannel.setChannelCoverUrl(channelList.get(0).getIconUrl());
        }
        myChannel.setChannelIntro(edit_channel_desc.getText().toString().trim());
        myChannel.setMainId(mainId);
        myChannel.setChannelName(edit_definition_channel_detail_channel_name.getText().toString().trim());
        myChannel.setmTotalCount(channelList.size());
        myChannel.setAlbumId(albumId);
        myChannel.setChannelTags(albumTags);
        myChannel.setChannelList(channelList);
        myChannel.setUrl(url);
        myChannel.setChannelType(101);

        Call<MyChannelSaveRsBean> updateCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).
                postMyChannelSaveQuest(MopidyTools.getHeaders(),Util.createSaveListRqBean(myChannel, mContext));

        MyChannelManager.saveMyChannel(updateCall, new Callback<MyChannelSaveRsBean>() {
            @Override
            public void onResponse(Call<MyChannelSaveRsBean> call, Response<MyChannelSaveRsBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != onRefreshMyChannelListener) {
                        onRefreshMyChannelListener.refreshMyChannel(myChannel);
                    }
                    dialog.dismiss();
                    
                    if (mActivity instanceof FragmentEntrust) {
                        ((FragmentEntrust) mActivity).popFragment(getTag());
                    }
                } else {
                    if (null != onRefreshMyChannelListener) {
                        onRefreshMyChannelListener.refreshFail(getString(R.string.save_fail));
                    }

                    if (mActivity instanceof FragmentEntrust) {
                        ((FragmentEntrust) mActivity).popFragment(getTag());
                    }
                }
            }

            @Override
            public void onFailure(Call<MyChannelSaveRsBean> call, Throwable t) {
                if (null != onRefreshMyChannelListener) {
                    onRefreshMyChannelListener.refreshFail(getString(R.string.save_fail));
                }
                dialog.dismiss();

                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).popFragment(getTag());
                }
            }
        }, mContext);
    }


    @Override
    public void deleteChannel(int position) {
        channelList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
