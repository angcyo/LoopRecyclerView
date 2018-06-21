package com.angcyo.looprecyclerview.new_opt;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


/**
 * 使用LoopLayoutManager实现的高级无限滚动,
 * Created by angcyo on 2018-03-07.
 */

public class RExLoopRecyclerView extends RecyclerView {

    Runnable autoScrollRunnable;
    int curScrollPosition;
    boolean enableScroll = true;
    long autoScrollTimeInterval = 1600;
    //无限循环
    boolean mInfiniteLoop = true;
    private LoopLayoutManager mLoopLayoutManager;
    private RPagerSnapHelper.OnPageListener mOnPageListener;

    public RExLoopRecyclerView(Context context) {
        this(context, null);
    }

    public RExLoopRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RExLoopRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    protected void initView(Context context) {

        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                curScrollPosition = getCurrentPosition();

                scrollToNext();

                if (enableScroll) {
                    postDelayed(autoScrollRunnable, autoScrollTimeInterval);
                }
            }
        };

        mLoopLayoutManager = new LoopLayoutManager(getContext(), getLoopOrientation(), false);
        setInfinite(mInfiniteLoop);
        setLayoutManager(mLoopLayoutManager);

        new RExLoopRecyclerView.LoopSnapHelper().setOnPageListener(new RPagerSnapHelper.OnPageListener() {
            @Override
            public void onPageSelector(int fromPosition, int toPosition) {
                super.onPageSelector(fromPosition, toPosition);
                //L.e("onPageSelector() -> " + fromPosition + " to " + toPosition);
                if (mOnPageListener != null) {
                    mOnPageListener.onPageSelector(fromPosition, toPosition);
                }
            }
        }).attachToRecyclerView(this);
    }

    private int getLoopOrientation() {
        String tag = (String) this.getTag();
        if (!TextUtils.isEmpty(tag) && "V".equalsIgnoreCase(tag)) {
            return ViewPagerLayoutManager.VERTICAL;
        }
        return ViewPagerLayoutManager.HORIZONTAL;
    }

    @Override
    public void setTag(Object tag) {
        super.setTag(tag);
        setOrientation(getLoopOrientation());
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (enableScroll) {
            postDelayed(autoScrollRunnable, autoScrollTimeInterval);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mLoopLayoutManager != null) {
            mLoopLayoutManager.setItemWidthHeight(w, h);
        }
    }

    /**
     * 打开无限循环
     */
    public void setInfinite(boolean enable) {
        mInfiniteLoop = enable;
        if (mLoopLayoutManager != null) {
            mLoopLayoutManager.setInfinite(enable);
        }
    }

    /**
     * @see ViewPagerLayoutManager#setOrientation(int)
     */
    public void setOrientation(int orientation) {
        if (mLoopLayoutManager != null) {
            mLoopLayoutManager.setOrientation(orientation);
        }
    }

    /**
     * 滚动到下一个
     */
    public void scrollToNext() {
        scrollTo(true);
    }

    /**
     * 滚动到上一个
     */
    public void scrollToPrev() {
        scrollTo(false);
    }

    public void scrollTo(boolean forwardDirection) {
        if (getLayoutManager() instanceof LoopLayoutManager && getAdapter() != null) {
            final int offsetPosition;
            if (forwardDirection) {
                offsetPosition = 1;
            } else {
                offsetPosition = -1;
            }

            final int currentPosition = getCurrentPosition();

            int position = ((LoopLayoutManager) getLayoutManager()).getReverseLayout() ?
                    currentPosition - offsetPosition : currentPosition + offsetPosition;
            smoothScrollToPosition(position);
        }
    }

    public int getCurrentPosition() {
        if (getLayoutManager() instanceof LoopLayoutManager) {
            return ((LoopLayoutManager) getLayoutManager()).getCurrentPosition();
        } else {
            return RecyclerView.NO_POSITION;
        }
    }

    public void setOnPageListener(RPagerSnapHelper.OnPageListener onPageListener) {
        mOnPageListener = onPageListener;
    }

    public static class LoopLayoutManager extends ViewPagerLayoutManager {

        private float itemWidth = -1;
        private float itemHeight = -1;

        public LoopLayoutManager(Context context) {
            this(context, ViewPagerLayoutManager.HORIZONTAL, false);
        }

        public LoopLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
            setEnableBringCenterToFront(true);
        }

        public void setItemWidthHeight(float itemWidth, float itemHeight) {
            this.itemWidth = itemWidth;
            this.itemHeight = itemHeight;
            requestLayout();
        }

        @Override
        public void setInfinite(boolean enable) {
            if (!enable) {
                int positionOffset = getCurrentPositionOffset();
                if (positionOffset > getItemCount() || positionOffset < 0) {
                    mOffset = 0;
                }
            }
            super.setInfinite(enable);
        }

        /**
         * Item 之间间隔的大小
         * 默认情况下, Item之间是相互叠加显示的, 需要通过此方法, 设置间隔才能显示出线性的效果
         */
        @Override
        protected float setInterval() {
            if (getOrientation() == ViewPagerLayoutManager.VERTICAL) {
                return itemHeight;
            }
            return itemWidth;
        }

        /**
         * 用来控制item属性, 比如各种属性动画, 在滑动的时候出发
         */
        @Override
        protected void setItemViewProperty(View itemView, float targetOffset) {
            //targetOffset 和 itemInterval 密切广西
            //targetOffset 取值范围 -itemInterval/2  0  itemInterval/2
            //L.e("setItemViewProperty() -> " + targetOffset);
        }
    }

    public static class LoopSnapHelper extends RPagerSnapHelper {

        @Override
        public boolean onFling(int velocityX, int velocityY) {
            super.onFling(velocityX, velocityY);
            //拦截fling操作
            return true;
        }

        @Nullable
        @Override
        public View findSnapView(RecyclerView.LayoutManager layoutManager) {
            return super.findSnapView(layoutManager);
        }

        @Override
        public int findTargetSnapPosition(RecyclerView.LayoutManager lm, int velocityX, int velocityY) {
            //return super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
            //重写fling操作
            if (lm instanceof LoopLayoutManager) {
            } else {
                return RecyclerView.NO_POSITION;
            }
            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
            if (adapter == null) {
                return RecyclerView.NO_POSITION;
            }

            final int minFlingVelocity = mRecyclerView.getMinFlingVelocity();

            LoopLayoutManager layoutManager = (LoopLayoutManager) lm;
            int orientation = layoutManager.getOrientation();

            final boolean forwardDirection;
            if (layoutManager.canScrollHorizontally()) {
                forwardDirection = velocityX > 0;
            } else {
                forwardDirection = velocityY > 0;
            }
            final int offsetPosition;
            if (forwardDirection) {
                offsetPosition = 1;
            } else {
                offsetPosition = -1;
            }

            final int currentPosition = layoutManager.getCurrentPosition();
            if ((orientation == ViewPagerLayoutManager.VERTICAL
                    && Math.abs(velocityY) > minFlingVelocity) || (orientation == ViewPagerLayoutManager.HORIZONTAL
                    && Math.abs(velocityX) > minFlingVelocity)) {

                int position = layoutManager.getReverseLayout() ?
                        currentPosition - offsetPosition : currentPosition + offsetPosition;
                mRecyclerView.smoothScrollToPosition(position);
            }

            //不需要默认的fling操作
            return RecyclerView.NO_POSITION;
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
    }
}
