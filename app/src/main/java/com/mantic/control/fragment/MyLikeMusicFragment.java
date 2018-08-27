package com.mantic.control.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.adapter.ChannelDetailMoreAdapter;
import com.mantic.control.adapter.MyLikeMusicAdapter;
import com.mantic.control.api.mylike.bean.MyLikeDeleteRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.decoration.ChanneDetailMoreItemDecoration;
import com.mantic.control.manager.MyLikeManager;
import com.mantic.control.utils.AudioPlayerUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;
import com.mantic.control.utils.Utility;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lin on 2017/6/16.
 */

public class MyLikeMusicFragment extends BaseSlideFragment implements View.OnClickListener,
        MyLikeMusicAdapter.OnItemMoreClickListener, ChannelDetailMoreAdapter.OnItemClickLitener,
        DataFactory.OnMyLikeMusicListener, DataFactory.BeingPlayListListener {
    private TextView tv_my_like_music_size;
    private DataFactory mDataFactory;
    private List<Channel> mMyLikeMusicList;
    private MyLikeMusicAdapter mMyLikeMusicAdapter;
    private RecyclerView rv_my_like_music;
    private RelativeLayout rl_my_like_music_random;
    private LinearLayout ll_my_like_music_back;
    private TextView tv_my_like_back;
    private ImageView iv_my_like_back;
    private ImageView iv_my_like_operator;

    private ChannelDetailMoreAdapter mChannelDetailMoreAdapter;
    private Dialog mDialog;

    private ArrayList<Channel> channelList = new ArrayList<>();
    private List<String> moreStringList;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        rv_my_like_music = (RecyclerView) view.findViewById(R.id.rv_my_like_music);
        tv_my_like_music_size = (TextView) view.findViewById(R.id.tv_my_like_music_size);
        rl_my_like_music_random = (RelativeLayout) view.findViewById(R.id.rl_my_like_music_random);
        ll_my_like_music_back = (LinearLayout) view.findViewById(R.id.ll_my_like_music_back);
        iv_my_like_back = (ImageView) view.findViewById(R.id.iv_my_like_back);
        iv_my_like_operator = (ImageView) view.findViewById(R.id.iv_my_like_operator);
        tv_my_like_back = (TextView) view.findViewById(R.id.tv_my_like_back);
        rl_my_like_music_random.setOnClickListener(this);
        iv_my_like_operator.setOnClickListener(this);

        tv_my_like_back.setClickable(false);
        iv_my_like_back.setClickable(false);
        ll_my_like_music_back.setClickable(true);
        iv_my_like_back.setFocusable(false);
        tv_my_like_back.setFocusable(false);
        ll_my_like_music_back.setFocusable(true);
        ll_my_like_music_back.setOnClickListener(this);
    }

    @Override
    protected void onLazyLoad() {
        mDataFactory = DataFactory.getInstance();
        mMyLikeMusicList = mDataFactory.getMyLikeMusicList();
        Glog.i("MyLikeMusicFragment", "onLazyLoad: " + mMyLikeMusicList.size());
        if (mMyLikeMusicList.size() == 0) {
            tv_my_like_music_size.setVisibility(View.GONE);
        } else {
            tv_my_like_music_size.setVisibility(View.VISIBLE);
            tv_my_like_music_size.setText(String.format(getString(R.string.channel_album_total_size), mMyLikeMusicList.size()));
        }

        mMyLikeMusicAdapter = new MyLikeMusicAdapter(mContext, mActivity, mMyLikeMusicList);
        mMyLikeMusicAdapter.setOnItemMoreClickListener(this);
        rv_my_like_music.setAdapter(mMyLikeMusicAdapter);
        rv_my_like_music.setLayoutManager(new LinearLayoutManager(getContext()));
        rv_my_like_music.addItemDecoration(new MyLikeMusicItemDecoration(mContext) );
        rv_my_like_music.setNestedScrollingEnabled(false);
//        mMyLikeMusicAdapter.onStart();
        if (mMyLikeMusicList.size() > 6) {
            mMyLikeMusicAdapter.showLoadComplete();
        } else {
            mMyLikeMusicAdapter.showEmpty();
        }

        mDialog = Utility.getDialog(mContext, R.layout.channel_detail_more_adapter);
        RecyclerView rv_channel_detail_more = (RecyclerView) mDialog.findViewById(R.id.rv_channel_detail_more);
        rv_channel_detail_more.setLayoutManager(new LinearLayoutManager(mContext));
        rv_channel_detail_more.addItemDecoration(new ChanneDetailMoreItemDecoration(mContext));
        moreStringList = new ArrayList<String>();
        moreStringList.add("我喜欢的声音");
        moreStringList.add(getString(R.string.add_music_to_definition_mychannel));
        moreStringList.add(getString(R.string.next_play));
        moreStringList.add(getString(R.string.last_play));
        moreStringList.add(getString(R.string.delete));
        moreStringList.add(getString(R.string.cancel));
        mChannelDetailMoreAdapter = new ChannelDetailMoreAdapter(mContext, moreStringList);
        mChannelDetailMoreAdapter.setmOnItemClickLitener(this);
        rv_channel_detail_more.setAdapter(mChannelDetailMoreAdapter);

        mDataFactory.registerMyLikeMusiceListListener(this);
        mDataFactory.registerBeingPlayListListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        this.mMyLikeMusicAdapter.onStop();
        mDataFactory.unregisterMyLikeMusiceListListener(this);
        mDataFactory.unregisterBeingPlayListListener(this);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_my_like_music;
    }


    @Override
    public void changeMyLikeMusicCount() {
        tv_my_like_music_size.setText(String.format(getString(R.string.channel_album_total_size), mDataFactory.getMyLikeMusicCount()));
        mMyLikeMusicList = mDataFactory.getMyLikeMusicList();
        Glog.i(TAG, "changeMyLikeMusicCount: " + mMyLikeMusicList.size());
        mMyLikeMusicAdapter.setChannelList(mMyLikeMusicList);
    }

    @Override
    public void callback(ArrayList<Channel> beingPlayList) {
        ArrayList<Channel> list = mDataFactory.getMyLikeMusicList();
        for (int i = 0; i < list.size(); i++) {
            Channel channel = list.get(i);
            if (null == mDataFactory.getCurrChannel()) {
                channel.setPlayState(Channel.PLAY_STATE_STOP);
            } else {
                if (channel.getName().equals(mDataFactory.getCurrChannel().getName()) && channel.getUri().equals(mDataFactory.getCurrChannel().getUri())) {
                    channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
                } else {
                    channel.setPlayState(Channel.PLAY_STATE_STOP);
                }
            }

            list.set(i, channel);
        }

        mMyLikeMusicAdapter.setChannelList(list);
    }

    @Override
    public void moreClickListener(int position) {
        channelList.clear();
        channelList.add(mMyLikeMusicList.get(position));
        moreStringList.set(0, channelList.get(0).getName());
        mChannelDetailMoreAdapter.setMoreStringList(moreStringList);
        mDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {

        switch (position) {
            case 1:
                ChannelAddFragment channelAddFragment = new ChannelAddFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("channelList", channelList);
                channelAddFragment.setArguments(bundle);
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).pushFragment(channelAddFragment, "MyLikeMusicFragment");
                }
                break;

            case 2:
                AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.NEXT_INSERT, mContext, channelList);
                break;
            case 3:
                AudioPlayerUtil.insertChannel(mDataFactory, AudioPlayerUtil.LAST_INSERT, mContext, channelList);
                break;
            case 4:

                MyLikeManager.getInstance().deleteMyLike(new Callback<MyLikeDeleteRsBean>() {
                    @Override
                    public void onResponse(Call<MyLikeDeleteRsBean> call, Response<MyLikeDeleteRsBean> response) {
                        if (response.isSuccessful() && null == response.errorBody()) {
                            mDataFactory.notifyOperatorResult(getString(R.string.delete_favourite), true);
                            mMyLikeMusicList.remove(channelList.get(0));
                            mMyLikeMusicAdapter.setChannelList(mMyLikeMusicList);
                            mDataFactory.notifyMyLikeMusicStatusChange();
                            mDataFactory.notifyMyLikeMusicListChange();
                            if (null == mMyLikeMusicList || mMyLikeMusicList.size() == 0) {
                                if(mActivity instanceof FragmentEntrust){
                                    ((FragmentEntrust)getActivity()).popFragment(getTag());
                                }
                            }
                        } else {
                            mDataFactory.notifyOperatorResult(getString(R.string.failed_delete_favourite), true);
                        }
                    }

                    @Override
                    public void onFailure(Call<MyLikeDeleteRsBean> call, Throwable t) {
                        mDataFactory.notifyOperatorResult(getString(R.string.failed_delete_favourite), true);
                    }
                }, channelList.get(0), mContext);

                break;

            default:
                break;
        }

        mDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_my_like_music_random:
                if (null != mMyLikeMusicList && mMyLikeMusicList.size() > 0) {
                    int randomSize = (int) (Math.random() * mMyLikeMusicList.size());
                    mMyLikeMusicAdapter.playPause(randomSize, null);
                    rv_my_like_music.scrollToPosition(randomSize);
                }
                break;
            case R.id.ll_my_like_music_back:
                if(mActivity instanceof FragmentEntrust){
                    ((FragmentEntrust)getActivity()).popFragment(getTag());
                }
                break;
            case R.id.iv_my_like_operator:
                MyLikeMusicManagementFragment fragment = new MyLikeMusicManagementFragment();
                if(mActivity instanceof FragmentEntrust){
                    ((FragmentEntrust) getActivity()).pushFragment(fragment, MyLikeMusicManagementFragment.class.getName());
                }
                break;
        }
    }


    public class MyLikeMusicItemDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private int divideHeight = 1;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public MyLikeMusicItemDecoration(Context context){
            this.ctx = context;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.fragmentChannelDetailCoverMarginLeft));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
            divideHeight = 1;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount - 1; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + divideHeight;
                if (i == childCount - 1) {
                    c.drawRect(0, top, right, bottom, dividerPaint);
                } else {
                    c.drawRect(left, top, right, bottom, dividerPaint);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = divideHeight;
        }
    }
    
}
