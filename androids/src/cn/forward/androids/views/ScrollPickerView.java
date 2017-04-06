package cn.forward.androids.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * 滚动选择器,带惯性滑动
 *
 * @author huangziwei
 * @date 2016.1.11
 */
public abstract class ScrollPickerView<T> extends View {

    private int mVisibleItemCount = 3; // 可见的item数量

    private boolean mIsInertiaScroll = true; // 快速滑动时是否惯性滚动一段距离，默认开启
    private boolean mIsCirculation = true; // 是否循环滚动，默认开启

    /*
      不允许父组件拦截触摸事件，设置为true为不允许拦截，此时该设置才生效
      当嵌入到ScrollView等滚动组件中，为了使该自定义滚动选择器可以正常工作，请设置为true
     */
    private boolean mDisallowInterceptTouch = false;

    private int mSelected; // 当前选中的item下标
    private List<T> mData;
    private int mItemHeight = 0; // 每个条目的高度=mMeasureHeight／mVisibleItemCount
    private int mCenterPosition = -1; // 中间item的位置，mCenterPosition＜＝mVisibleItemCount，默认为 mVisibleItemCount / 2
    private int mCenterY; // 中间item的起始坐标y = mCenterPosition*mItemHeight
    private float mLastMoveY; // 触摸的坐标y

    private float mMoveLength = 0; // item移动长度，负数表示向上移动，正数表示向下移动

    private GestureDetector mGestureDetector;
    private OnSelectedListener mListener;

    private Scroller mScroller;
    private boolean mIsFling; // 是否正在惯性滑动
    private boolean mIsMovingCenter; // 是否正在滑向中间
    // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次触屏滑动的坐标
    private int mLastScrollY = 0; // Scroller的坐标y

    private boolean mDisallowTouch = false; // 不允许触摸

    private Paint mPaint; //
    private int mCenterItemBackground = Color.TRANSPARENT; // 中间选中item的背景色

    private long mTouchDownTime;

    private boolean mCanTap = true; // 单击切换选项或触发点击监听器

    public ScrollPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScrollPickerView(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(getContext(),
                new FlingOnGestureListener());
        mScroller = new Scroller(getContext());
        mAutoScrollAnimator = ValueAnimator.ofInt(0, 0);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mData == null || mData.size() <= 0) {
            return;
        }
        // 中间item
        mPaint.setColor(mCenterItemBackground);
        float y = mCenterY;
        canvas.drawRect(0, y, getWidth(), y + mItemHeight, mPaint);
        drawItem(canvas, mData, mSelected, 0, mMoveLength, mCenterY + mMoveLength);

