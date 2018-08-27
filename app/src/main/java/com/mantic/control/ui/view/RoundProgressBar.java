package com.mantic.control.ui.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.mantic.control.R;
import com.mantic.control.utils.Glog;


/**
 * RoundProgressBar.
 *
 * @author wujiangxia
 */
public class RoundProgressBar extends View {
    private static final float START_ANGLE = -90F;
    private static final float ANGLE_SCALE = 360F;
    private static final float RECT_MARGIN = 5F;
    private static final long MAX_DURATION = 120 * 1000L;
    private RectF mCanvasBounds = null;
    private Paint mBatteryArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ValueAnimator mValueAnimator;
    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;
    private int roundPathColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    //设置进度
    private float currentvalue = 1.0f;

    private PathMeasure tickPathMeasure;
    /**
     * 打钩百分比
     */
    float tickPercent = 0;

    private Path path;
    //初始化打钩路径
    private Path tickPath;
    private boolean isDrawPath;

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        roundPathColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundPathColor, Color.RED);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        tickPathMeasure = new PathMeasure();
        path = new Path();
        tickPath = new Path();
        mTypedArray.recycle();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initBounds(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int centre = (int) width / 2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2); //圆环的半径
        // Draw  circle
        mBatteryArcPaint.setColor(roundColor);
        mBatteryArcPaint.setStyle(Paint.Style.STROKE);
        mBatteryArcPaint.setStrokeWidth(roundWidth);
        mBatteryArcPaint.setAntiAlias(true);
        canvas.drawCircle(centre, centre, radius, mBatteryArcPaint); //画出圆环
        // Draw text
        mBatteryArcPaint.setStrokeWidth(0);
        mBatteryArcPaint.setColor(textColor);
        mBatteryArcPaint.setTextSize(textSize);
        mBatteryArcPaint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (currentvalue / 100.0f * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = mBatteryArcPaint.measureText(percent + "%");
        if (!isDrawPath) {//测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            if (textIsDisplayable && currentvalue != 0) {
                canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize / 2, mBatteryArcPaint); //画出进度百分比
            }
            mBatteryArcPaint.setColor(roundProgressColor);
            mBatteryArcPaint.setStyle(Paint.Style.STROKE);
            mBatteryArcPaint.setStrokeWidth(roundWidth);
            mBatteryArcPaint.setAntiAlias(true);
            canvas.drawArc(mCanvasBounds, START_ANGLE, ANGLE_SCALE * currentvalue / 100, false, mBatteryArcPaint);
        }else{
            mBatteryArcPaint.setColor(roundProgressColor);
            mBatteryArcPaint.setStyle(Paint.Style.STROKE);
            mBatteryArcPaint.setStrokeWidth(roundWidth);
            mBatteryArcPaint.setAntiAlias(true);
            canvas.drawArc(mCanvasBounds, START_ANGLE, ANGLE_SCALE , false, mBatteryArcPaint);
        }

        // Draw arc


        if (isDrawPath) {
            mBatteryArcPaint.setColor(roundPathColor);
            mBatteryArcPaint.setStyle(Paint.Style.STROKE);
            mBatteryArcPaint.setStrokeWidth(roundWidth);
            mBatteryArcPaint.setAntiAlias(true);

            Glog.v("wujx", "height:" + height + "radius" + radius);
            // 根据设置该view的高度，进行对所画图进行居中处理
            int offsetHeight = (height - radius * 2) / 2;

            // 设置第一条直线的相关参数
            int firStartX = width / 3;
            int firStartY = offsetHeight + radius;

            int firEndX = firStartX + radius / 4;
            int firEndY = firStartY + radius / 4;
            int secEndX = width / 3 + radius * 7 / 10;
            int secEndY = firEndY - radius * 2 / 5;

            Glog.v("wujx", "firStartXY:" + firStartX + ":" + firStartY);
            Glog.v("wujx", "firEndXY:" + firEndX + ":" + firEndY);
            Glog.v("wujx", "secEndXY:" + secEndX + ":" + secEndY);

            mBatteryArcPaint.setStrokeJoin(Paint.Join.ROUND);
            tickPath.moveTo(firStartX, firStartY);

            tickPath.lineTo(firEndX, firEndY);
            tickPath.lineTo(secEndX, secEndY);
            tickPathMeasure.setPath(tickPath, false);
        /*
         * On KITKAT and earlier releases, the resulting path may not display on a hardware-accelerated Canvas.
         * A simple workaround is to add a single operation to this path, such as dst.rLineTo(0, 0).
         */
            tickPathMeasure.getSegment(0, tickPercent * tickPathMeasure.getLength(), path, true);
            path.rLineTo(0, 0);
            canvas.drawPath(path, mBatteryArcPaint);
        }
        invalidate();
    }

    private void initBounds(Canvas canvas) {
        if (mCanvasBounds == null) {
            mCanvasBounds = new RectF();
            mCanvasBounds.top = RECT_MARGIN;
            mCanvasBounds.left = RECT_MARGIN;
            mCanvasBounds.right = (-RECT_MARGIN + canvas.getWidth());
            mCanvasBounds.bottom = (-RECT_MARGIN + canvas.getHeight());
        }
    }

    public void setStopValueAnimator() {

        mValueAnimator.start();
    }

    public void setCurrentValue(float value) {
        Glog.v("wujx", "currentvalue" + currentvalue + "mValueAnimator" + mValueAnimator);
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            currentvalue = 0;
        }
        mValueAnimator = ValueAnimator.ofFloat(currentvalue, value);
        mValueAnimator.setDuration(MAX_DURATION);
        mValueAnimator.setTarget(currentvalue);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                currentvalue = (float) valueAnimator.getAnimatedValue();
                Glog.v("wujx", "currentvalue" + currentvalue);
            }
        });
        mValueAnimator.start();
    }


    public void setTickPath(boolean isOK) {
        isDrawPath = isOK;
        ValueAnimator mTickAnimation;
        mTickAnimation = ValueAnimator.ofFloat(0f, 1f);

        mTickAnimation.setDuration(1000);
        mTickAnimation.setInterpolator(new AccelerateInterpolator());
        mTickAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                tickPercent = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        mTickAnimation.start();
    }


}