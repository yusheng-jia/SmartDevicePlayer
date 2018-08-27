package com.mantic.control.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by root on 17-5-5.
 */
public class ManticSharedPreferences {
    private static final String MANTIC_SHARED_PREFERENCES = "mantic_shared_preferences";
    public static final String KEY_INTEREST = "key_interest";
    private static SharedPreferences INSTANCE;

    public static SharedPreferences getInstance(Context context){
        if(INSTANCE == null){
            Context applicationContext = context.getApplicationContext();
            INSTANCE = applicationContext.getSharedPreferences(MANTIC_SHARED_PREFERENCES,Context.MODE_PRIVATE);
        }
        return INSTANCE;
    }
}