        int length = Math.max(mCenterPosition, mVisibleItemCount - mCenterPosition);
        int positon;
        // 上下两边
        for (int i = 1; i <= length && i <= mData.size(); i++) {

            if (i <= mCenterPosition + 1) {  // 上面的items,相对位置为 -i
                positon = mSelected - i < 0 ? mData.size() + mSelected - i
                        : mSelected - i;
                // 传入位置信息，绘制item
                if (mIsCirculation) {
                    drawItem(canvas, mData, positon, -i, mMoveLength, mCenterY + mMoveLength - i * mItemHeight);
                } else if (mSelected - i >= 0) { // 非循环滚动
                    drawItem(canvas, mData, positon, -i, mMoveLength, mCenterY + mMoveLength - i * mItemHeight);
                }
            }
            if (i <= mVisibleItemCount - mCenterPosition) {  // 下面的items,相对位置为 i
                positon = mSelected + i >= mData.size() ? mSelected + i
                        - mData.size() : mSelected + i;
                // 传入位置信息，绘制item
                if (mIsCirculation) {
                    drawItem(canvas, mData, positon, i, mMoveLength, mCenterY + mMoveLength + i * mItemHeight);
                } else if (mSelected + i < mData.size()) { // 非循环滚动
                    drawItem(canvas, mData, positon, i, mMoveLength, mCenterY + mMoveLength + i * mItemHeight);
                }
            }
        }
    }

    /**
     * 绘制item
     *
     * @param canvas
     * @param data       　数据集
     * @param position   在data数据集中的位置
     * @param relative   相对中间item的位置,relative=position-getSelected()
     * @param moveLength 中间item滚动的距离，moveLength<0则表示向上滚动的距离，moveLength＞0则表示向上滚动
     * @param top        当前绘制item的顶部坐标
     */
    public abstract void drawItem(Canvas canvas, List<T> data, int position, int relative, float moveLength, float top);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        reset();
    }

    private void reset() {
        mItemHeight = getMeasuredHeight() / mVisibleItemCount;
        if (mCenterPosition < 0) {
            mCenterPosition = mVisibleItemCount / 2;
        }
        mCenterY = mCenterPosition * mItemHeight;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mDisallowTouch) { // 不允许触摸
            return true;
        }

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        switch (event.getActionMasked()) {
            // ACTION_DOWN交给mGestureDetector处理
           /* case MotionEvent.ACTION_DOWN:

                break;*/
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getY() - mLastMoveY) < 0.1f) {
                    return true;
                }
                mMoveLength += event.getY() - mLastMoveY;
                mLastMoveY = event.getY();
                checkCirculation();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mLastMoveY = event.getY();
                moveToCenter();
                break;
        }
        return true;
    }


    /**
     * @param currY
     * @param endY
     */
    private void computeScroll(int currY, int endY) {
        if (currY != endY) { // 正在滚动
            // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次滑动的坐标
            mMoveLength = mMoveLength + currY - mLastScrollY;
            mLastScrollY = currY;
            checkCirculation();
            invalidate();
        } else { // 滚动完毕
            mIsMovingCenter = false;
            mLastScrollY = 0;

            // 直接居中，不通过动画
            if (mMoveLength > 0) { //// 向下滑动
                if (mMoveLength < mItemHeight / 2) {
                    mMoveLength = 0;
                } else {
                    mMoveLength = mItemHeight;
                }
            } else {
                if (-mMoveLength < mItemHeight / 2) {
                    mMoveLength = 0;
                } else {
                    mMoveLength = -mItemHeight;
                }
            }
            checkCirculation();
//            scroll(mMoveLength, 0);
            mMoveLength = 0;
            mLastScrollY = 0;
            notifySelected();
            invalidate();
//            moveToCenter();
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) { // 正在滚动
            // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次滑动的坐标
            mMoveLength = mMoveLength + mScroller.getCurrY() - mLastScrollY;
            mLastScrollY = mScroller.getCurrY();
            checkCirculation(); //　检测当前选中的item
            invalidate();
        } else { // 滚动完毕
            if (mIsFling) {
                mIsFling = false;
                moveToCenter(); // 滚动到中间位置
            } else if (mIsMovingCenter) { // 选择完成，回调给监听器
                mMoveLength = 0;
                mIsMovingCenter = false;
                mLastScrollY = 0;
                notifySelected();
            }
        }
    }

    public void cancelScroll() {
        mLastScrollY = 0;
        mIsFling = mIsMovingCenter = false;
        mScroller.abortAnimation();
        stopAutoScroll();
    }

    // 检测当前选择的item位置
    private void checkCirculation() {
        if (mMoveLength >= mItemHeight) { // 向下滑动
            // 该次滚动距离中越过的item数量
            int span = (int) (mMoveLength / mItemHeight);
            mSelected -= span;
            if (mSelected < 0) {  // 滚动顶部，判断是否循环滚动
                if (mIsCirculation) {
                    do {
                        mSelected = mData.size() + mSelected;
                    } while (mSelected < 0); // 当越过的item数量超过一圈时
                    mMoveLength = (mMoveLength - mItemHeight) % mItemHeight;
                } else { // 非循环滚动
                    mSelected = 0;
                    mMoveLength = mItemHeight;
                    if (mIsFling) { // 停止惯性滑动，根据computeScroll()中的逻辑，下一步将调用moveToCenter()
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) { //  移回中间位置
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = (mMoveLength - mItemHeight) % mItemHeight;
            }

        } else if (mMoveLength <= -mItemHeight) { // 向上滑动
            // 该次滚动距离中越过的item数量
            int span = (int) (-mMoveLength / mItemHeight);
            mSelected += span;
            if (mSelected >= mData.size()) { // 滚动末尾，判断是否循环滚动
                if (mIsCirculation) {
                    do {
                        mSelected = mSelected - mData.size();
                    } while (mSelected >= mData.size()); // 当越过的item数量超过一圈时
                    mMoveLength = (mMoveLength + mItemHeight) % mItemHeight;
                } else { // 非循环滚动
                    mSelected = mData.size() - 1;
                    mMoveLength = -mItemHeight;
                    if (mIsFling) { // 停止惯性滑动，根据computeScroll()中的逻辑，下一步将调用moveToCenter()
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) { //  移回中间位置
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = (mMoveLength + mItemHeight) % mItemHeight;
            }
        }
    }

    // 移动到中间位置
    private void moveToCenter() {

        if (!mScroller.isFinished() || mIsFling) {
            return;
        }
        cancelScroll();

        // 向下滑动
        if (mMoveLength > 0) {
            if (mMoveLength < mItemHeight / 2) {
                scroll(mMoveLength, 0);
            } else {
                scroll(mMoveLength, mItemHeight);
            }
        } else {
            if (-mMoveLength < mItemHeight / 2) {
                scroll(mMoveLength, 0);
            } else {
                scroll(mMoveLength, -mItemHeight);
            }
        }
    }

    // 平滑滚动
    private void scroll(float from, int to) {

        mLastScrollY = (int) from;
        mIsMovingCenter = true;
        mScroller.startScroll(0, (int) from, 0, 0);
        mScroller.setFinalY(to);
        invalidate();
    }

    // 惯性滑动，
    private void fling(float from, float vY) {
        mLastScrollY = (int) from;
        mIsFling = true;
        // 最多可以惯性滑动10个item
        mScroller.fling(0, (int) from, 0, (int) vY, 0, 0, -10 * mItemHeight,
                10 * mItemHeight);
        invalidate();
    }

    private void notifySelected() {
        if (mListener != null) {
            // 告诉监听器选择完毕
            post(new Runnable() {
                @Override
                public void run() {
                    mListener.onSelected(ScrollPickerView.this, mSelected);
                }
            });
        }
    }

    private boolean mIsAutoScrolling = false;
    private ValueAnimator mAutoScrollAnimator;
    private final static SlotInterpolator sAutoScrollInterpolator = new SlotInterpolator();

    /**
     * 自动滚动(必须设置为可循环滚动)
     *
     * @param position
     * @param duration
     * @param speed    每毫秒移动的像素点
     */
    public void autoScrollFast(final int position, long duration, float speed, final Interpolator interpolator) {
        if (mIsAutoScrolling || !mIsCirculation) {
            return;
        }
        cancelScroll();
        mIsAutoScrolling = true;


        int length = (int) (speed * duration);
        int circle = (int) (length * 1f / (mData.size() * mItemHeight) + 0.5f); // 圈数
        circle = circle <= 0 ? 1 : circle;

        int aPlan = circle * (mData.size()) * mItemHeight + (mSelected - position) * mItemHeight;
        int bPlan = aPlan + (mData.size()) * mItemHeight; // 多一圈
        // 让其尽量接近length
        final int endY = Math.abs(length - aPlan) < Math.abs(length - bPlan) ? aPlan : bPlan;

        mAutoScrollAnimator.cancel();
        mAutoScrollAnimator.setIntValues(0, endY);
        mAutoScrollAnimator.setInterpolator(interpolator);
        mAutoScrollAnimator.setDuration(duration);
        mAutoScrollAnimator.removeAllUpdateListeners();
        mAutoScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                computeScroll((int) animation.getAnimatedValue(), endY);
                float rate = 0;
                if (Build.VERSION.SDK_INT >= 12) {
                    rate = animation.getAnimatedFraction();
                } else {
                    rate = animation.getCurrentPlayTime() * 1f / animation.getDuration();
                }
                if (rate >= 1) {
                    mIsAutoScrolling = false;
                }
            }
        });
        mAutoScrollAnimator.start();
    }

    /**
     * 自动滚动，默认速度为 1.5 pixel/ms
     *
     * @see ScrollPickerView#autoScrollFast(int, long, float, Interpolator)
     */
    public void autoScrollFast(final int position, long duration) {
        float speed = dip2px(0.40f);
        autoScrollFast(position, duration, speed, sAutoScrollInterpolator);
    }

    /**
     * 自动滚动
     *
     * @see ScrollPickerView#autoScrollFast(int, long, float, Interpolator)
     */
    public void autoScrollFast(final int position, long duration, float speed) {
        autoScrollFast(position, duration, speed, sAutoScrollInterpolator);
    }

    /**
     * 滚动到指定位置
     *
     * @param toPosition   　需要滚动到的位置
     * @param duration     　滚动时间
     * @param interpolator
     */
    public void autoScrollToPosition(int toPosition, long duration, final Interpolator interpolator) {
        toPosition = toPosition % mData.size();
        final int endY = (mSelected - toPosition) * mItemHeight;
        autoScrollTo(endY, duration, interpolator, false);
    }

    /**
     * @param endY         　需要滚动到的位置
     * @param duration     　滚动时间
     * @param interpolator
     * @param canIntercept 能否终止滚动，比如触摸屏幕终止滚动
     */
    public void autoScrollTo(final int endY, long duration, final Interpolator interpolator, boolean canIntercept) {
        if (mIsAutoScrolling) {
            return;
        }
        final boolean temp = mDisallowTouch;
        mDisallowTouch = !canIntercept;
        mIsAutoScrolling = true;
        mAutoScrollAnimator.cancel();
        mAutoScrollAnimator.setIntValues(0, endY);
        mAutoScrollAnimator.setInterpolator(interpolator);
        mAutoScrollAnimator.setDuration(duration);
        mAutoScrollAnimator.removeAllUpdateListeners();
        mAutoScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                computeScroll((int) animation.getAnimatedValue(), endY);
                float rate = 0;
                if (Build.VERSION.SDK_INT >= 12) {
                    rate = animation.getAnimatedFraction();
                } else {
                    rate = animation.getCurrentPlayTime() * 1f / animation.getDuration();
                }
                if (rate >= 1) {
                    mIsAutoScrolling = false;
                    mDisallowTouch = temp;
                }
            }
        });
        mAutoScrollAnimator.start();
    }


    /**
     * 停止自动滚动
     */
    public void stopAutoScroll() {
        mIsAutoScrolling = false;
        mAutoScrollAnimator.cancel();
    }

    private static class SlotInterpolator implements Interpolator {
        @Override
        public float getInterpolation(float input) {
            return (float) (Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
        }
    }


    /**
     * 快速滑动时，惯性滑动一段距离
     *
     * @author huangziwei
     */
    private class FlingOnGestureListener extends SimpleOnGestureListener {

        public boolean onDown(MotionEvent e) {
            mTouchDownTime = System.currentTimeMillis();
            if (mDisallowInterceptTouch) {  // 不允许父组件拦截事件
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            // 点击时取消所有滚动效果
            cancelScroll();
            mLastMoveY = e.getY();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               final float velocityY) {
            // 惯性滑动
            if (mIsInertiaScroll) {
                cancelScroll();
                fling(mMoveLength, velocityY);
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) { // 单击时切换选项
            // 滚动过程中单击，不切换选项
            if (!mCanTap || mMoveLength != 0 || mLastScrollY != 0) {
                return false;
            }
            mLastMoveY = e.getY();
            if (mLastMoveY >= mCenterY && mLastMoveY <= mCenterY + mItemHeight) {
                performClick();
            } else if (mLastMoveY < mCenterY) {
                int moveY = mItemHeight;
                autoScrollTo(moveY, 150, sAutoScrollInterpolator, false);
            } else if (mLastMoveY > mCenterY + mItemHeight) {
                int moveY = -mItemHeight;
                autoScrollTo(moveY, 150, sAutoScrollInterpolator, false);
            } else {
                moveToCenter();
            }
            return true;
        }
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        if (data == null) {
            mData = new ArrayList<T>();
        } else {
            this.mData = data;
        }
        mSelected = mData.size() / 2;
        invalidate();
    }


    public T getSelectedItem() {
        return mData.get(mSelected);
    }

    public void setSelectedPosition(int position) {
        if (position < 0 || position > mData.size() - 1
                || position == mSelected) {
            return;
        }
        mSelected = position;
        invalidate();
        if (mListener != null) {
            notifySelected();
        }
    }

    public void setOnSelectedListener(OnSelectedListener listener) {
        mListener = listener;
    }

    public OnSelectedListener getListener() {
        return mListener;
    }

    public boolean isInertiaScroll() {
        return mIsInertiaScroll;
    }

    public void setInertiaScroll(boolean inertiaScroll) {
        this.mIsInertiaScroll = inertiaScroll;
    }

    public boolean isIsCirculation() {
        return mIsCirculation;
    }

    public void setIsCirculation(boolean isCirculation) {
        this.mIsCirculation = isCirculation;
    }

    public boolean isDisallowInterceptTouch() {
        return mDisallowInterceptTouch;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public void setVisibleItemCount(int visibleItemCount) {
        mVisibleItemCount = visibleItemCount;
        reset();
        invalidate();
    }

    public void setDisallowInterceptTouch(boolean disallowInterceptTouch) {
        mDisallowInterceptTouch = disallowInterceptTouch;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public boolean isDisallowTouch() {
        return mDisallowTouch;
    }

    public void setDisallowTouch(boolean disallowTouch) {
        mDisallowTouch = disallowTouch;
    }

    /**
     * 中间item的位置，0 <= centerPosition <= mVisibleItemCount
     *
     * @param centerPosition
     */
    public void setCenterPosition(int centerPosition) {
        if (mCenterPosition < 0) {
            mCenterPosition = 0;
        } else if (mCenterPosition > mVisibleItemCount) {
            mCenterPosition = mVisibleItemCount;
        } else {
            mCenterPosition = centerPosition;
        }
        mCenterY = mCenterPosition * mItemHeight;
        invalidate();
    }

    /**
     * 中间item的位置,默认为 mVisibleItemCount / 2
     *
     * @return
     */
    public int getCenterPosition() {
        return mCenterPosition;
    }

    public void setCenterItemBackground(int centerItemBackground) {
        mCenterItemBackground = centerItemBackground;
        invalidate();
    }

    public int getCenterItemBackground() {
        return mCenterItemBackground;
    }

    public boolean isCanTap() {
        return mCanTap;
    }

    /**
     * 设置 单击切换选项或触发点击监听器
     * @param canTap
     */
    public void setCanTap(boolean canTap) {
        mCanTap = canTap;
    }

    /**
     * @author huangziwei
     */
    public interface OnSelectedListener {
        void onSelected(ScrollPickerView scrollPickerView, int position);
    }

    public int dip2px(float dipVlue) {
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float sDensity = metrics.density;
        return (int) (dipVlue * sDensity + 0.5F);
    }

}