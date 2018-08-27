package com.mantic.control.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.jaeger.library.StatusBarUtil;
import com.mantic.control.R;
import com.mantic.control.fragment.ClockCallbacks;
import com.mantic.control.fragment.ClockFragment;
import com.mantic.control.utils.StatusBarHelper;
import com.mantic.control.widget.TitleBar;

public class ClockActivity extends AppCompatActivity implements ClockCallbacks{
    private ClockFragment clockFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                StatusBarUtil.setColor(this, Color.parseColor("#f9f9fa"), 0);
                StatusBarHelper.statusBarLightMode(this);
            }
        }
        initFragment();
    }
    private void initFragment(){
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(ClockFragment.TAG);
        if (null == fragment) {
            clockFragment = new ClockFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.clock_container, clockFragment, ClockFragment.TAG).commit();
        }
}

    @Override
    public void popFragment(String tag) {
        this.getSupportFragmentManager().popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void pushFragment(Fragment fragment, String tag) {
        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_down_out, R.anim.push_up_in, R.anim.push_down_out);
        transaction.add(R.id.clock_container, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }
}
