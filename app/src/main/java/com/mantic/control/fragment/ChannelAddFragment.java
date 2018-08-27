package com.mantic.control.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.ChannelAddSelectAdapter;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.AddTrack;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDetailRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.ChannelAddItemDecoration;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.ScrollEditText;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lbj on 17-5-22.
 */
public class ChannelAddFragment extends BaseSlideFragment implements ChannelAddSelectAdapter.OnItemClickLitener,
        View.OnClickListener, TitleBar.OnButtonClickListener {
    private static final String TAG = "ChannelAddFragment";
    private ChannelAddSelectAdapter mChannelAddSelectAdapter;

    private RelativeLayout rl_channel_add_root;
    private ScrollEditText edit_new_channel_name;
    private TitleBar tb_channel_add;
    private TextView tv_added_channel;
    private TextView tv_new_channel_name_count;
    private RelativeLayout rl_new_channel_name;
    private ImageView iv_select_channel;
    private RecyclerView rsv_my_channel;
    private ArrayList<MyChannel> mDefinitionChannelList;
    private ArrayList<MyChannel> mShowMyChannels = new ArrayList<>();
    private ArrayList<Channel> channelList;
    private String myChannelName;
    private int lastSelectPosition = -1;
    private static final int ADDTONEW = 1;
    private static final int ADDTOEXIST = 2;
    private static final int NOTOADD = 3;
    private int currentAddType = ADDTONEW;

    private MyChannel myChannel = null;


    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_channel_add;
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        ((MainActivity) mActivity).setAudioPlayerVisible(false);
        mDefinitionChannelList = mDataFactory.getDefinitionMyChannelList();
        myChannelName = arguments.getString("myChannelName");


        edit_new_channel_name.setText(Util.getEdit_new_channel_name(mContext, mDefinitionChannelList));
        if (mDefinitionChannelList.size() > 0) {
            for (int i = 0; i < mDefinitionChannelList.size(); i++) {
                if (!mDefinitionChannelList.get(i).getChannelName().equals(myChannelName)) {
                    mShowMyChannels.add(mDefinitionChannelList.get(i));
                }
            }
        }

        if (null != mShowMyChannels && mShowMyChannels.size() <= 0) {
            tv_added_channel.setVisibility(View.GONE);
        } else {
            tv_added_channel.setVisibility(View.VISIBLE);
        }
//        edit_new_channel_name.setText(getString(R.string.create_new_channel));


        channelList = (ArrayList<Channel>) arguments.getSerializable("channelList");

        mChannelAddSelectAdapter = new ChannelAddSelectAdapter(mContext, mShowMyChannels);
        mChannelAddSelectAdapter.setmOnItemClickLitener(this);
        rsv_my_channel.setAdapter(mChannelAddSelectAdapter);
        rsv_my_channel.setLayoutManager(new LinearLayoutManager(mContext));
        rsv_my_channel.addItemDecoration(new ChannelAddItemDecoration(mContext));
        rsv_my_channel.setNestedScrollingEnabled(false);
        Selection.selectAll(edit_new_channel_name.getText());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               showSoftKeyboard(edit_new_channel_name, mContext);
                           }

                       },
                500);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        tb_channel_add = (TitleBar) view.findViewById(R.id.tb_channel_add);
        tb_channel_add.setOnButtonClickListener(this);

        rl_channel_add_root = (RelativeLayout) view.findViewById(R.id.rl_channel_add_root);
        rl_channel_add_root.setOnClickListener(this);
        iv_select_channel = (ImageView) view.findViewById(R.id.iv_select_channel);
        edit_new_channel_name = (ScrollEditText) view.findViewById(R.id.edit_new_channel_name);
        edit_new_channel_name.addTextChangedListener(new EditChangedListener());
        edit_new_channel_name.setOnClickListener(this);

        tv_added_channel = (TextView) view.findViewById(R.id.tv_added_channel);
        tv_new_channel_name_count = (TextView) view.findViewById(R.id.tv_new_channel_name_count);
        rl_new_channel_name = (RelativeLayout) view.findViewById(R.id.rl_new_channel_name);
        rl_new_channel_name.setOnClickListener(this);
        rsv_my_channel = (RecyclerView) view.findViewById(R.id.rsv_my_channel);
    }


    @Override
    public void onLeftClick() {
        if (mActivity instanceof FragmentEntrust) {
            ((FragmentEntrust) mActivity).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {
        switch (currentAddType) {
            case NOTOADD:
                if (isAdded()) {
                    mDataFactory.notifyOperatorResult(getString(R.string.select_add_channel), false);
                }
                return;
            case ADDTONEW:
                if (TextUtils.isEmpty(edit_new_channel_name.getText().toString().trim()) || isExistChannelName()) {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.channel_name_cannot_be_null_or_repeat), false);
                    }
                    return;
                }
                myChannel = new MyChannel();
                myChannel.setChannelName(edit_new_channel_name.getText().toString().trim());
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
                        if (mActivity instanceof FragmentEntrust) {
                            ((FragmentEntrust) mActivity).popFragment(getTag());
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

                break;
            case ADDTOEXIST:
                myChannel = mShowMyChannels.get(lastSelectPosition);

                Call<MyChannelDetailRsBean> detailCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).
                        postMyChannelDetailQuest(MopidyTools.getHeaders(),Util.createDetailRqBean(myChannel, mContext));

                MyChannelManager.detailMyChannel(detailCall, new Callback<MyChannelDetailRsBean>() {
                    @Override
                    public void onResponse(Call<MyChannelDetailRsBean> call, Response<MyChannelDetailRsBean> response) {
                        if (response.isSuccessful() && null != response.body().result) {
                            List<AddTrack> tracks = response.body().result.tracks;
                            myChannel.getChannelList().clear();
                            myChannel.setChannelIntro(response.body().result.mantic_describe);
                            for (int i = 0; i < tracks.size(); i++) {
                                AddTrack track = tracks.get(i);
                                Channel channel = new Channel();
                                channel.setName(track.getName());
                                channel.setIconUrl(track.getMantic_image());
                                if (null != track.getMantic_artists_name()) {
                                    String singer = "";
                                    for (int z = 0; z < track.getMantic_artists_name().size(); z++) {
                                        if (z != track.getMantic_artists_name().size() - 1) {
                                            singer = singer + track.getMantic_artists_name().get(z).toString() + "，";
                                        } else {
                                            singer = singer + track.getMantic_artists_name().get(z).toString();
                                        }
                                    }
                                    channel.setSinger(singer);
                                }

                                channel.setDuration(track.getLength());
//                            channel.setLastSyncTime(track.getMantic_last_modified());
//                            channel.setServiceId(mServiceId);
                                channel.setPlayUrl(track.getMantic_real_url());
                                channel.setAlbum(myChannel.getChannelName());
                                channel.setUri(track.getUri());
                                channel.setMantic_album_name(track.getMantic_album_name());
                                myChannel.addChannel(channel);
                            }

                            if (isExistChannel(myChannel.getChannelList(), channelList)) {
                                return;
                            }

                            final ArrayList<Channel> channels = removeUpdateDuplicateChannel(myChannel.getChannelList(), channelList);

                            if (channels.size() > 0) {

                                Call<ResponseBody> updateCall = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class).
                                        postMyChannelUpdateQuest(MopidyTools.getHeaders(),Util.createUpdateRqBean(myChannel, channels, mContext));

                                MyChannelManager.updateMyChannel(updateCall, new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful() && null == response.errorBody()) {
                                            myChannel.getChannelList().addAll(channels);
                                            ArrayList<Channel> channels = removeDuplicate(myChannel.getChannelList());
                                            myChannel.setChannelCoverUrl(channels.get(0).getIconUrl());
                                            myChannel.setmTotalCount(channels.size());
                                            myChannel.setChannelList(channels);
                                            if (isAdded()) {
                                                mDataFactory.notifyOperatorResult(String.format(getString(R.string.already_add_new_channel), myChannel.getChannelName()), true);
                                            }
                                            mDataFactory.setMyChannelAt(mDataFactory.getMyChannelList().indexOf(myChannel), myChannel);
                                            mDataFactory.getDefinitionMyChannelList().set(lastSelectPosition, myChannel);

                                            SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "channels" + myChannel.getChannelId(), GsonUtil.channellistToString(myChannel.getChannelList()));
                                            SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "DefinitionMyChannelList", GsonUtil.myChannelListToString(mDataFactory.getDefinitionMyChannelList()));
                                        } else {
                                            if (isAdded()) {
                                                mDataFactory.notifyOperatorResult(getString(R.string.fail_add_new_channel), false);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        if (isAdded()) {
                                            mDataFactory.notifyOperatorResult(getString(R.string.fail_add_new_channel), false);
                                        }
                                    }
                                });
                            }
                        } else {
                            if (isAdded()) {
                                mDataFactory.notifyOperatorResult(getString(R.string.fail_add_new_channel), false);
                            }
                        }

                        if (mActivity instanceof FragmentEntrust) {
                            ((FragmentEntrust) mActivity).popFragment(getTag());
                        }
                    }

                    @Override
                    public void onFailure(Call<MyChannelDetailRsBean> call, Throwable t) {
                        if (isAdded()) {
                            mDataFactory.notifyOperatorResult(getString(R.string.fail_add_new_channel), false);
                        }
                        if (mActivity instanceof FragmentEntrust) {
                            ((FragmentEntrust) mActivity).popFragment(getTag());
                        }
                    }
                }, myChannel, mContext);

                break;
        }
