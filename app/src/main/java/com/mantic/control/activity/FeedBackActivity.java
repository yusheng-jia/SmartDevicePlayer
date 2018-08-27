package com.mantic.control.activity;


import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.mantic.control.R;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

import me.yokeyword.swipebackfragment.SwipeBackActivity;


public class FeedBackActivity extends SwipeBackActivity implements OnClickListener, TitleBar.OnButtonClickListener {
    private static final String TAG = "FeedBackActivity";

    private static final String FEEDBACK_UPLOAD_URL = "http://heartbeat.hll.com/feedback";

    private TextView tvTitle, toolbar_next;
    private LinearLayout llback;
    private EditText mFeedbackContent;

    private TitleBar tb_feed_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(FeedBackActivity.this, Color.parseColor("#f9f9fa"), 0);
            StatusBarHelper.statusBarLightMode(this);
        }
        initView();
    }


    private void initView() {

        tb_feed_back = (TitleBar) findViewById(R.id.tb_feed_back);
        tb_feed_back.setOnButtonClickListener(this);
        tb_feed_back.getRightTextView().setEnabled(false);
        tb_feed_back.getRightTextView().setTextColor(getResources().getColor(R.color.tv_channel_add_save_unable_color));

        mFeedbackContent = (EditText) findViewById(R.id.edit_content);
        mFeedbackContent.addTextChangedListener(new EditChangedListener());
        toolbar_next = (TextView) findViewById(R.id.toolbar_next);
/*        llback = (LinearLayout) findViewById(R.id.toolbar_back);
        tvTitle = (TextView) findViewById(R.id.toolbar_title);


        tvTitle.setText(R.string.menu_suggestions);
        toolbar_next.setText(R.string.commit);*/

//        showTitleAndNext(R.string.menu_suggestions,R.string.commit);
      //  toolbar_next.setVisibility(View.VISIBLE);
    //    llback.setVisibility(View.VISIBLE);
//        toolbar_next.setOnClickListener(this);
//        llback.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

/*            case R.id.toolbar_back:
                finish();
                break;*/
            case R.id.toolbar_next:

                break;
        }
    }

    @Override
    public void onLeftClick() {
        hideSoftKey();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
    }

    @Override
    public void onRightClick() {

    }

    @Override
    protected void onDestroy() {
        hideSoftKey();
        super.onDestroy();
    }


    private void hideSoftKey() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


    class EditChangedListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                tb_feed_back.getRightTextView().setEnabled(false);
                tb_feed_back.getRightTextView().setTextColor(getResources().getColor(R.color.tv_channel_add_save_unable_color));
            } else {
                tb_feed_back.getRightTextView().setEnabled(true);
                Resources resource = getResources();
                ColorStateList csl = resource.getColorStateList(R.color.titlebar_text_background);
                tb_feed_back.getRightTextView().setTextColor(csl);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

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
