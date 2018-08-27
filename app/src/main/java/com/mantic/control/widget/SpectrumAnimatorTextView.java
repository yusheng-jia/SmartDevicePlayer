package com.mantic.control.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.utils.Glog;

import java.util.Random;

/**
 * Created by root on 17-4-19.
 */
public class SpectrumAnimatorTextView extends TextView {
    private static final String TAG = "SpectrumAnimatorTextView";

    private JumpingImageSpan lineOne;
    private JumpingImageSpan lineTwo;
    private JumpingImageSpan lineThree;
    private JumpingImageSpan lineFour;

    private int showSpeed = 100;

    private int jumpHeight;
    private boolean autoPlay;
    private boolean isPlaying;
    private boolean isHide = true;
    private int period;
    private long startTime;

    private boolean lockDotOne;
    private boolean lockDotTwo;
    private boolean lockDotThree;

    private Handler handler;

    Random random = new Random();

    private AnimatorSet mAnimatorSet = new AnimatorSet();
    private float textWidth;

    private SpannableString spannable;

    public SpectrumAnimatorTextView(Context context) {
        super(context);
        init(context, null);
    }

    public SpectrumAnimatorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SpectrumAnimatorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        handler = new Handler(Looper.getMainLooper());
        //自定义属性
        /*
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaitingDots);
            period = typedArray.getInt(R.styleable.WaitingDots_period, 6000);
            jumpHeight = typedArray.getInt(R.styleable.WaitingDots_jumpHeight, (int) (getTextSize() / 4));
            autoPlay = typedArray.getBoolean(R.styleable.WaitingDots_autoplay, true);
            typedArray.recycle();
        }
        */

        //jumpHeight = 50;
        period = 500;
        lineOne = new JumpingImageSpan(context, R.drawable.spectrum_red);
        lineTwo = new JumpingImageSpan(context, R.drawable.spectrum_red);
        lineThree = new JumpingImageSpan(context, R.drawable.spectrum_red);
        lineFour = new JumpingImageSpan(context, R.drawable.spectrum_red);

        jumpHeight = lineOne.getDrawable().getBounds().height();

        //Glog.i(TAG,"lineOne.getDrawable().getBounds().height() = "+lineOne.getDrawable().getBounds().height());
        //Glog.i(TAG,"lineOne.getDrawable().getIntrinsicHeight()"+lineOne.getDrawable().getIntrinsicHeight());

        //将每个点设置为jumpingSpan类型
        //SpannableString spannable = new SpannableString(".......");
        spannable = new SpannableString(".......");

