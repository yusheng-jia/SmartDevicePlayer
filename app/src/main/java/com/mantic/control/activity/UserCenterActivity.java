package com.mantic.control.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jaeger.library.StatusBarUtil;
import com.mantic.control.R;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.StatusBarHelper;

import me.yokeyword.swipebackfragment.SwipeBackActivity;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/12/26.
 * desc:
 */

public class UserCenterActivity extends SwipeBackActivity implements View.OnClickListener {

    private TextView centerName;
    private ImageView centerIcon;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_center_activity);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////            StatusBarUtil.setColor(UserCenterActivity.this, Color.parseColor("#000000"), 0);
//            StatusBarHelper.statusBarLightMode(this);
//        }
        findViewById(R.id.change_password).setOnClickListener(this);
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            }
        });
        centerName = (TextView)findViewById(R.id.center_name);
        centerName.setText(SharePreferenceUtil.getUserName(this));
        centerIcon = (ImageView)findViewById(R.id.center_head);

    }

    @Override
    protected void onResume() {
        super.onResume();
        centerIcon.setBackgroundResource(R.drawable.ic_default_icon);
        Glide.with(this)
                .load(SharePreferenceUtil.getUserPhoto(this))
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(centerIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
                        circularBitmapDrawable.setCornerRadius(12);
                        centerIcon.setImageDrawable(circularBitmapDrawable);
                        resource.recycle();
                        resource = null;
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_password:
                Intent intent = new Intent(UserCenterActivity.this, RetrievePasswordActivity.class);
                intent.putExtra("comfrom", "UserCenterActivity");
                startActivity(intent);
                break;
        }
    }
}
