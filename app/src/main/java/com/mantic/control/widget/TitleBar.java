package com.mantic.control.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;

/**
 * Created by lin on 2017/6/5.
 * 自定义titlebar组件
 */

public class TitleBar extends RelativeLayout {

    private String mLeftButtonText;
    private int mLeftButtonTextColor;
    private float mLeftButtonSize;
    private Drawable mLeftButtonImage;

    private String mTitleButtonText;
    private int mTitleButtonTextColor;
    private float mTitleButtonSize;

    private String mRightButtonText;
    private int mRightButtonTextColor;
    private float mRightButtonSize;
    private Drawable mRightButtonImage;

    private TextView mLeftTextView;
    private ImageView mLeftButton;

    private TextView mRightTextView;
    private ImageView mRightButton;

    private TextView mTitleTextView;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
        initView(context);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Titlebar);
        mLeftButtonText = typedArray.getString(R.styleable.Titlebar_leftButtonText);
        mLeftButtonTextColor = typedArray.getColor(R.styleable.Titlebar_leftButtonTextColor, Color.GRAY);
        mLeftButtonSize = typedArray.getDimension(R.styleable.Titlebar_leftButtonTextSize, 13);
        mLeftButtonImage = typedArray.getDrawable(R.styleable.Titlebar_leftButtonImage);

        mTitleButtonText = typedArray.getString(R.styleable.Titlebar_titleText);
        mTitleButtonTextColor = typedArray.getColor(R.styleable.Titlebar_titleColor, Color.GRAY);
        mTitleButtonSize = typedArray.getDimension(R.styleable.Titlebar_titleSize, 17);

        mRightButtonText = typedArray.getString(R.styleable.Titlebar_rightButtonText);
        mRightButtonTextColor = typedArray.getColor(R.styleable.Titlebar_rightButtonTextColor, Color.GRAY);
        mRightButtonSize = typedArray.getDimension(R.styleable.Titlebar_rightButtonTextSize, 13);
        mRightButtonImage = typedArray.getDrawable(R.styleable.Titlebar_rightButtonImage);

        typedArray.recycle();
    }


    private void initView(Context context) {
        this.setOnClickListener(null);
        if(mLeftButtonImage == null & mLeftButtonText != null){
            // 当用户没有设置左侧按钮图片并设置了左侧的按钮文本属性时--添加左侧文本按钮
            mLeftTextView = new TextView(context);
            mLeftTextView.setText(mLeftButtonText);
            mLeftTextView.setTextColor(mLeftButtonTextColor);
            mLeftTextView.setTextSize(mLeftButtonSize);
            mLeftTextView.getPaint().setTextSize(mLeftButtonSize);
            mLeftTextView.setGravity(Gravity.CENTER_VERTICAL);

            mLeftTextView.setPadding(15, 0, 15, 0);
            mLeftTextView.setClickable(true);
            Resources resource =  context.getResources();
            ColorStateList csl = resource.getColorStateList(R.color.titlebar_text_background);
            mLeftTextView.setTextColor(csl);
            RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            leftParams.addRule(RelativeLayout.CENTER_VERTICAL);
            addView(mLeftTextView, leftParams);
        }else if(mLeftButtonImage != null){
            // 添加左侧图片按钮
            RelativeLayout.LayoutParams leftParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            leftParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mLeftButton = new ImageView(context);
            mLeftButton.setImageDrawable(mLeftButtonImage);
            mLeftButton.setPadding(0, 0, 30, 0);
            addView(mLeftButton, leftParams);
        }

        // 添加中间标题
        mTitleTextView = new TextView(context);
        mTitleTextView.setText(mTitleButtonText);
        mTitleTextView.setTextColor(mTitleButtonTextColor);
        mTitleTextView.setTextSize(mTitleButtonSize);
        mTitleTextView.getPaint().setFakeBoldText(true);
        mTitleTextView.getPaint().setTextSize(mTitleButtonSize);
        mTitleTextView.setOnClickListener(null);
        mTitleTextView.setGravity(Gravity.CENTER_VERTICAL);
        RelativeLayout.LayoutParams titleTextViewParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        titleTextViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mTitleTextView,titleTextViewParams);


        if(mRightButtonImage == null & mRightButtonText != null){
            // 当用户没有设置右侧按钮图片并设置了左侧的按钮文本属性时--添加右侧文本按钮
            mRightTextView = new TextView(context);
            mRightTextView.setText(mRightButtonText);
            mRightTextView.setTextColor(mRightButtonTextColor);
            mRightTextView.setTextSize(mRightButtonSize);
            mRightTextView.getPaint().setTextSize(mRightButtonSize);
            mRightTextView.setGravity(Gravity.CENTER);
            mRightTextView.setPadding(30, 0, 0, 0);
            mRightTextView.setClickable(true);
            Resources resource = (Resources) context.getResources();
            ColorStateList csl = (ColorStateList) resource.getColorStateList(R.color.titlebar_text_background);
            mRightTextView.setTextColor(csl);

            RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
            addView(mRightTextView,rightParams);
        }else if(mRightButtonImage != null){
            // 添加右侧图片按钮
            RelativeLayout.LayoutParams rightParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rightParams.addRule(RelativeLayout.CENTER_VERTICAL);
            mRightButton = new ImageView(context);
            mRightButton.setImageDrawable(mRightButtonImage);
            mRightButton.setPadding(30, 0, 10, 0);
            addView(mRightButton, rightParams);
        }
    }


    public void setTitleText(String title) {
        if (null != mTitleTextView) {
            mTitleTextView.setText(title);
        }
    }

    public TextView getLeftTextView() {
        return mLeftTextView;
    }

    public TextView getRightTextView() {
        return mRightTextView;
    }

    public ImageView getLeftImageView(){
        return mLeftButton;
    }

    /**
     * 在button点击事件接口
     */
    public interface OnButtonClickListener{
        void onLeftClick();
        void onRightClick();
    }

    /**
     * 设置点击事件
     * @param onButtonClickListener
     */
    public void setOnButtonClickListener(final OnButtonClickListener onButtonClickListener) {
        if(onButtonClickListener !=null){
            if(mLeftTextView != null){
                mLeftTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClickListener.onLeftClick();
                    }
                });
            }
            if(mLeftButton != null){
                mLeftButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClickListener.onLeftClick();
                    }
                });
            }
            if(mRightTextView != null){
                mRightTextView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClickListener.onRightClick();
                    }
                });
            }
            if(mRightButton != null){
                mRightButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClickListener.onRightClick();
                    }
                });
            }

        }
    }
}
