package com.mantic.control.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mantic.control.R;
import com.mantic.control.utils.ResourceUtils;

/**
 * Created by lin on 2017/7/27.
 */

public class ChanneDetailMoreItemDecoration extends RecyclerView.ItemDecoration {
    private Context ctx;
    private int divideHeight = 1;
    private int divideMarginLeft;
    private Paint dividerPaint;

    public ChanneDetailMoreItemDecoration(Context context){
        this.ctx = context;
        this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx, R.dimen.channel_detail_more_list_item_left_padding));
        this.dividerPaint = new Paint();
        this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.audioPlayerPlaylistIndicatorColor));
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
            if (i ==0 || i == childCount - 2) {
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