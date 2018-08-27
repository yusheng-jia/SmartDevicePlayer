package com.mantic.control.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;

/**
 * Created by lin on 2017/7/27.
 */

public class MyChannelAddItemDecoration extends RecyclerView.ItemDecoration {
    private Context ctx;
    private int divideHeight = 1;
    private Paint dividerPaint;

    public MyChannelAddItemDecoration(Context context) {
        this.ctx = context;
        this.dividerPaint = new Paint();
        this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
        divideHeight = 1;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        int right = parent.getWidth();

        for (int i = 0; i < childCount - 1; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + divideHeight;
            c.drawRect(0, top, right, bottom, dividerPaint);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = divideHeight;
    }
}
