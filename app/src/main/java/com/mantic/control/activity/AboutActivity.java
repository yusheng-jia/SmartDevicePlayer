package com.mantic.control.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mantic.control.BuildConfig;
import com.mantic.control.R;
import com.mantic.control.utils.StatusBarHelper;

import me.yokeyword.swipebackfragment.SwipeBackActivity;

/**
 * Created by wujiangxia on 2017/5/2.
 */
public class AboutActivity extends SwipeBackActivity {
    private TextView tvTitle;
    private LinearLayout llback;
    private TextView tv_about_version;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        tv_about_version = (TextView) findViewById(R.id.tv_about_version);
        tv_about_version.setText("Version: " + BuildConfig.VERSION_NAME);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            this.finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
