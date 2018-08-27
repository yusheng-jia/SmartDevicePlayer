/*
 * Copyright © Yolanda. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mantic.control.itemtouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.mantic.control.R;

/**
 * Created in 2016/5/7 21:01.
 */
public class DefaultItemTouchHelpCallback extends ItemTouchHelper.Callback {

    /**
     * Item操作的回调
     */
    private OnItemTouchCallbackListener onItemTouchCallbackListener;

    /**
     * 是否可以拖拽
     */
    private boolean isCanDrag = false;
    /**
     * 是否可以被滑动
     */
    private boolean isCanSwipe = false;

    /**
     * 拖拽时候的背景
     */
    private int dragingBackGroundRes = R.drawable.channel_manager_item_shadow;

    /**
     * 拖拽完成的北京
     */
    private int dragedBackGroundColor = Color.WHITE;




    public DefaultItemTouchHelpCallback(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置Item操作的回调，去更新UI和数据源
     *
     * @param onItemTouchCallbackListener
     */
    public void setOnItemTouchCallbackListener(OnItemTouchCallbackListener onItemTouchCallbackListener) {
        this.onItemTouchCallbackListener = onItemTouchCallbackListener;
    }

    /**
     * 设置是否可以被拖拽
     *
     * @param canDrag 是true，否false
     */
    public void setDragEnable(boolean canDrag) {
        isCanDrag = canDrag;
    }

    /**
     * 设置是否可以被滑动
     *
     * @param canSwipe 是true，否false
     */
    public void setSwipeEnable(boolean canSwipe) {
        isCanSwipe = canSwipe;
    }

    /**
     * 当Item被长按的时候是否可以被拖拽
     *
     * @return
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return isCanDrag;
    }

    /**
     * Item是否可以被滑动(H：左右滑动，V：上下滑动)
     *
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return isCanSwipe;
    }


    public void setDragingBackGroundRes(int dragingBackGroundRes) {
        this.dragingBackGroundRes = dragingBackGroundRes;
    }

    public void setDragedBackGroundColor(int dragedBackGroundColor) {
        this.dragedBackGroundColor = dragedBackGroundColor;
    }

    /**
     * 当用户拖拽或者滑动Item的时候需要我们告诉系统滑动或者拖拽的方向
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {// GridLayoutManager
            // flag如果值是0，相当于这个功能被关闭
            int dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlag = 0;
            // create make
            return makeMovementFlags(dragFlag, swipeFlag);
        } else if (layoutManager instanceof LinearLayoutManager) {// linearLayoutManager
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            int orientation = linearLayoutManager.getOrientation();

            int dragFlag = 0;
            int swipeFlag = 0;

            // 为了方便理解，相当于分为横着的ListView和竖着的ListView
            if (orientation == LinearLayoutManager.HORIZONTAL) {// 如果是横向的布局
                swipeFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                dragFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            } else if (orientation == LinearLayoutManager.VERTICAL) {// 如果是竖向的布局，相当于ListView
                dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlag = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            }
//            return makeMovementFlags(dragFlag, swipeFlag);
            return makeMovementFlags(dragFlag, 0);
        }
        return 0;
    }

    /**
     * 当Item被拖拽的时候被回调
     *
     * @param recyclerView     recyclerView
     * @param srcViewHolder    拖拽的ViewHolder
     * @param targetViewHolder 目的地的viewHolder
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder srcViewHolder, RecyclerView.ViewHolder targetViewHolder) {
        if (onItemTouchCallbackListener != null) {
            return onItemTouchCallbackListener.onMove(srcViewHolder.getAdapterPosition(), targetViewHolder.getAdapterPosition());
        }
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onSwiped(viewHolder.getAdapterPosition());
        }
    }

    //当item视图变化时调用
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        //根据item滑动偏移的值修改item透明度。screenwidth是我提前获得的屏幕宽度
        viewHolder.itemView.setAlpha(1-Math.abs(dX)/recyclerView.getWidth());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if(actionState==ItemTouchHelper.ACTION_STATE_DRAG){
            Context context = viewHolder.itemView.getContext();
//            viewHolder.itemView.setBackground(context.getResources().getDrawable(R.drawable.channel_manager_item_shadow));//拖拽时设置背景色为灰色
            viewHolder.itemView.setBackgroundResource(dragingBackGroundRes);
        }
    }

    //当item拖拽完成时调用
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        viewHolder.itemView.setBackgroundColor(dragedBackGroundColor);//拖拽停止时设置背景色为白色
        if (onItemTouchCallbackListener != null) {
            onItemTouchCallbackListener.onMoveEnd();
        }
    }

    public interface OnItemTouchCallbackListener {
        /**
         * 当某个Item被滑动删除的时候
         *
         * @param adapterPosition item的position
         */
        void onSwiped(int adapterPosition);

        /**
         * 当两个Item位置互换的时候被回调
         *
         * @param srcPosition    拖拽的item的position
         * @param targetPosition 目的地的Item的position
         * @return 开发者处理了操作应该返回true，开发者没有处理就返回false
         */
        boolean onMove(int srcPosition, int targetPosition);


        void onMoveEnd();
    }
}
