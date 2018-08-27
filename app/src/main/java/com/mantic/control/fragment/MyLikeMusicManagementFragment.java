package com.mantic.control.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.adapter.MyLikeMusicManagerAdapter;
import com.mantic.control.data.Channel;
import com.mantic.control.itemtouch.DefaultItemTouchHelpCallback;
import com.mantic.control.itemtouch.DefaultItemTouchHelper;
import com.mantic.control.manager.MyLikeManager;
import com.mantic.control.widget.TitleBar;

import java.util.ArrayList;
import java.util.Collections;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-20.
 */
public class MyLikeMusicManagementFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener {
    private static final String TAG = " ";
    private RecyclerView rcv_my_like_manager;
    private LinearLayoutManager mLinearLayoutManager;
    private MyLikeMusicManagerAdapter myLikeMusicManagerAdapter;
    private TitleBar tb_my_like_management;
    private ArrayList<Channel> myLikeMusicList;

    private Vibrator vibrator;
    /**
     * 滑动拖拽的帮助类
     */
    private DefaultItemTouchHelper itemTouchHelper;

    private boolean isNeedUpdateMyLike = false;

    @Override
    public void onLeftClick() {
        if (getActivity() instanceof FragmentEntrust) {
            ((FragmentEntrust) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        myLikeMusicList = mDataFactory.getMyLikeMusicList();

        if (mActivity instanceof MainActivity) {
            ((MainActivity) mActivity).setAudioPlayerVisible(false);
        }

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        this.mLinearLayoutManager = new LinearLayoutManager(getActivity());
        this.rcv_my_like_manager.setLayoutManager(this.mLinearLayoutManager);
        this.rcv_my_like_manager.addItemDecoration(new MyLikeListItemDecoration(mActivity));
        this.myLikeMusicManagerAdapter = new MyLikeMusicManagerAdapter(mContext, mActivity, getTag());
        this.rcv_my_like_manager.setAdapter(this.myLikeMusicManagerAdapter);

        // 把ItemTouchHelper和itemTouchHelper绑定
        itemTouchHelper = new DefaultItemTouchHelper(onItemTouchCallbackListener);
        itemTouchHelper.attachToRecyclerView(rcv_my_like_manager);
        myLikeMusicManagerAdapter.setItemTouchHelper(itemTouchHelper);
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
            if (myLikeMusicList != null) {
                // 更换数据源中的数据Item的位置
                Collections.swap(myLikeMusicList, srcPosition, targetPosition);
                // 更新UI中的Item的位置，主要是给用户看到交互效果
                myLikeMusicManagerAdapter.notifyItemMoved(srcPosition, targetPosition);
                mDataFactory.setMyLikeMusicList(myLikeMusicList);
                mDataFactory.notifyMyLikeMusicListChange();
                vibrator.vibrate(80);
                isNeedUpdateMyLike = true;
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
        tb_my_like_management = (TitleBar) view.findViewById(R.id.tb_my_like_management);
        tb_my_like_management.setOnButtonClickListener(this);
        this.rcv_my_like_manager = (RecyclerView) view.findViewById(R.id.rcv_my_like_manager);
    }

    @Override
    protected void onLazyLoad() {
        super.onLazyLoad();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.my_like_management_fragment;
    }


    @Override
    public void onDestroy() {
        if (isNeedUpdateMyLike) {
            isNeedUpdateMyLike = false;
            MyLikeManager.getInstance().updateMyLike(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && null == response.errorBody()) {

                    } else {
                        mDataFactory.notifyOperatorResult(getString(R.string.failed_update_my_like), false);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mDataFactory.notifyOperatorResult(getString(R.string.failed_update_my_like), false);
                }
            }, mContext, mDataFactory.getMyLikeUri(), myLikeMusicList);

        }
        ((MainActivity) mActivity).setAudioPlayerVisible(true);
        super.onDestroy();
    }

    public class MyLikeListItemDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private Paint paint;
        private int dividerHeight = 1;

        public MyLikeListItemDecoration(Context context) {
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
}
