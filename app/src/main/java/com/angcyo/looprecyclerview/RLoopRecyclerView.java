package com.angcyo.looprecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/03/01 11:58
 * 修改人员：Robi
 * 修改时间：2017/03/01 11:58
 * 修改人员：chenqingzhen
 * 修改时间：2017/12/16 14:40
 * 修改备注：
 * Version: 1.0.0
 */
public class RLoopRecyclerView extends RecyclerView {
    private static final String TAG = "angcyo";
    public static final int SCROLL_MODE_NORMAL=0;
    public static final int SCROLL_MODE_PAGE=1;
    private int mScrollMode;//0 默认滑动  1翻页滑动

    public RLoopRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RLoopRecyclerView(Context context) {
        this(context, null);
    }

    public RLoopRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(R.styleable.RLoopRecyclerView);
        mScrollMode = a.getInt(R.styleable.RLoopRecyclerView_ScrollMode, 0);
        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView();
    }

    @Override
    public LoopAdapter getAdapter() {
        return (LoopAdapter) super.getAdapter();
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (!(adapter instanceof LoopAdapter)) {
            throw new IllegalArgumentException("adapter must  instanceof LoopAdapter!");
        }
        super.setAdapter(adapter);
        scrollToPosition(getAdapter().getItemRawCount() * 10000);//开始时的偏移量
    }

    private void initView() {
        if(mScrollMode==SCROLL_MODE_PAGE) {
            new RPagerSnapHelper().setOnPageListener(new RPagerSnapHelper.OnPageListener() {
                @Override
                public void onPageSelector(int position) {
                    Log.e(TAG, "onPageSelector: " + position % getAdapter().getItemRawCount());
                }
            }).attachToRecyclerView(this);
        }
    }

    public static abstract class LoopAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

        List<E> datas = new ArrayList<>();

        public void setDatas(List<E> datas) {
            this.datas = datas;
            notifyDataSetChanged();
        }

        /**
         * 真实数据的大小
         */
        public int getItemRawCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        final public int getItemViewType(int position) {
            return getLoopItemViewType(position % getItemRawCount());
        }

        protected int getLoopItemViewType(int position) {
            return 0;
        }

        @Override
        final public void onBindViewHolder(VH holder, int position) {
            onBindLoopViewHolder(holder, position % getItemRawCount());
        }

        public abstract void onBindLoopViewHolder(VH holder, int position);

        @Override
        final public int getItemCount() {
            int rawCount = getItemRawCount();
            if (rawCount > 0) {
                return Integer.MAX_VALUE;
            }
            return 0;
        }
    }
}
