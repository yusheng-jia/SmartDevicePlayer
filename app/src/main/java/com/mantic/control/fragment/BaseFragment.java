package com.mantic.control.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.data.DataFactory;
import com.mantic.control.widget.ReboundScrollView;

/**
 * Created by lin on 2017/6/15.
 */

public abstract class BaseFragment extends Fragment implements
        ReboundScrollView.OnScrollBottomListener,
        ReboundScrollView.ScrollListener{
    /**
     * 贴附的activity
     */
    protected Activity mActivity;

    protected Context mContext;

    /**
     * 根view
     */
    protected View mRootView;

    /**
     * 是否对用户可见
     */
    protected boolean mIsVisible;
    /**
     * 是否加载完成
     * 当执行完oncreatview,View的初始化方法后方法后即为true
     */
    protected boolean mIsPrepare;

    /**
     * 有弹性的scrollview
     */
    protected ReboundScrollView mReboundScrollView;

    protected DataFactory mDataFactory;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mActivity = getActivity();
        mContext = getContext();
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(setLayoutResourceId(), container, false);

        initView(mRootView);

        initData(getArguments());

        mIsPrepare = true;

//        onLazyLoad();

        setListener();

        return mRootView;
    }

    /**
     * 初始化数据
     *
     * @param arguments 接收到的从其他地方传递过来的参数
     * @date 2016-5-26 下午3:57:48
     */
    protected void initData(Bundle arguments) {
        mDataFactory = DataFactory.newInstance(getContext());
    }

    /**
     * 初始化View
     * @date 2016-5-26 下午3:58:49
     */
    protected void initView(View view) {
      /*  mReboundScrollView = (ReboundScrollView) view.findViewById(R.id.rsv_unified_management);
        if (null != mReboundScrollView) {
            mReboundScrollView.setScrollListener(this);
        }*/
    }

    @Override
    public void srollToBottom() {

    }

    /**
     * 设置监听事件
     * @date 2016-5-26 下午3:59:36
     */
    protected void setListener() {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        
        this.mIsVisible = isVisibleToUser;

        if (isVisibleToUser) {
            onVisibleToUser();
        }
    }

    /**
     * 用户可见时执行的操作
     * @date 2016-5-26 下午4:09:39
     */
    protected void onVisibleToUser() {
        if (mIsPrepare && mIsVisible) {
            onLazyLoad();
        }
    }

    /**
     * 懒加载，仅当用户可见切view初始化结束后才会执行
     * @date 2016-5-26 下午4:10:20
     */
    protected void onLazyLoad() {

    }

    /**
     * 设置根布局资源id
     *
     * @return
     * @date 2016-5-26 下午3:57:09
     */
    protected abstract int setLayoutResourceId();


    @Override
    public void scrollOriention(int oriention) {
        /*if (null != mDataFactory.getCurrChannel()) {
            ((MainActivity) getActivity()).setAudioPlayerStatus(oriention);
        }*/
    }
}
