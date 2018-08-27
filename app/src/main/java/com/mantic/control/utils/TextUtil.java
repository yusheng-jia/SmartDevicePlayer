package com.mantic.control.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mantic.control.R;


/**
 * Created by linbingjie on 2017/5/24.
 */

public class TextUtil {


    /**
     *
     * @param context
     * @param isExpend 伸缩
     * @param textView
     * @param res 图片资源
     * @param maxLines 最大行数
     * @param content 内容
     */
    public static void addImage(Context context, boolean isExpend, final TextView textView, final int res,
                          final int maxLines, String content) {
        if (isExpend) {
            TextPaint paint  = textView.getPaint();
            float textWidth = paint.measureText("\t\t\t\t" + TextUtil.ToDBC(content));
            int paddingLeft = textView.getPaddingLeft();
            int paddingRight= textView.getPaddingRight();
            if (textWidth <= (textView.getWidth() - paddingLeft - paddingRight) * maxLines) {
                textView.setText("\t\t\t\t" + TextUtil.ToDBC(content));
            } else {
                insertDrawableIntoTextView(context, textView, "\t\t\t\t" + TextUtil.ToDBC(content),
                        R.drawable.expandabletextview_collapsedrawable,  1);
            }
        } else {
            TextUtil.setExpandTextView(context, textView, res, maxLines, TextUtil.ToDBC(content), 2);
        }
    }

    /**
     *
     * @param textView
     * @param content
     * @param res
     */
    private static void insertDrawableIntoTextView(Context context, final TextView textView, String content, int res,  int type) {

        String imgTag = "img";
        int start = content.length();
        int end = start + imgTag.length();
        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(content+imgTag);
        // 插入图片
        Drawable drawable = context.getResources().getDrawable(res);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan imgSpan = new ImageSpan(drawable,ImageSpan.ALIGN_BASELINE);
        ssBuilder.setSpan(imgSpan, start, end, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        final SpannableStringBuilder builder = ssBuilder;
        int lines = 0;
        if (type == 1) {
            float f = textView.getPaint().measureText(ssBuilder.toString()) / textView.getWidth();
            lines = (int) Math.ceil(Double.parseDouble(String.valueOf(f)));
            SharePreferenceUtil.setSharePreferenceData(context, "Mantic", "lastLines", String.valueOf(lines));
        } else {
            String data = SharePreferenceUtil.getSharePreferenceData(context, "Mantic", "lastLines");
            if (!TextUtils.isEmpty(data)) {
                lines = Integer.parseInt(data);
            }
        }


        switch (type) {
            case 1:
                setExpendTextViewAnimation(textView, builder, textView.getLineHeight() * 2, textView.getLineHeight() * lines, true);
                break;
            case 2:
                setExpendTextViewAnimation(textView, builder, textView.getLineHeight() * lines, textView.getLineHeight() * 2, false);
                break;
            default:
                textView.setText(builder);
                break;
        }



    }

    public static void setExpandTextView(Context context, TextView textView, int res, int maxLines, String content, int type) {
        if (TextUtils.isEmpty(content)) {
            textView.setVisibility(View.GONE);
            return;
        }

        content = "\t\t\t\t" + content;
        int availableTextWidth = 0;
        if(availableTextWidth == 0 && textView.getWidth()>0){
            TextPaint paint  = textView.getPaint();
            float textWidth = paint.measureText(content);
            int paddingLeft = textView.getPaddingLeft();
            int paddingRight= textView.getPaddingRight();

            if (textWidth <= (textView.getWidth() - paddingLeft - paddingRight) * maxLines) {
                textView.setText(content);
                return;
            }

            int bufferWidth = (int) paint.getTextSize()*4;//缓冲区长度，空出两个字符的长度来给最后的省略号及图片
            // 计算出2行文字所能显示的长度
            availableTextWidth = (textView.getWidth() - paddingLeft - paddingRight) * maxLines - bufferWidth;
            // 根据长度截取出剪裁后的文字
            String ellipsizeStr = (String) TextUtils.ellipsize(content, (TextPaint) paint, availableTextWidth, TextUtils.TruncateAt.END);
            insertDrawableIntoTextView(context, textView, ellipsizeStr, res, type);
        }
    }

    private static void setExpendTextViewAnimation(final TextView textView, final SpannableStringBuilder ssb, int begin, int end, final boolean flag) {
        final ViewGroup.LayoutParams layoutParams1 = textView.getLayoutParams();
        //TextView的平移动画
        ValueAnimator animator_textView1= ValueAnimator.ofInt(begin, end);
        animator_textView1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                layoutParams1.height=(Integer)animation.getAnimatedValue();
                textView.setLayoutParams(layoutParams1);
                if (flag) {
                    textView.setText(ssb);
                }
            }
        });
        animator_textView1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!flag) {
                    textView.setText(ssb);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        AnimatorSet mAnimatorSets1=new AnimatorSet();
        mAnimatorSets1.setDuration(500);
        mAnimatorSets1.play(animator_textView1);
        mAnimatorSets1.start();
    }

    /**
     * 半角转换为全角,要不然textview会出现换行截断问题
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);
            }
        }
        return new String(c);
    }

}
