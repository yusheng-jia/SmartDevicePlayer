package com.mantic.control.manager;

import android.app.Activity;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by lin on 2017/6/14.
 */

public class DragLayoutManager {

    private static Stack<String> activityStack;

    private static DragLayoutManager instance;

    private DragLayoutManager() {
    }

    /**
     * 单一实例
     */
    public static DragLayoutManager getAppManager() {
        if (instance == null) {
            instance = new DragLayoutManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addDragLyout(String tag) {
        if (activityStack == null) {
            activityStack = new Stack<String>();
        }
        activityStack.add(tag);
    }

    /**
     * 添加Activity到堆栈
     */
    public void removeAllDragLyout() {
        if (null != activityStack) {
         activityStack.clear();
        }
    }

    /**
     * 添加Activity到堆栈
     */
    public void removeLastDragLyout() {
        if (null != activityStack && activityStack.size() > 0) {
            activityStack.remove(activityStack.size() - 1);

        }
    }


    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public boolean getDragLayoutLockeEnable() {
        if (null == activityStack || activityStack.size() == 0) {
            return false;
        }
        return true;
    }

}
