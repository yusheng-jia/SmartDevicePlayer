package com.mantic.control.widget;

/**
 * Created by lin on 2017/6/6.
 */

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.mantic.control.ui.view.ScrollBottomScrollView;
import com.mantic.control.utils.Glog;

/**
 * 有弹性的ScrollView
 * 实现下拉弹回和上拉弹回
 * @author zhangjg
 * @date Feb 13, 2014 6:11:33 PM
 */
public class ReboundScrollView extends ScrollView {

    private static final String TAG = "BounceScrollView";

    private View inner;// 孩子View

    private float y;// 点击时y坐标

    private Rect normal = new Rect();// 矩形(这里只是个形式，只是用于判断是否需要动画.)

    private boolean isCount = false;// 是否开始计算
    private float lastX = 0;
    private float lastY = 0;
    private float currentX = 0;
    private float currentY = 0;
    private float distanceX = 0;
    private float distanceY = 0;
    private boolean upDownSlide = false; //判断上下滑动的flag

    public ReboundScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /***
     * 根据 XML 生成视图工作完成.该函数在生成视图的最后调用，在所有子视图添加完之后. 即使子类覆盖了 onFinishInflate
     * 方法，也应该调用父类的方法，使该方法得以执行.
     */
    @Override
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            inner = getChildAt(0);
            View view;
        }
        super.onFinishInflate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        currentX = ev.getX();
        currentY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                distanceX = currentX - lastX;
                distanceY = currentY - lastY;
                if (Math.abs(distanceX) < Math.abs(distanceY) && Math.abs(distanceY) > 12) {
                    upDownSlide = true;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        lastX = currentX;
        lastY = currentY;
        if (upDownSlide && inner != null) commOnTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return super.onInterceptTouchEvent(ev);
    }

    /***
     * 监听touch
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }


    /***
     * 触摸事件
     *
     * @param ev
     */
    public void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                // 手指松开.
                if (isNeedAnimation()) {
                    animation();
                    isCount = false;
                }
                clear0();
                break;
            case MotionEvent.ACTION_MOVE:
                final float preY = y;// 按下时的y坐标
                float nowY = ev.getY();// 时时y坐标
                int deltaY = (int) (preY - nowY);// 滑动距离

                if (!isCount) {
                    deltaY = 0; // 在这里要归0.
                }

                y = nowY;
                // 当滚动到最上或者最下时就不会再滚动，这时移动布局
                if (isNeedMove()) {
                    // 初始化头部矩形
                    if (normal.isEmpty()) {
                        // 保存正常的布局位置
                        normal.set(inner.getLeft(), inner.getTop(),
                                inner.getRight(), inner.getBottom());
                    }
                    // 移动布局
                    inner.layout(inner.getLeft(), inner.getTop() - deltaY / 2,
                            inner.getRight(), inner.getBottom() - deltaY / 2);
                    if (deltaY > 30) {
                        if (mListener != null)
                            mListener.scrollOriention(SCROLL_UP);
                    } else if (deltaY < -30){
                        if (mListener != null)
                            mListener.scrollOriention(SCROLL_DOWN);
                    }
                }
                isCount = true;
                break;

            default:
                break;
        }
    }

    /***
     * 回缩动画
     */
    public void animation() {
        // 开启移动动画
        TranslateAnimation ta = new TranslateAnimation(0, 0, inner.getTop(),
                normal.top);
        ta.setDuration(400);
        ta.setInterpolator(new DecelerateInterpolator());
        inner.startAnimation(ta);
        // 设置回到正常的布局位置
        inner.layout(normal.left, normal.top, normal.right, normal.bottom);

        normal.setEmpty();

    }

    // 是否需要开启动画
    public boolean isNeedAnimation() {
        return !normal.isEmpty();
    }

    /***
     * 是否需要移动布局 inner.getMeasuredHeight():获取的是控件的总高度
     *
     * getHeight()：获取的是屏幕的高度
     *
     * @return
     */
    public boolean isNeedMove() {
        int offset = inner.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        // 0是顶部，后面那个是底部
        if (scrollY == 0 || scrollY == offset) {
            return true;
        }
        return false;
    }

    private void clear0() {
        lastX = 0;
        lastY = 0;
        distanceX = 0;
        distanceY = 0;
        upDownSlide = false;
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//        super.onScrollChanged(l, t, oldl, oldt);

        Glog.i(TAG, "onScrollChanged: " + (oldt - t));
        if (oldt > t && oldt - t >= SCROLLLIMIT) {// 向下
            if (mListener != null)
                mListener.scrollOriention(SCROLL_DOWN);
        } else if (oldt < t && t - oldt >= SCROLLLIMIT) {// 向上
            if (mListener != null)
                mListener.scrollOriention(SCROLL_UP);
        }

        View view = this.getChildAt(0);
        if (this.getHeight() + this.getScrollY() == view.getHeight()) {
            _calCount++;
            if (_calCount == 1) {
                if (_listener != null) {
                    _listener.srollToBottom();
                }
            }
        } else {
            _calCount = 0;
        }
    }

    /**
     * ScrollView正在向上滑动
     */
    public static final int SCROLL_UP = 0x01;

    /**
     * ScrollView正在向下滑动
     */
    public static final int SCROLL_DOWN = 0x10;

    /**
     * 最小的滑动距离
     */
    private static final int SCROLLLIMIT = 5;

    private ScrollListener mListener;

    public interface ScrollListener {
        void scrollOriention(int oriention);
    }


    public void setScrollListener(ScrollListener mListener) {
        this.mListener = mListener;
    }

    private ScrollBottomScrollView.OnScrollBottomListener _listener;
    private int _calCount;

    public interface OnScrollBottomListener {
        void srollToBottom();
    }

    public void registerOnScrollViewScrollToBottom(ScrollBottomScrollView.OnScrollBottomListener l) {
        _listener = l;
    }

    public void unRegisterOnScrollViewScrollToBottom() {
        _listener = null;
    }
}