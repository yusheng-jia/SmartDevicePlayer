package com.mantic.control.activity;

/**
 * Created by wujiangxia on 2017/5/2.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.mantic.control.R;


public class BaseActivity extends AppCompatActivity {

    private FrameLayout contentView;
    public TextView tvTitle, toolbar_next;
    private TextView close;
    private ImageView llback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        llback = (ImageView) findViewById(R.id.toolbar_back);
        tvTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbar_next = (TextView) findViewById(R.id.toolbar_next);
        close = (TextView) findViewById(R.id.toolbar_close);
        llback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initView();
    }


    public void showCloseView(){
        close.setVisibility(View.VISIBLE);
        llback.setVisibility(View.GONE);
    }

    public void setCloseText(String text){
        close.setText(text);
    }
    private void initView() {

        hideTitleBar();
        contentView = (FrameLayout) findViewById(R.id.base_content);
    }

    @Override
    public void setContentView(int layoutResId) {
        LayoutInflater.from(this).inflate(layoutResId, contentView);
    }

    @Override
    public void setContentView(View view) {
        contentView.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        contentView.addView(view, params);
    }

    public void hideTitleBar() {
        tvTitle.setVisibility(View.INVISIBLE);
        toolbar_next.setVisibility(View.INVISIBLE);
    }

    public void showTitleAndNext(String title,String next) {
        tvTitle.setVisibility(View.VISIBLE);
        toolbar_next.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
        toolbar_next.setText(next);
    }

    public void showTitleAndNext(int title,int next) {
        tvTitle.setVisibility(View.VISIBLE);
        toolbar_next.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(title));
        toolbar_next.setText(getString(next));
    }

    public void showTitle(String title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(title);
    }

    public void showTitle(int title) {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText(getString(title));
    }

    public void showNext(String next){
        toolbar_next.setVisibility(View.VISIBLE);
        toolbar_next.setText(next);
    }

    public void hideBack(){
        llback.setVisibility(View.GONE);
    }

    public void hideNext(){
        toolbar_next.setVisibility(View.GONE);
    }

    public TextView getNext(){
        return toolbar_next;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
