package com.mantic.control.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mantic.control.R;


/**
 * Created by test on 2016/3/15.
 */
public class Utility {

    public static Dialog getDialog(Context context, int viewId, boolean canceledOnTouchOutside, boolean cancelable) {
        final Dialog d = new Dialog(context, R.style.customDialog);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCanceledOnTouchOutside(canceledOnTouchOutside);
        d.setCancelable(cancelable);
        d.setContentView(viewId);

        Window dialogWindow = d.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setWindowAnimations(R.style.dialogstyle);
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) context.getResources().getDisplayMetrics().widthPixels; // 宽度;
        dialogWindow.setAttributes(lp);
        return d;
    }

    public static Dialog getDialog(Context context, int viewId) {
        return getDialog(context, viewId, true, true);
    }
    public static BottomSheetDialog getBottomDialog(Context context, int viewId, boolean canceledOnTouchOutside, boolean cancelable) {
        final BottomSheetDialog d = new BottomSheetDialog(context);
//        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        d.setCanceledOnTouchOutside(canceledOnTouchOutside);
//        d.setCancelable(cancelable);
        d.setContentView(viewId);
        return d;
    }

    public static BottomSheetDialog getBottomDialog(Context context, int viewId) {
        return getBottomDialog(context, viewId, true, true);
    }
}
