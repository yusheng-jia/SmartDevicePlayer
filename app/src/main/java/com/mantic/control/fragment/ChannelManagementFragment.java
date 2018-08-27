package com.mantic.control.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.ChannelManagerAdapter;
import com.mantic.control.api.mychannel.bean.MyChannelListPositionChangeRsBean;
import com.mantic.control.data.MyChannel;
import com.mantic.control.itemtouch.DefaultItemTouchHelpCallback;
import com.mantic.control.itemtouch.DefaultItemTouchHelper;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-20.
 */
public class ChannelManagementFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener,ChannelManagerAdapter.OnItemClickListener{
    private static final String TAG = "ChannelManagementFragment";
    private RecyclerView rcv_channel_manager;
    private LinearLayoutManager mLinearLayoutManager;
    private ChannelManagerAdapter mChannelListItemAdapter;
    private TitleBar tb_channel_management;
    private ArrayList<MyChannel> myChannelList;

    private Vibrator vibrator;
    public static boolean isFromClock=false;
    private static int RESPONSE_CODE=0X233;
    /**
     * 滑动拖拽的帮助类
     */
    private DefaultItemTouchHelper itemTouchHelper;

    private String titleName;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments().containsKey("addchannel")){
            if (getArguments().getString("addchannel").contains("choosechannel")){
                isFromClock=true;
                titleName=getArguments().getString("addchannel").split(" ")[1];

            }else {
                isFromClock=false;
            }

        }
    }

    @Override
    public void onLeftClick() {
        if (getActivity() instanceof FragmentEntrust) {
            ((FragmentEntrust) getActivity()).popFragment(getTag());

        }else if (getActivity() instanceof ClockCallbacks) {
            ((ClockCallbacks) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        myChannelList = mDataFactory.getMyChannelList();

        if(mActivity instanceof FragmentEntrust){
            ((MainActivity) mActivity).setAudioPlayerVisible(false);
        }

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.mLinearLayoutManager = new LinearLayoutManager(getActivity());
        this.rcv_channel_manager.setLayoutManager(this.mLinearLayoutManager);
        this.rcv_channel_manager.addItemDecoration(new ChannelListItemDecoration(getActivity()));

        if (isFromClock){
            this.mChannelListItemAdapter = new ChannelManagerAdapter(mContext, mActivity, getTag(),titleName);
            this.mChannelListItemAdapter.setmOnItemClickListener(this);




        }else {
            this.mChannelListItemAdapter = new ChannelManagerAdapter(mContext, mActivity, getTag());
        }
        this.rcv_channel_manager.setAdapter(this.mChannelListItemAdapter);

        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(rcv_channel_manager);
        mChannelListItemAdapter.setItemTouchHelper(itemTouchHelper);
        itemTouchHelper.setDragEnable(false);
        itemTouchHelper.setSwipeEnable(true);
    }


    private DefaultItemTouchHelpCallback.OnItemTouchCallbackListener onItemTouchCallbackListener = new DefaultItemTouchHelpCallback.OnItemTouchCallbackListener() {
        @Override
        public void onSwiped(int adapterPosition) {
            return;
        }

        @Override
        public boolean onMove(int srcPosition, int targetPosition) {
            if (myChannelList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(myChannelList, srcPosition, targetPosition);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                mChannelListItemAdapter.notifyItemMoved(srcPosition, targetPosition);
                mDataFactory.notifyMyChannelListChanged();
                vibrator.vibrate(80);
                return true;
            }
            return false;
        }

        @Override
        public void onMoveEnd() {

        }
    };

    @Override
    protected void initView(View view) {
        super.initView(view);
        tb_channel_management = (TitleBar) view.findViewById(R.id.tb_channel_management);
        tb_channel_management.setOnButtonClickListener(this);
        this.rcv_channel_manager = (RecyclerView) view.findViewById(R.id.rcv_channel_manager);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.channel_management_fragment;
    }


    @Override
    public void onDestroy() {
        MyChannelManager.getInstance().changeMyChannelListPosition(new Callback<MyChannelListPositionChangeRsBean>() {
            @Override
            public void onResponse(Call<MyChannelListPositionChangeRsBean> call, Response<MyChannelListPositionChangeRsBean> response) {

            }

            @Override
            public void onFailure(Call<MyChannelListPositionChangeRsBean> call, Throwable t) {

            }
        }, mDataFactory.getMyChannelList(), mContext);
        if(mActivity instanceof FragmentEntrust){
            ((MainActivity) mActivity).setAudioPlayerVisible(true);
        }

        super.onDestroy();
    }

    public class ChannelListItemDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private Paint paint;
        private int dividerHeight = 1;

        public ChannelListItemDecoration(Context context) {
            this.ctx = context;
            this.paint = new Paint();
            this.paint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = dividerHeight;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount; i++) {
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + dividerHeight;
                c.drawRect(left, top, right, bottom, this.paint);
            }
        }
    }

    @Override
    public void onItemClick(MyChannel channel,int position) {
        String imageUrl=channel.getChannelCoverUrl();
        String title=channel.getChannelName();
        String url=channel.getUrl();
        backResult(imageUrl,title,url);
       // if (getActivity() instanceof ClockCallbacks) {
         //   ((ClockCallbacks) getActivity()).popFragment(getTag());
       // }
    }

    private void backResult(String clocktime,String hint,String url) {

        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("imageUrl", clocktime);
        intent.putExtra("title",hint);
        intent.putExtra("channelUrl",url);
        getTargetFragment().onActivityResult(AddClockFragment.REQUEST_CODE_ADD,ChannelManagementFragment.RESPONSE_CODE, intent);

            }
}
