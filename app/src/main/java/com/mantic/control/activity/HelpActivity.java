package com.mantic.control.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.mantic.control.R;
import com.mantic.control.widget.TitleBar;

import me.yokeyword.swipebackfragment.SwipeBackActivity;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/8/3.
 * desc:帮助说明
 */

public class HelpActivity extends SwipeBackActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TitleBar titleBar = (TitleBar) findViewById(R.id.help_titlebar);
        titleBar.setOnButtonClickListener(new TitleBar.OnButtonClickListener() {
            @Override
            public void onLeftClick() {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }

            @Override
            public void onRightClick() {

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