        spannable.setSpan(lineOne, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ImageSpan(context,R.drawable.spectrum_white), 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(lineTwo, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ImageSpan(context,R.drawable.spectrum_white), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(lineThree, 4, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new ImageSpan(context,R.drawable.spectrum_white), 5, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(lineFour, 6, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(spannable, TextView.BufferType.SPANNABLE);

        textWidth = getPaint().measureText(".", 0, 1);
        //一下两个是把updateListener加到点1上，通过它来进行刷新动作
        /*
        final ObjectAnimator lineOneJumpAnimator = createJumpAnimator(lineOne, 0);
        lineOneJumpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });
        lineOneJumpAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                lineOneJumpAnimator.setFloatValues(0, -random.nextInt(jumpHeight));
            }
        });
        */
        ObjectAnimator lineOneJumpAnimator = createJumpAnimator(lineOne, 0);
        ObjectAnimator lineTwoJumpAnimator = createJumpAnimator(lineTwo, 0);
        ObjectAnimator lineThreeJumpAnimator = createJumpAnimator(lineThree, 0);
        ObjectAnimator lineFourJumpAnimator = createJumpAnimator(lineFour, 0);

        //这里通过animationSet来控制三个点的组合动作
        //mAnimatorSet.playTogether(lineOneJumpAnimator, createJumpAnimator(lineTwo,
        //        period / 6), createJumpAnimator(lineThree, period * 2 / 6));
        mAnimatorSet.playTogether(lineOneJumpAnimator, lineTwoJumpAnimator, lineThreeJumpAnimator,lineFourJumpAnimator);

        isPlaying = autoPlay;
        if(autoPlay) {
            start();
        }
    }

    public void start() {
        Glog.i(TAG,"start");
        isPlaying = true;
        //一旦开始就INFINITE
        setText(spannable, TextView.BufferType.SPANNABLE);
        setAllAnimationsRepeatCount(ValueAnimator.INFINITE);
        mAnimatorSet.start();
        isFirst = true;
    }


    boolean isFirst = true;

    /*动画的实现核心
    *@param jumpingSpan 传入点，
    * @delay 动画运行延迟，通过这个参数让三个点进行有时差的运动
     */
    private ObjectAnimator createJumpAnimator(final JumpingImageSpan jumpingSpan, long delay) {
        //ObjectAnimator jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", 0, -jumpHeight);
        ObjectAnimator jumpAnimator = null;
        if (jumpingSpan == lineOne) {
            jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", -jumpHeight * 0.5f, -jumpHeight * 0.2f);
            jumpAnimator.setDuration(50);
        } else if (jumpingSpan == lineTwo){
            jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", -jumpHeight * 0.2f, jumpHeight * 0.2f);
            jumpAnimator.setDuration(30);
        } else if (jumpingSpan == lineThree){
            jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", -jumpHeight * 0.75f, jumpHeight * 0.75f);
                jumpAnimator.setDuration(100);
        } else if (jumpingSpan == lineFour){
            jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", -jumpHeight * 0.75f, jumpHeight * 0.75f);
            jumpAnimator.setDuration(50);
        }


//        jumpAnimator = ObjectAnimator.ofFloat(jumpingSpan, "translationY", -jumpHeight * 0.2f, jumpHeight * 0.3f);
//        jumpAnimator.setDuration(500);
        /*setEvaluator这个重要，功能是为了通过方程来平滑的实现点运动的“节奏感”，可以试试把这段去掉，
            你会发现点会以默认的速度上下运动，特别生硬，TypeEvaluator中的evaluate可以计算出点的当前位置。
            通过对当前点的计算间接设计了点的轨迹运动，和时间插值TimeInterpolator达到相同的效果，就好比你不知道速度但是你知道每秒所在的位置相当于速度了。
            这个计算方法是这样的：可以参见这个博文
            http://blog.csdn.net/serapme/article/details/47006049
            ValueAnimator还封装了一个TypeAnimator，根据开始、结束值与TimeIniterpolator计算得到的值计算出属性值。
            ValueAnimator根据动画已进行的时间跟动画总时间（duration）的比计算出一个时间因子（0~1），然后根据TimeInterpolator计算出另一个因子，最后TypeAnimator通过这个因子计算出属性值，如上例中10ms时：
            首先计算出时间因子，即经过的时间百分比：t=10ms/40ms=0.25
            经插值计算(inteplator)后的插值因子:大约为0.15，上述例子中用了AccelerateDecelerateInterpolator，计算公式为（input即为时间因子）：
            (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
            最后根据TypeEvaluator计算出在10ms时的属性值：0.15*（40-0）=6pixel。上例中TypeEvaluator为FloatEvaluator，计算方法为 ：
            public Float evaluate(float fraction, Number startValue, Number endValue) {
                    float startFloat = startValue.floatValue();
                    return startFloat + fraction * (endValue.floatValue() - startFloat);
             }
         */
        jumpAnimator.setEvaluator(new TypeEvaluator<Number>() {

            @Override
            public Number evaluate(float fraction, Number from, Number to) {
                return Math.max(0, Math.sin(fraction * Math.PI * 2)) * (to.floatValue() - from.floatValue());
            }
        });
        //jumpAnimator.setDuration(period);
//        jumpAnimator.setDuration(getRandomPeriod());
        jumpAnimator.setStartDelay(delay);
        jumpAnimator.setRepeatCount(ValueAnimator.INFINITE);
        jumpAnimator.setRepeatMode(ValueAnimator.REVERSE);

        jumpAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });


        jumpAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Glog.i(TAG,"jumpAnimator onAnimationRepeat random.nextInt(jumpHeight) = "+random.nextInt(jumpHeight));
//                ((ObjectAnimator)animation).setDuration(getRandomPeriod());
                if (jumpingSpan == lineOne) {
                    ((ObjectAnimator)animation).setFloatValues(jumpHeight * 0.5f, jumpHeight * 0.8f);
                    ((ObjectAnimator)animation).setDuration(500);
                } else if (jumpingSpan == lineTwo){
                    ((ObjectAnimator)animation).setFloatValues(0, jumpHeight * 0.7f);
                    ((ObjectAnimator)animation).setDuration(300);
                } else if (jumpingSpan == lineThree){
                    ((ObjectAnimator)animation).setFloatValues(jumpHeight * 0.65f, jumpHeight * 0.25f);
                    ((ObjectAnimator)animation).setDuration(600);
                    if (isFirst) {
                        isFirst = false;
                        setVisibility(VISIBLE);
                    }
                } else if (jumpingSpan == lineFour){
                    ((ObjectAnimator)animation).setFloatValues(jumpHeight * 0.25f, jumpHeight * 0.75f);
                    ((ObjectAnimator)animation).setDuration(500);
                }

            }
        });

        return jumpAnimator;
    }

    private int getRandomPeriod(){
        return 500;
    }

    //以下部分非核心功能也没难度就不注释了~主要是因为懒~
    public void stop() {
        Glog.i(TAG,"stop");
        isPlaying = false;
        setAllAnimationsRepeatCount(0);
        mAnimatorSet.cancel();
    }

    private void setAllAnimationsRepeatCount(int repeatCount) {
        for (Animator animator : mAnimatorSet.getChildAnimations()) {
            if (animator instanceof ObjectAnimator) {
                ((ObjectAnimator) animator).setRepeatCount(repeatCount);
            }
        }
    }

    public void hide() {

        createDotHideAnimator(lineThree, 2).start();

        ObjectAnimator dotTwoMoveRightToLeft = createDotHideAnimator(lineTwo, 1);
        dotTwoMoveRightToLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });

        dotTwoMoveRightToLeft.start();
        isHide = true;
    }

    public void show() {

        setVisibility(INVISIBLE);
        ObjectAnimator dotThreeMoveRightToLeft = createDotShowAnimator(lineThree, 2);

        dotThreeMoveRightToLeft.start();

        ObjectAnimator dotTwoMoveRightToLeft = createDotShowAnimator(lineTwo, 1);
        dotTwoMoveRightToLeft.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                invalidate();
            }
        });

        dotTwoMoveRightToLeft.start();
        isHide = false;
    }

    private ObjectAnimator createDotHideAnimator(JumpingImageSpan span, float widthMultiplier) {
        return createDotHorizontalAnimator(span, 0, -textWidth * widthMultiplier);
    }

    private ObjectAnimator createDotShowAnimator(JumpingImageSpan span, int widthMultiplier) {
        return createDotHorizontalAnimator(span, -textWidth * widthMultiplier, 0);
    }

    private ObjectAnimator createDotHorizontalAnimator(JumpingImageSpan span, float from, float to) {
        ObjectAnimator dotThreeMoveRightToLeft = ObjectAnimator.ofFloat(span, "translationX", from, to);
        dotThreeMoveRightToLeft.setDuration(showSpeed);
        return dotThreeMoveRightToLeft;
    }

    public void showAndPlay() {
        show();
        start();
    }

    public void hideAndStop() {
        hide();
        stop();
    }

    public boolean isHide() {
        return isHide;
    }

    public void setHide(boolean hide) {
        isHide = hide;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setJumpHeight(int jumpHeight) {
        this.jumpHeight = jumpHeight;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public class JumpingSpan extends ReplacementSpan {

        private float translationX = 0;
        private float translationY = 0;

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fontMetricsInt) {
            return (int) paint.measureText(text, start, end);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            canvas.drawText(text, start, end, x + translationX, y + translationY, paint);
        }

        public void setTranslationX(float translationX) {
            this.translationX = translationX;
        }

        public void setTranslationY(float translationY) {
            this.translationY = translationY;
        }
    }

    public class JumpingImageSpan extends ImageSpan {
        private float translationX = 0;
        private float translationY = 0;

        public JumpingImageSpan(Context context, Bitmap b) {
            super(context, b);
        }

        public JumpingImageSpan(Context context, int resourceId) {
            super(context, resourceId);
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
            x += translationX;
            bottom += translationY;
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }

        public void setTranslationX(float translationX) {
            this.translationX = translationX;
        }

        public void setTranslationY(float translationY) {
            this.translationY = translationY;
        }
    }
}
