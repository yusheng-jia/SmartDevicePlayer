package com.mantic.control.fragment;

import android.support.v4.app.Fragment;

public interface ClockCallbacks {
    public void pushFragment(Fragment fragment, String tag);
    public void popFragment(String tag);
}
