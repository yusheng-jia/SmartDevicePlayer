package com.mantic.control.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
/**
 * Created by jayson on 2017/6/26.
 */

public class ShaderImageView extends ImageView {
        private RectF mTempSrc = new RectF();
        private RectF mTempDst = new RectF();
        private Matrix mDrawMatrix = new Matrix();
        private int DEFAULT_LEVEL = 30;
        private int DEFAULT_ALPHA = 0x50;
        private static final Matrix.ScaleToFit[] sS2FArray = {
                Matrix.ScaleToFit.FILL,
                Matrix.ScaleToFit.START,
                Matrix.ScaleToFit.CENTER,
                Matrix.ScaleToFit.END
        };

    public ShaderImageView(Context context) {
            super(context);
        }

    public ShaderImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

    public ShaderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ShaderImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawShaderDrawable(canvas);
        }
    private void drawShaderDrawable(Canvas canvas){
        Drawable drawable = getDrawable();
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAlpha(DEFAULT_ALPHA);
        configureBounds();
        canvas.drawBitmap(bitmap,mDrawMatrix,paint);
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void configureBounds() {
        Drawable mDrawable = getDrawable();
        int mPaddingLeft = getPaddingLeft();
        int mPaddingRight = getPaddingRight();
        int mPaddingTop = getPaddingTop();
        int mPaddingBottom = getPaddingBottom();
        ScaleType mScaleType = getScaleType();
        int dwidth = mDrawable.getIntrinsicWidth();
        int dheight = mDrawable.getIntrinsicHeight();

        int vwidth = getWidth() - mPaddingLeft - mPaddingRight;
        int vheight = getHeight() - mPaddingTop - mPaddingBottom;

        boolean fits = (dwidth < 0 || vwidth == dwidth) &&
                (dheight < 0 || vheight == dheight);

        if (dwidth <= 0 || dheight <= 0 || ScaleType.FIT_XY == mScaleType) {
            /**如果drawble没有内在的尺寸或者，以适合屏幕尺寸缩放*/
            mDrawable.setBounds(0, 0, vwidth, vheight);
            mDrawMatrix = null;
        } else {
            mDrawable.setBounds(0, 0, dwidth, dheight);

            if (ScaleType.MATRIX == mScaleType) {
                // Use the specified matrix as-is.
                if (getMatrix().isIdentity()) {
                    mDrawMatrix = null;
                } else {
                    mDrawMatrix.set(getMatrix());
                }
            } else if (fits) {
                // The bitmap fits exactly, no transform needed.
                mDrawMatrix = null;
            } else if (ScaleType.CENTER == mScaleType) {
                // Center bitmap in view, no scaling.
                mDrawMatrix.set(getMatrix());
                mDrawMatrix.setTranslate((int) ((vwidth - dwidth) * 0.5f + 0.5f),
                        (int) ((vheight - dheight) * 0.5f + 0.5f)+vheight/DEFAULT_LEVEL);
            } else if (ScaleType.CENTER_CROP == mScaleType) {
                mDrawMatrix.set(getMatrix());

                float scale;
                float dx = 0, dy = 0;

                if (dwidth * vheight > vwidth * dheight) {
                    scale = (float) vheight / (float) dheight;
                    dx = (vwidth - dwidth * scale) * 0.5f;
                } else {
                    scale = (float) vwidth / (float) dwidth;
                    dy = (vheight - dheight * scale) * 0.5f;
                }

                mDrawMatrix.setScale(scale, scale);
                float tmp_height = mDrawable.getIntrinsicHeight()*scale/DEFAULT_LEVEL;
                mDrawMatrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f)+tmp_height);
            } else if (ScaleType.CENTER_INSIDE == mScaleType) {
                mDrawMatrix.set(getMatrix());
                float scale;
                float dx;
                float dy;

                if (dwidth <= vwidth && dheight <= vheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) vwidth / (float) dwidth,
                            (float) vheight / (float) dheight);
                }

                dx = (int) ((vwidth - dwidth * scale) * 0.5f + 0.5f);
                dy = (int) ((vheight - dheight * scale) * 0.5f + 0.5f);

                mDrawMatrix.setScale(scale, scale);
                float tmp_height = mDrawable.getIntrinsicHeight()*scale/DEFAULT_LEVEL;
                mDrawMatrix.postTranslate(dx, dy+tmp_height);
            } else {
                // Generate the required transform.
                mTempSrc.set(0, 0, dwidth, dheight);
                mTempDst.set(0, 0, vwidth, vheight);
                mTempDst.set(0,mTempDst.height()/DEFAULT_LEVEL,vwidth,vheight+mTempDst.height()/DEFAULT_LEVEL);
                mDrawMatrix.set(getMatrix());
                mDrawMatrix.setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(mScaleType));
            }
        }
    }
    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType st)  {
        // ScaleToFit enum to their corresponding Matrix.ScaleToFit values
        int number = 0;
        if (st == ScaleType.MATRIX)
            number =0;
        if (st == ScaleType.FIT_XY)
            number = 1;
        if (st == ScaleType.FIT_START)
            number = 2;
        if (st == ScaleType.FIT_CENTER)
            number = 3;
        if (st == ScaleType.FIT_END)
            number = 4;
        if (st == ScaleType.CENTER)
            number = 5;
        if (st == ScaleType.CENTER_CROP)
            number = 6;
        if (st == ScaleType.CENTER_INSIDE)
            number = 7;
        return sS2FArray[number-1];
    }

}
