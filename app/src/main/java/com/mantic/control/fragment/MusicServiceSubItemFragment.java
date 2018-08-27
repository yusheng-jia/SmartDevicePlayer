package com.mantic.control.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.adapter.MusicServiceSubItemListAdapter;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.manager.DragLayoutManager;
import com.mantic.control.musicservice.IMusicServiceSubItem;
import com.mantic.control.musicservice.IMusicServiceSubItemList;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.ResourceUtils;

import java.util.ArrayList;

/**
 * Created by root on 17-4-19.
 */
public class MusicServiceSubItemFragment extends BaseSlideFragment implements IMusicServiceSubItemList,
        View.OnClickListener, DataFactory.MyChannelListListener {
    private static final String TAG = "MusicServiceSubItemFragment";
    public static final String SUB_ITEM_TITLE = "sub_item_title";
    private String mServiceId;
    private MyMusicService mMyMusicService;

    private MusicServiceSubItemListAdapter mMusicServiceSubItemListAdapter;

    private LinearLayout mRequestView;
    private ImageView mCircularProgressView;
    private TextView mRequestErrorText;
    private TextView mDefaultMusicServiceText;
    private LinearLayout fragment_channel_details_toolbar;
    private ImageView iv_upload_song;
    private View view_under_titlebar;
    private LinearLayout ll_music_service_subitem_back;
    private TextView tv_music_service_item_close;
    private TextView tv_music_service_item_title;
    private RecyclerView musicServiceSubitemList;


    private AnimationDrawable netAnimation;


    private ArrayList<IMusicServiceSubItem> iMusicServiceSubItemList = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mServiceId = this.getArguments().getString(MyMusicService.MY_MUSIC_SERVICE_ID);
        /*
        ArrayList<DataFactory.MusicService> musicServiceList = this.mDataFactory.getMusicServiceList();
        for(int i = 0;i < musicServiceList.size();i++){
            MyMusicService myMusicService = musicServiceList.get(i).getMyMusicService();

            if(myMusicService == null || myMusicService.getMusicServiceID() == null){
                continue;
            }

            if(this.mServiceId.equals(myMusicService.getMusicServiceID())){
                this.mMyMusicService = myMusicService;
                this.mMyMusicService.setIMusicServiceSubItemListCallBack(this);
                this.mMyMusicService.exec(this.getArguments());
                break;
            }
        }
        */
        /*
        this.mMyMusicService = this.mDataFactory.getMyMusicServiceFromServiceId(this.mServiceId);
        if(this.mMyMusicService != null){
            this.mMyMusicService.setIMusicServiceSubItemListCallBack(this);
            this.mMyMusicService.exec(this.getArguments());
        }
        */
    }


    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        if (arguments.getBoolean("isHideTitleBar")) {
            fragment_channel_details_toolbar.setVisibility(View.GONE);
            view_under_titlebar.setVisibility(View.GONE);
        }

        tv_music_service_item_title.setText(arguments.getString(SUB_ITEM_TITLE));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        musicServiceSubitemList.setLayoutManager(linearLayoutManager);
        musicServiceSubitemList.addItemDecoration(new MusicServiceSubItemListItemDecoration(getActivity()));
        this.mMusicServiceSubItemListAdapter = new MusicServiceSubItemListAdapter(getActivity(),mActivity, mServiceId);
        musicServiceSubitemList.setAdapter(this.mMusicServiceSubItemListAdapter);

        musicServiceSubitemList.addOnScrollListener(onScrollListener);
        /*
        this.mMyMusicService = this.mDataFactory.getMyMusicServiceFromServiceId(this.mServiceId);
        if(this.mMyMusicService != null){
            this.mMyMusicService.setIMusicServiceSubItemListCallBack(this);
            this.mMyMusicService.exec(this.getArguments());
        }
        */
        mDataFactory.registerMyChannelListListener(this);


        if(this.mRequestView != null && this.mRequestView.getVisibility() != View.VISIBLE){
            this.mRequestView.setVisibility(View.VISIBLE);
        }
        if(this.mCircularProgressView != null && this.mCircularProgressView.getVisibility() != View.VISIBLE){
            this.mCircularProgressView.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshFragment();
            }
        }, 500);

        if ("NetizenSong".equals(arguments.getString("comFrom", ""))) {
            iv_upload_song.setVisibility(View.VISIBLE);
        } else {
            iv_upload_song.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        fragment_channel_details_toolbar = (LinearLayout) view.findViewById(R.id.fragment_channel_details_toolbar);
        iv_upload_song = (ImageView) view.findViewById(R.id.iv_upload_song);
        view_under_titlebar = view.findViewById(R.id.view_under_titlebar);
        ll_music_service_subitem_back = (LinearLayout) view.findViewById(R.id.ll_music_service_subitem_back);
        tv_music_service_item_close = (TextView) view.findViewById(R.id.tv_music_service_item_close);
        tv_music_service_item_title = (TextView) view.findViewById(R.id.tv_music_service_item_title);
        ll_music_service_subitem_back.setOnClickListener(this);
        iv_upload_song.setOnClickListener(this);
        tv_music_service_item_close.setOnClickListener(this);


        this.mRequestView = (LinearLayout) view.findViewById(R.id.request_view);
        this.mCircularProgressView = (ImageView) view.findViewById(R.id.progress_view);
        this.mDefaultMusicServiceText = (TextView)view.findViewById(R.id.default_music_service_text);
        this.mRequestErrorText = (TextView) view.findViewById(R.id.request_error_text);
//        Button mRequestErrorBtn = (Button) view.findViewById(R.id.request_error_btn);
//        mRequestErrorBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                refreshFragment();
//            }
//        });
        musicServiceSubitemList = (RecyclerView) view.findViewById(R.id.music_service_subitem_list);
        this.mCircularProgressView.setBackgroundResource(R.drawable.net_loading);
        netAnimation = (AnimationDrawable)mCircularProgressView.getBackground();
        netAnimation.start();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.music_service_subitem_fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataFactory.unregisterMyChannelListListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_music_service_subitem_back:
                if(mActivity instanceof FragmentEntrust){
                    ((FragmentEntrust)getActivity()).popFragment(getTag());
                }
                break;
            case R.id.tv_music_service_item_close:
                if(mActivity instanceof FragmentEntrust){
                    ((FragmentEntrust) mActivity).popAllFragment();
                    DragLayoutManager.getAppManager().removeAllDragLyout();
                }
                break;
            case R.id.iv_upload_song:
                if(mActivity instanceof FragmentEntrust) {
                    UploadSongFragment uploadSongFragment = new UploadSongFragment();
                    ((FragmentEntrust) mActivity).pushFragment(uploadSongFragment, UploadSongFragment.class.getSimpleName());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void callback(ArrayList<MyChannel> myChannels) {
        mMusicServiceSubItemListAdapter.notifyDataSetChanged();
    }

    private void refreshFragment(){

        this.mMyMusicService = this.mDataFactory.getMyMusicServiceFromServiceId(this.mServiceId);
        if(this.mMyMusicService != null){
            this.mMyMusicService.setIMusicServiceSubItemListCallBack(this);
            this.mMyMusicService.exec(this.getArguments());
        }
        Glog.i(TAG,"mServiceId: " + this.mServiceId);
        if(this.mServiceId.equals("nodata")){
            this.mDefaultMusicServiceText.setVisibility(View.VISIBLE);
            this.mCircularProgressView.setVisibility(View.GONE);
            this.mRequestErrorText.setVisibility(View.GONE);
        }
    }

    @Override
    public void createMusicServiceSubItemList(ArrayList<IMusicServiceSubItem> items) {
        Glog.i(TAG,"createMusicServiceSubItemList............." + items.size());
        if(this.mRequestView != null && this.mRequestView.getVisibility() == View.VISIBLE){
            this.mRequestView.setVisibility(View.GONE);
        }

        iMusicServiceSubItemList = items;
        this.mMusicServiceSubItemListAdapter.setMusicServiceSubItemList(iMusicServiceSubItemList);
        this.mMusicServiceSubItemListAdapter.notifyDataSetChanged();

        this.mMusicServiceSubItemListAdapter.onLoadComplete();
    }

    @Override
    public void onEmpty() {
        Glog.i(TAG,"onEmpty..........................");
        netAnimation.stop();
        this.mCircularProgressView.setVisibility(View.VISIBLE);
        this.mCircularProgressView.setBackgroundResource(R.drawable.search_result_empty);
        this.mRequestErrorText.setVisibility(View.VISIBLE);
        this.mRequestErrorText.setText(R.string.cannot_find);

    }

    @Override
    public void onError(int code, String message) {
        Glog.i(TAG,"onError code = "+code+"---message = "+message);
        netAnimation.stop();
        if(this.mCircularProgressView != null && this.mCircularProgressView.getVisibility() != View.GONE){
            this.mCircularProgressView.setVisibility(View.VISIBLE);
        }
        this.mCircularProgressView.setBackgroundResource(R.drawable.net_failed);
        this.mRequestErrorText.setText(R.string.net_failed);
        if (!NetworkUtils.isAvailableByPing(mContext)) {
            this.mRequestErrorText.setText(R.string.net_failed);
        } else {
            this.mRequestErrorText.setText(R.string.service_problem);
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener(){

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (null == mMyMusicService) {
                return;
            }
            boolean canRefresh = mMyMusicService.isRefresh();
            LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            int lastVisibleItemPosition = lm.findLastVisibleItemPosition();
            int visibleItemCount = recyclerView.getChildCount();
            Glog.i(TAG,"canRefresh: " + canRefresh);
            if (lastVisibleItemPosition == totalItemCount-1
                    && visibleItemCount > 0 && canRefresh) {
                mMusicServiceSubItemListAdapter.onLoading();
                mMyMusicService.RefreshAlbumList();
                Glog.i(TAG,"LOAD MORE................");
            } else {
                mMusicServiceSubItemListAdapter.onLoadComplete();
            }


        }
    };


    public class MusicServiceSubItemListItemDecoration extends RecyclerView.ItemDecoration{
        private Context ctx;private int dividerHeight;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public MusicServiceSubItemListItemDecoration(Context context){
            this.ctx = context;
            this.dividerHeight = 1;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.fragmentSubitemListItemDecorationMarginLeft));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if(position == (mMusicServiceSubItemListAdapter.getItemCount()-1)) {
                outRect.bottom = ResourceUtils.dip2px(ctx, ResourceUtils.getXmlDef(ctx, R.dimen.myChannelLastItemGap));
            }else {
                outRect.bottom = this.dividerHeight;
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount ; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + dividerHeight;
                c.drawRect(left, top, right, bottom, dividerPaint);
            }
        }
    }

    
}
