package com.angcyo.looprecyclerview.new_opt;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：让第一个Pager Item 的left 和 top 为0 (处理当item的宽度不是全屏的时候, 并添加pager 的listener)
 * 创建人员：Robi
 * 创建时间：2017/02/27 14:10
 * 修改人员：Robi
 * 修改时间：2017/02/27 14:10
 * 修改备注：
 * Version: 1.0.0
 */
public class RPagerSnapHelper extends PagerSnapHelper {

    protected RecyclerView mRecyclerView;
    OnPageListener mOnPageListener;
    int mCurrentPosition = -1;
    /**
     * 默认是横向Pager
     */
    int mOrientation = LinearLayoutManager.HORIZONTAL;
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                //开始滚动
            } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                //结束滚动
            } else if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                //滑行中
            }
        }
    };

    public RPagerSnapHelper() {
    }

    public RPagerSnapHelper(int mCurrentPosition) {
        this.mCurrentPosition = mCurrentPosition;
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public RPagerSnapHelper setCurrentPosition(int currentPosition) {
        mCurrentPosition = currentPosition;
        return this;
    }

    public OnPageListener getOnPageListener() {
        return mOnPageListener;
    }

    /**
     * 页面选择回调监听
     */
    public RPagerSnapHelper setOnPageListener(OnPageListener onPageListener) {
        mOnPageListener = onPageListener;
        return this;
    }

    @Override
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView) throws IllegalStateException {
        super.attachToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;

        if (recyclerView != null) {
            recyclerView.removeOnScrollListener(mScrollListener);
            recyclerView.addOnScrollListener(mScrollListener);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                mOrientation = ((LinearLayoutManager) layoutManager).getOrientation();
            }
        }
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) targetView.getLayoutParams();
        int position = params.getViewAdapterPosition();
        int left = targetView.getLeft();
        int right = targetView.getRight();
        int top = targetView.getTop();
        int bottom = targetView.getBottom();
        ViewGroup viewGroup = (ViewGroup) targetView.getParent();

        int[] out = new int[]{0, 0};
        boolean isLastItem;
        if (mOrientation == LinearLayoutManager.HORIZONTAL) {
            isLastItem = position == layoutManager.getItemCount() - 1/*最后一个*/ && right == viewGroup.getMeasuredWidth();
            out[0] = left;
            out[1] = 0;
        } else {
            isLastItem = position == layoutManager.getItemCount() - 1/*最后一个*/ && bottom == viewGroup.getMeasuredHeight();
            out[0] = 0;
            out[1] = top;
        }

        if (mOnPageListener != null && mCurrentPosition != position) {
            int currentPosition = mCurrentPosition;
            boolean listener = false;
            if (mOrientation == LinearLayoutManager.HORIZONTAL && (out[0] == 0 || isLastItem)) {
                listener = true;
            } else if (mOrientation == LinearLayoutManager.VERTICAL && (out[1] == 0 || isLastItem)) {
                listener = true;
            }

            if (listener) {
                mCurrentPosition = position;
                mOnPageListener.onPageSelector(mCurrentPosition);
                mOnPageListener.onPageSelector(currentPosition, mCurrentPosition);
            }
        }
        return out;
    }

    public RPagerSnapHelper setOrientation(int orientation) {
        mOrientation = orientation;
        return this;
    }

    public static abstract class OnPageListener {

        @Deprecated
        public void onPageSelector(int position) {

        }

        public void onPageSelector(int fromPosition, int toPosition) {

        }
    }
}
