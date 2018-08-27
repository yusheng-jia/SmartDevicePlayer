package com.mantic.control.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;

/**
 * Created by lin on 2018/2/1.
 */

public class CustomViewPager extends ViewPager {
    private Context mContext;

    public CustomViewPager(Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height)height = h;
        }

        heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
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

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        // TODO Auto-generated method stub
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