/*
                ArrayList<Channel> channelList = myChannel.getChannelList();
                if (channelList.size() >= 20) {
                    List<Channel> channels = channelList.subList(0, 20);
                    ACacheUtil.putData(getContext(), "channels" + myChannel.getChannelId(), GsonUtil.channellistToString(channels));
                } else {
                    List<Channel> channels = channelList.subList(0, channelList.size());
                    ACacheUtil.putData(getContext(), "channels" + myChannel.getChannelId(), GsonUtil.channellistToString(channels));
                }*/

//        ACacheUtil.putData(mContext, "channels" + myChannel.getChannelId(), GsonUtil.channellistToString(myChannel.getChannelList()));
//        ACacheUtil.putData(mContext, "DefinitionMyChannelList", GsonUtil.myChannelListToString(mDataFactory.getDefinitionMyChannelList()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_new_channel_name:
            case R.id.edit_new_channel_name:
                currentAddType = ADDTONEW;
                iv_select_channel.setVisibility(View.VISIBLE);

                for (int i = 0; i < mShowMyChannels.size(); i++) {
                    mShowMyChannels.get(i).setSelect(false);
                }

                mChannelAddSelectAdapter.setDefinitionMyChannelList(mShowMyChannels);
                if (TextUtils.isEmpty(edit_new_channel_name.getText().toString().trim())) {
                    tb_channel_add.getRightTextView().setEnabled(false);
                    tb_channel_add.getRightTextView().setTextColor(mContext.getResources().getColor(R.color.tv_channel_add_save_unable_color));
                } else {

                    tb_channel_add.getRightTextView().setEnabled(true);
                    tb_channel_add.getRightTextView().setTextColor(mContext.getResources().getColor(R.color.text_color_black));
                }
                break;
            case R.id.rl_channel_add_root:
                hideSoftKeyboard(edit_new_channel_name, mContext);
                break;
        }
    }


    private boolean isExistChannelName() {
        for (int i = 0; i < mDefinitionChannelList.size(); i++) {
            if (mDefinitionChannelList.get(i).getChannelName().equals(edit_new_channel_name.getText().toString().trim())) {
                return true;
            }
        }

        return false;
    }


    private boolean isExistChannel(ArrayList<Channel> oldChannels, ArrayList<Channel> newChannels) {
        Glog.i(TAG, "isExistChannel: " + newChannels.size());
        Glog.i(TAG, "isExistChannel: " + oldChannels.size());
        if (newChannels.size() > 1) {
            return false;
        } else {
            for (int i = 0; i < oldChannels.size(); i++) {
                Glog.i(TAG, "newChannels.get(" + 0 + ").getName(): " + newChannels.get(0).getName());
                Glog.i(TAG, "newChannels.get(" + 0 + ").getUri(): " + newChannels.get(0).getUri());
                Glog.i(TAG, "oldChannels.get(" + i + ").getName(): " + oldChannels.get(i).getName());
                Glog.i(TAG, "oldChannels.get(" + i + ").getUri(): " + oldChannels.get(i).getUri());
                if (oldChannels.get(i).getName().equals(newChannels.get(0).getName()) &&
                        oldChannels.get(i).getUri().equals(newChannels.get(0).getUri())) {
                    if (isAdded()) {
                        mDataFactory.notifyOperatorResult(getString(R.string.channel_exist_this_source), false);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    //删除要更新里面已经存在于频道中的资源
    ArrayList<Channel> removeUpdateDuplicateChannel(ArrayList<Channel> oldChannels, ArrayList<Channel> newChannels) {
        ArrayList<Channel> channels = new ArrayList<>();
        for (int i = 0; i < newChannels.size(); i++) {
            channels.add(newChannels.get(i));
        }
        for (int i = 0; i < oldChannels.size(); i++) {
            Channel channel = oldChannels.get(i);
            for (int j = channels.size() - 1; j >= 0; j--) {
                if (channel.getName().equals(channels.get(j).getName()) && channel.getUri().equals(channels.get(j).getUri())) {
                    channels.remove(j);
                }
            }
        }

        return channels;
    }

    private ArrayList<Channel> removeDuplicate(ArrayList<Channel> list) {

        for (int i = 0; i < list.size(); i++) {
            Glog.i(TAG, "removeDuplicate: " + list.get(i).getName());
            for (int j = list.size() - 1; j > i; j--) {
                if (list.get(j).getName().equals(list.get(i).getName()) && list.get(j).getUri().equals(list.get(i).getUri())) {
                    list.remove(j);
                }
            }
        }
        return list;
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

    @Override
    public void onItemClick(View view, int position, boolean isSelected) {
        iv_select_channel.setVisibility(View.GONE);
        if (isSelected) {
            mShowMyChannels.get(position).setSelect(false);
            lastSelectPosition = -1;
            currentAddType = NOTOADD;
        } else {
            lastSelectPosition = position;
            for (int i = 0; i < mShowMyChannels.size(); i++) {
                if (i == position) {
                    mShowMyChannels.get(i).setSelect(true);
                    currentAddType = ADDTOEXIST;
                } else {
                    mShowMyChannels.get(i).setSelect(false);
                }
            }
        }
        mChannelAddSelectAdapter.notifyDataSetChanged();
        tb_channel_add.getRightTextView().setEnabled(true);
        tb_channel_add.getRightTextView().setTextColor(mContext.getResources().getColor(R.color.text_color_black));
    }


    @Override
    public void onDestroy() {
        ((MainActivity) mActivity).setAudioPlayerVisible(true);
        hideSoftKeyboard(edit_new_channel_name, mContext);
        super.onDestroy();
    }

    class EditChangedListener implements TextWatcher {
        private CharSequence temp = "";//监听前的文本
        private final int charMaxNum = 20;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp = s;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            tv_new_channel_name_count.setText(getString(R.string.channel_add_new_name_number, s.length()));
            if (s.length() == 0) {
                if (iv_select_channel.getVisibility() == View.VISIBLE) {
                    tb_channel_add.getRightTextView().setEnabled(false);
                    tb_channel_add.getRightTextView().setTextColor(mContext.getResources().getColor(R.color.tv_channel_add_save_unable_color));
                }
            } else {
                tb_channel_add.getRightTextView().setEnabled(true);
                Resources resource = getResources();
                ColorStateList csl = resource.getColorStateList(R.color.titlebar_text_background);
                tb_channel_add.getRightTextView().setTextColor(csl);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (temp.length() > charMaxNum) {
                edit_new_channel_name.setText(temp.subSequence(0, charMaxNum));
                edit_new_channel_name.setSelection(charMaxNum);
            }
        }

    }

}
