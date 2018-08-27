package com.mantic.control.utils;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

/**
 * Created by lin on 2017/7/17.
 */

public class StatusBarHelper {
    @IntDef({
            OTHER,
            MIUI,
            FLYME,
            ANDROID_M
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface SystemType {

    }

    public static final int OTHER = -1;
    public static final int MIUI = 1;
    public static final int FLYME = 2;
    public static final int ANDROID_M = 3;

    /**
     * 设置状态栏黑色字体图标，
     * 适配4.4以上版本MIUIV、Flyme和6.0以上版本其他Android
     *
     * @return 1:MIUI 2:Flyme 3:android6.0
     */
    public static int statusBarLightMode(Activity activity) {
        @SystemType int result = 0;
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (new MIUIHelper().setStatusBarLightMode(activity, true)) {
                result = MIUI;
            } else if (new FlymeHelper().setStatusBarLightMode(activity, true)) {
                result = FLYME;
            } else if (new AndroidMHelper().setStatusBarLightMode(activity, true)) {
                result = ANDROID_M;
            }
        }*/
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            if(isFlyme()){
                new FlymeHelper().setStatusBarLightMode(activity,true);
                result=FLYME;
            }else if(isMiui()){
                new MIUIHelper().setStatusBarLightMode(activity,true);
                result=MIUI;
            }else {
                new AndroidMHelper().setStatusBarLightMode(activity,true);
                result=ANDROID_M;
            }
        }
        return result;
    }

    /**
     * 已知系统类型时，设置状态栏黑色字体图标。
     * 适配4.4以上版本MIUI6、Flyme和6.0以上版本其他Android
     *
     * @param type 1:MIUI 2:Flyme 3:android6.0
     */
    public static void statusBarLightMode(Activity activity, @SystemType int type) {
        statusBarMode(activity, type, true);

    }

    /**
     * 清除MIUI或flyme或6.0以上版本状态栏黑色字体
     */
    public static void statusBarDarkMode(Activity activity, @SystemType int type) {
        statusBarMode(activity, type, false);
    }

    private static void statusBarMode(Activity activity, @SystemType int type, boolean isFontColorDark) {
        if (type == MIUI) {
            new MIUIHelper().setStatusBarLightMode(activity, isFontColorDark);
        } else if (type == FLYME) {
            new FlymeHelper().setStatusBarLightMode(activity, isFontColorDark);
        } else if (type == ANDROID_M) {
            new AndroidMHelper().setStatusBarLightMode(activity, isFontColorDark);
        }
    }
    private static boolean isMiui(){
        return Build.MANUFACTURER=="Xiaomi";
    }
    private static boolean isFlyme(){
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }

}