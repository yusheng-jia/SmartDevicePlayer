package com.mantic.control.fragment;

import android.support.v4.app.Fragment;

/**
 * Created by root on 17-4-14.
 */
public interface FragmentEntrust {
    public void pushFragment(Fragment fragment,String tag);
    public void popFragment(String tag);
    public void popAllFragment();
}
