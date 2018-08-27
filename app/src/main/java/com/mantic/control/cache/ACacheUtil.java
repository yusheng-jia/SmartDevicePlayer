package com.mantic.control.cache;

import android.content.Context;

/**
 * Created by lin on 2017/6/12.
 */

public class ACacheUtil {

    public static void putData(Context context, String key, String value) {
        ACache aCache = ACache.get(context);
        aCache.put(key, value);
    }

    public static String getData(Context context, String key) {
        ACache aCache = ACache.get(context);
        return aCache.getAsString(key);
    }

    public static boolean removeData(Context context, String key) {
        ACache aCache = ACache.get(context);
        return aCache.remove(key);
    }
}
