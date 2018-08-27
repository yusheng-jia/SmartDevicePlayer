package com.mantic.control.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/3/14.
 * desc:
 */

public class SkillWebView extends WebView {
    private static final String TAG = "SkillWebView";
    private Context mContext;

    public SkillWebView(Context context) {
        super(context);
        mContext = context;
    }

    public SkillWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SkillWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        Glog.i(TAG,"dispatchTouchEvent:" + arg0);
        switch (arg0.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                DataFactory.newInstance(mContext).notifyMainViewPagerCanScroll(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                DataFactory.newInstance(mContext).notifyMainViewPagerCanScroll(true);
                break;
        }
        return super.onInterceptTouchEvent(arg0);
    }
//
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        Glog.i(TAG,"onTouchEvent:" + arg0);
        if(arg0.getAction() == MotionEvent.ACTION_DOWN){
            DataFactory.newInstance(mContext).notifyMainViewPagerCanScroll(false);
        }

        if(arg0.getAction() == MotionEvent.ACTION_MOVE){
            DataFactory.newInstance(mContext).notifyMainViewPagerCanScroll(false);
        }

        if(arg0.getAction() == MotionEvent.ACTION_UP){
            DataFactory.newInstance(mContext).notifyMainViewPagerCanScroll(true);
        }

        return super.onTouchEvent(arg0);
    }


}
