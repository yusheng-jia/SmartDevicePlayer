package com.mantic.control.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.entiy.ClassificationBean;
import com.mantic.control.utils.Glog;

import java.util.List;

import static android.R.attr.tag;

/**
 * Created by lin on 2018/1/17.
 */

public class ClassificationTitleDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private List<ClassificationBean> mList;
    public static String currentTag = "心情";
    private LayoutInflater mLayoutInflater;
    private int mTitleHight = 38;
    private int mTitleTextSize = 20;

    public ClassificationTitleDecoration(Context context, List<ClassificationBean> dataList) {
        super();
        this.mContext = context;
        this.mList = dataList;
        mTitleHight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 38, context.getResources().getDisplayMetrics());
        // mTitleTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, context.getResources().getDisplayMetrics());
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    public static void setCurrentTag(String tag) {
        ClassificationTitleDecoration.currentTag = tag;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //获取到视图中第一个可见的item的position
        int position = ((GridLayoutManager) parent.getLayoutManager()).findFirstVisibleItemPosition();
        String category = mList.get(position).getCategory();
        View child = parent.findViewHolderForLayoutPosition(position).itemView;
        boolean flag = false;
        if ((position + 1) < mList.size()) {
            String suspensionTag = mList.get(position + 1).getCategory();
            if (null != category && !category.equals(suspensionTag)) {
                Glog.d("ZHG-TEST", "!!!!!!!!!!!!!child.Height() = " + child.getHeight() + " , child.top = " + child.getTop() + " , mTitleHight = " + mTitleHight);
                if (child.getHeight() + child.getTop() < mTitleHight) {
                    c.save();
                    flag = true;
                    c.translate(0, child.getHeight() + child.getTop() - mTitleHight);
                }
            }
        }
        View topTitleView = mLayoutInflater.inflate(R.layout.classification_title_item, parent, false);
        TextView textView = (TextView) topTitleView.findViewById(R.id.tv_classification_title_name);
        //textView.setTextSize(mTitleTextSize);
        textView.setText(category);
        int toDrawWidthSpec;//用于测量的widthMeasureSpec
        int toDrawHeightSpec;//用于测量的heightMeasureSpec
        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) topTitleView.getLayoutParams();
        if (lp == null) {
            lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//这里是根据复杂布局layout的width height，new一个Lp
            topTitleView.setLayoutParams(lp);
        }
        topTitleView.setLayoutParams(lp);
        if (lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            //如果是MATCH_PARENT，则用父控件能分配的最大宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.EXACTLY);
        } else if (lp.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果是WRAP_CONTENT，则用父控件能分配的最大宽度和AT_MOST构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight(), View.MeasureSpec.AT_MOST);
        } else {
            //否则则是具体的宽度数值，则用这个宽度和EXACTLY构建MeasureSpec。
            toDrawWidthSpec = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
        }
        //高度同理
        if (lp.height == ViewGroup.LayoutParams.MATCH_PARENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.EXACTLY);
        } else if (lp.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom(), View.MeasureSpec.AT_MOST);
        } else {
            toDrawHeightSpec = View.MeasureSpec.makeMeasureSpec(mTitleHight, View.MeasureSpec.EXACTLY);
        }
        //依次调用 measure,layout,draw方法，将复杂头部显示在屏幕上。
        topTitleView.measure(toDrawWidthSpec, toDrawHeightSpec);
        topTitleView.layout(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getPaddingLeft() + topTitleView.getMeasuredWidth(), parent.getPaddingTop() + topTitleView.getMeasuredHeight());
        topTitleView.draw(c);//Canvas默认在视图顶部，无需平移，直接绘制
        if (flag)
            c.restore();//恢复画布到之前保存的状态
        if (!TextUtils.equals(category, currentTag)) {
            currentTag = category;
        }

    }
}
