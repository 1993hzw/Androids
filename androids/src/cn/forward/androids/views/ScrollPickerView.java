package cn.forward.androids.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import cn.forward.androids.R;

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
    private int mItemHeight = 0; // 每个条目的高度,当垂直滚动时，高度=mMeasureHeight／mVisibleItemCount
    private int mItemWidth = 0; // 每个条目的宽度，当水平滚动时，宽度=mMeasureWidth／mVisibleItemCount
    private int mItemSize; // 当垂直滚动时，mItemSize = mItemHeight;水平滚动时，mItemSize = mItemWidth
    private int mCenterPosition = -1; // 中间item的位置，0<=mCenterPosition＜mVisibleItemCount，默认为 mVisibleItemCount / 2
    private int mCenterY; // 中间item的起始坐标y(不考虑偏移),当垂直滚动时，y= mCenterPosition*mItemHeight
    private int mCenterX; // 中间item的起始坐标x(不考虑偏移),当垂直滚动时，x = mCenterPosition*mItemWidth
    private int mCenterPoint; // 当垂直滚动时，mCenterPoint = mCenterY;水平滚动时，mCenterPoint = mCenterX
    private float mLastMoveY; // 触摸的坐标y
    private float mLastMoveX; // 触摸的坐标X

    private float mMoveLength = 0; // item移动长度，负数表示向上移动，正数表示向下移动

    private GestureDetector mGestureDetector;
    private OnSelectedListener mListener;

    private Scroller mScroller;
    private boolean mIsFling; // 是否正在惯性滑动
    private boolean mIsMovingCenter; // 是否正在滑向中间
    // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次触屏滑动的坐标
    private int mLastScrollY = 0; // Scroller的坐标y
    private int mLastScrollX = 0; // Scroller的坐标x

    private boolean mDisallowTouch = false; // 不允许触摸

    private Paint mPaint; //
    private Drawable mCenterItemBackground = null; // 中间选中item的背景色

    private boolean mCanTap = true; // 单击切换选项或触发点击监听器

    private boolean mIsHorizontal = false; // 是否水平滚动

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

        init(attrs);

    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,
                    R.styleable.ScrollPickerView);

            if (typedArray.hasValue(R.styleable.ScrollPickerView_spv_center_item_background)) {
                setCenterItemBackground(typedArray.getDrawable(R.styleable.ScrollPickerView_spv_center_item_background));
            }
            setVisibleItemCount(typedArray.getInt(
                    R.styleable.ScrollPickerView_spv_visible_item_count,
                    getVisibleItemCount()));
            setCenterPosition(typedArray.getInt(
                    R.styleable.ScrollPickerView_spv_center_item_position,
                    getCenterPosition()));
            setIsCirculation(typedArray.getBoolean(R.styleable.ScrollPickerView_spv_is_circulation, isIsCirculation()));
            setDisallowInterceptTouch(typedArray.getBoolean(R.styleable.ScrollPickerView_spv_disallow_intercept_touch, isDisallowInterceptTouch()));
            setHorizontal(typedArray.getInt(R.styleable.ScrollPickerView_spv_orientation, mIsHorizontal ? 1 : 2) == 1);
            typedArray.recycle();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {

        if (mData == null || mData.size() <= 0) {
            return;
        }

        // 选中item的背景色
        if (mCenterItemBackground != null) {
            mCenterItemBackground.draw(canvas);
        }

        // 只绘制可见的item
        int length = Math.max(mCenterPosition + 1, mVisibleItemCount - mCenterPosition);
        int position;
        int start = Math.min(length, mData.size());
        // 上下两边
        for (int i = start; i >= 1; i--) { // 先从远离中间位置的item绘制，当item内容偏大时，较近的item覆盖在较远的上面

            if (i <= mCenterPosition + 1) {  // 上面的items,相对位置为 -i
                position = mSelected - i < 0 ? mData.size() + mSelected - i
                        : mSelected - i;
                // 传入位置信息，绘制item
                if (mIsCirculation) {
                    drawItem(canvas, mData, position, -i, mMoveLength, mCenterPoint + mMoveLength - i * mItemSize);
                } else if (mSelected - i >= 0) { // 非循环滚动
                    drawItem(canvas, mData, position, -i, mMoveLength, mCenterPoint + mMoveLength - i * mItemSize);
                }
            }
            if (i <= mVisibleItemCount - mCenterPosition) {  // 下面的items,相对位置为 i
                position = mSelected + i >= mData.size() ? mSelected + i
                        - mData.size() : mSelected + i;
                // 传入位置信息，绘制item
                if (mIsCirculation) {
                    drawItem(canvas, mData, position, i, mMoveLength, mCenterPoint + mMoveLength + i * mItemSize);
                } else if (mSelected + i < mData.size()) { // 非循环滚动
                    drawItem(canvas, mData, position, i, mMoveLength, mCenterPoint + mMoveLength + i * mItemSize);
                }
            }
        }
        // 选中的item
        drawItem(canvas, mData, mSelected, 0, mMoveLength, mCenterPoint + mMoveLength);
    }

    /**
     * 绘制item
     *
     * @param canvas
     * @param data       　数据集
     * @param position   在data数据集中的位置
     * @param relative   相对中间item的位置,relative==0表示中间item,relative<0表示上（左）边的item,relative>0表示下(右)边的item
     * @param moveLength 中间item滚动的距离，moveLength<0则表示向上（右）滚动的距离，moveLength＞0则表示向下（左）滚动的距离
     * @param top        当前绘制item的坐标,当垂直滚动时为顶部y的坐标；当水平滚动时为item最左边x的坐标
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
        if (mCenterPosition < 0) {
            mCenterPosition = mVisibleItemCount / 2;
        }

        if (mIsHorizontal) {
            mItemHeight = getMeasuredHeight();
            mItemWidth = getMeasuredWidth() / mVisibleItemCount;

            mCenterY = 0;
            mCenterX = mCenterPosition * mItemWidth;

            mItemSize = mItemWidth;
            mCenterPoint = mCenterX;
        } else {
            mItemHeight = getMeasuredHeight() / mVisibleItemCount;
            mItemWidth = getMeasuredWidth();

            mCenterY = mCenterPosition * mItemHeight;
            mCenterX = 0;

            mItemSize = mItemHeight;
            mCenterPoint = mCenterY;
        }

        if (mCenterItemBackground != null) {
            mCenterItemBackground.setBounds(mCenterX, mCenterY, mCenterX + mItemWidth, mCenterY + mItemHeight);
        }

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
            case MotionEvent.ACTION_MOVE:

                if (mIsHorizontal) {
                    if (Math.abs(event.getX() - mLastMoveX) < 0.1f) {
                        return true;
                    }
                    mMoveLength += event.getX() - mLastMoveX;
                } else {
                    if (Math.abs(event.getY() - mLastMoveY) < 0.1f) {
                        return true;
                    }
                    mMoveLength += event.getY() - mLastMoveY;
                }
                mLastMoveY = event.getY();
                mLastMoveX = event.getX();
                checkCirculation();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                mLastMoveY = event.getY();
                mLastMoveX = event.getX();
                moveToCenter();
                break;
        }
        return true;
    }


    /**
     * @param curr
     * @param end
     */
    private void computeScroll(int curr, int end, float rate) {
        if (rate < 1) { // 正在滚动
            if (mIsHorizontal) {
                // 可以把scroller看做模拟的触屏滑动操作，mLastScrollX为上次滑动的坐标
                mMoveLength = mMoveLength + curr - mLastScrollX;
                mLastScrollX = curr;
            } else {
                // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次滑动的坐标
                mMoveLength = mMoveLength + curr - mLastScrollY;
                mLastScrollY = curr;
            }
            checkCirculation();
            invalidate();
        } else { // 滚动完毕
            mIsMovingCenter = false;
            mLastScrollY = 0;
            mLastScrollX = 0;

            // 直接居中，不通过动画
            if (mMoveLength > 0) { //// 向下滑动
                if (mMoveLength < mItemSize / 2) {
                    mMoveLength = 0;
                } else {
                    mMoveLength = mItemSize;
                }
            } else {
                if (-mMoveLength < mItemSize / 2) {
                    mMoveLength = 0;
                } else {
                    mMoveLength = -mItemSize;
                }
            }
            checkCirculation();
            mMoveLength = 0;
            mLastScrollY = 0;
            mLastScrollX = 0;
            notifySelected();
            invalidate();
        }

    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) { // 正在滚动
            if (mIsHorizontal) {
                // 可以把scroller看做模拟的触屏滑动操作，mLastScrollX为上次滑动的坐标
                mMoveLength = mMoveLength + mScroller.getCurrX() - mLastScrollX;
            } else {
                // 可以把scroller看做模拟的触屏滑动操作，mLastScrollY为上次滑动的坐标
                mMoveLength = mMoveLength + mScroller.getCurrY() - mLastScrollY;
            }
            mLastScrollY = mScroller.getCurrY();
            mLastScrollX = mScroller.getCurrX();
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
                mLastScrollX = 0;
                notifySelected();
            }
        }
    }

    public void cancelScroll() {
        mLastScrollY = 0;
        mLastScrollX = 0;
        mIsFling = mIsMovingCenter = false;
        mScroller.abortAnimation();
        stopAutoScroll();
    }

    // 检测当前选择的item位置
    private void checkCirculation() {
        if (mMoveLength >= mItemSize) { // 向下滑动
            // 该次滚动距离中越过的item数量
            int span = (int) (mMoveLength / mItemSize);
            mSelected -= span;
            if (mSelected < 0) {  // 滚动顶部，判断是否循环滚动
                if (mIsCirculation) {
                    do {
                        mSelected = mData.size() + mSelected;
                    } while (mSelected < 0); // 当越过的item数量超过一圈时
                    mMoveLength = (mMoveLength - mItemSize) % mItemSize;
                } else { // 非循环滚动
                    mSelected = 0;
                    mMoveLength = mItemSize;
                    if (mIsFling) { // 停止惯性滑动，根据computeScroll()中的逻辑，下一步将调用moveToCenter()
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) { //  移回中间位置
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = (mMoveLength - mItemSize) % mItemSize;
            }

        } else if (mMoveLength <= -mItemSize) { // 向上滑动
            // 该次滚动距离中越过的item数量
            int span = (int) (-mMoveLength / mItemSize);
            mSelected += span;
            if (mSelected >= mData.size()) { // 滚动末尾，判断是否循环滚动
                if (mIsCirculation) {
                    do {
                        mSelected = mSelected - mData.size();
                    } while (mSelected >= mData.size()); // 当越过的item数量超过一圈时
                    mMoveLength = (mMoveLength + mItemSize) % mItemSize;
                } else { // 非循环滚动
                    mSelected = mData.size() - 1;
                    mMoveLength = -mItemSize;
                    if (mIsFling) { // 停止惯性滑动，根据computeScroll()中的逻辑，下一步将调用moveToCenter()
                        mScroller.forceFinished(true);
                    }
                    if (mIsMovingCenter) { //  移回中间位置
                        scroll(mMoveLength, 0);
                    }
                }
            } else {
                mMoveLength = (mMoveLength + mItemSize) % mItemSize;
            }
        }
    }

    // 移动到中间位置
    private void moveToCenter() {

        if (!mScroller.isFinished() || mIsFling || mMoveLength == 0) {
            return;
        }
        cancelScroll();

        // 向下滑动
        if (mMoveLength > 0) {
            if (mIsHorizontal) {
                if (mMoveLength < mItemWidth / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, mItemWidth);
                }
            } else {
                if (mMoveLength < mItemHeight / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, mItemHeight);
                }
            }
        } else {
            if (mIsHorizontal) {
                if (-mMoveLength < mItemWidth / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, -mItemWidth);
                }
            } else {
                if (-mMoveLength < mItemHeight / 2) {
                    scroll(mMoveLength, 0);
                } else {
                    scroll(mMoveLength, -mItemHeight);
                }
            }
        }
    }

    // 平滑滚动
    private void scroll(float from, int to) {
        if (mIsHorizontal) {
            mLastScrollX = (int) from;
            mIsMovingCenter = true;
            mScroller.startScroll((int) from, 0, 0, 0);
            mScroller.setFinalX(to);
        } else {
            mLastScrollY = (int) from;
            mIsMovingCenter = true;
            mScroller.startScroll(0, (int) from, 0, 0);
            mScroller.setFinalY(to);
        }
        invalidate();
    }

    // 惯性滑动，
    private void fling(float from, float vel) {
        if (mIsHorizontal) {
            mLastScrollX = (int) from;
            mIsFling = true;
            // 最多可以惯性滑动10个item
            mScroller.fling((int) from, 0, (int) vel, 0, -10 * mItemWidth,
                    10 * mItemWidth, 0, 0);
        } else {
            mLastScrollY = (int) from;
            mIsFling = true;
            // 最多可以惯性滑动10个item
            mScroller.fling(0, (int) from, 0, (int) vel, 0, 0, -10 * mItemHeight,
                    10 * mItemHeight);
        }
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
        int circle = (int) (length * 1f / (mData.size() * mItemSize) + 0.5f); // 圈数
        circle = circle <= 0 ? 1 : circle;

        int aPlan = circle * (mData.size()) * mItemSize + (mSelected - position) * mItemSize;
        int bPlan = aPlan + (mData.size()) * mItemSize; // 多一圈
        // 让其尽量接近length
        final int end = Math.abs(length - aPlan) < Math.abs(length - bPlan) ? aPlan : bPlan;

        mAutoScrollAnimator.cancel();
        mAutoScrollAnimator.setIntValues(0, end);
        mAutoScrollAnimator.setInterpolator(interpolator);
        mAutoScrollAnimator.setDuration(duration);
        mAutoScrollAnimator.removeAllUpdateListeners();
        if (end != 0) { // itemHeight为0导致endy=0
            mAutoScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float rate = 0;
                    rate = animation.getCurrentPlayTime() * 1f / animation.getDuration();
                    computeScroll((int) animation.getAnimatedValue(), end, rate);
                }
            });
            mAutoScrollAnimator.removeAllListeners();
            mAutoScrollAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mIsAutoScrolling = false;
                }
            });
            mAutoScrollAnimator.start();
        } else {
            computeScroll(end, end, 1);
            mIsAutoScrolling = false;
        }
    }

    /**
     * 自动滚动，默认速度为 0.6dp/ms
     *
     * @see ScrollPickerView#autoScrollFast(int, long, float, Interpolator)
     */
    public void autoScrollFast(final int position, long duration) {
        float speed = dip2px(0.6f);
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
                float rate = 0;
                rate = animation.getCurrentPlayTime() * 1f / animation.getDuration();
                computeScroll((int) animation.getAnimatedValue(), endY, rate);
            }
        });
        mAutoScrollAnimator.removeAllListeners();
        mAutoScrollAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mIsAutoScrolling = false;
                mDisallowTouch = temp;
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

        private boolean mIsScrollingLastTime = false;

        public boolean onDown(MotionEvent e) {
            if (mDisallowInterceptTouch) {  // 不允许父组件拦截事件
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
            }
            mIsScrollingLastTime = isScrolling(); // 记录是否从滚动状态终止
            // 点击时取消所有滚动效果
            cancelScroll();
            mLastMoveY = e.getY();
            mLastMoveX = e.getX();
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               final float velocityY) {
            // 惯性滑动
            if (mIsInertiaScroll) {
                cancelScroll();
                if (mIsHorizontal) {
                    fling(mMoveLength, velocityX);
                } else {
                    fling(mMoveLength, velocityY);
                }
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            mLastMoveY = e.getY();
            mLastMoveX = e.getX();
            float lastMove = 0;
            if (isHorizontal()) {
                mCenterPoint = mCenterX;
                lastMove = mLastMoveX;
            } else {
                mCenterPoint = mCenterY;
                lastMove = mLastMoveY;
            }
            if (mCanTap && !isScrolling() && !mIsScrollingLastTime) {
                if (lastMove >= mCenterPoint && lastMove <= mCenterPoint + mItemSize) {
                    performClick();
                } else if (lastMove < mCenterPoint) {
                    int move = mItemSize;
                    autoScrollTo(move, 150, sAutoScrollInterpolator, false);
                } else if (lastMove > mCenterPoint + mItemSize) {
                    int move = -mItemSize;
                    autoScrollTo(move, 150, sAutoScrollInterpolator, false);
                } else {
                    moveToCenter();
                }
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

    public int getSelectedPosition() {
        return mSelected;
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

    /**
     * 是否允许父元素拦截事件，设置true后可以保证在ScrollView下正常滚动
     */
    public void setDisallowInterceptTouch(boolean disallowInterceptTouch) {
        mDisallowInterceptTouch = disallowInterceptTouch;
    }

    public int getItemHeight() {
        return mItemHeight;
    }

    public int getItemWidth() {
        return mItemWidth;
    }

    /**
     * @return 当垂直滚动时，mItemSize = mItemHeight;水平滚动时，mItemSize = mItemWidth
     */
    public int getItemSize() {
        return mItemSize;
    }

    /**
     * @return 中间item的起始坐标x(不考虑偏移), 当垂直滚动时，x = mCenterPosition*mItemWidth
     */
    public int getCenterX() {
        return mCenterX;
    }

    /**
     * @return 中间item的起始坐标y(不考虑偏移), 当垂直滚动时，y= mCenterPosition*mItemHeight
     */
    public int getCenterY() {
        return mCenterY;
    }

    /**
     * @return 当垂直滚动时，mCenterPoint = mCenterY;水平滚动时，mCenterPoint = mCenterX
     */
    public int getCenterPoint() {
        return mCenterPoint;
    }

    public boolean isDisallowTouch() {
        return mDisallowTouch;
    }

    /**
     * 设置是否允许手动触摸滚动
     *
     * @param disallowTouch
     */
    public void setDisallowTouch(boolean disallowTouch) {
        mDisallowTouch = disallowTouch;
    }

    /**
     * 中间item的位置，0 <= centerPosition <= mVisibleItemCount
     *
     * @param centerPosition
     */
    public void setCenterPosition(int centerPosition) {
        if (centerPosition < 0) {
            mCenterPosition = 0;
        } else if (centerPosition >= mVisibleItemCount) {
            mCenterPosition = mVisibleItemCount - 1;
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

    public void setCenterItemBackground(Drawable centerItemBackground) {
        mCenterItemBackground = centerItemBackground;
        mCenterItemBackground.setBounds(mCenterX, mCenterY, mCenterX + mItemWidth, mCenterY + mItemHeight);
        invalidate();
    }

    public void setCenterItemBackground(int centerItemBackgroundColor) {
        mCenterItemBackground = new ColorDrawable(centerItemBackgroundColor);
        mCenterItemBackground.setBounds(mCenterX, mCenterY, mCenterX + mItemWidth, mCenterY + mItemHeight);
        invalidate();
    }

    public Drawable getCenterItemBackground() {
        return mCenterItemBackground;
    }

    public boolean isScrolling() {
        return mIsFling || mIsMovingCenter || mIsAutoScrolling;
    }

    public boolean isFling() {
        return mIsFling;
    }

    public boolean isMovingCenter() {
        return mIsMovingCenter;
    }

    public boolean isAutoScrolling() {
        return mIsAutoScrolling;
    }

    public boolean isCanTap() {
        return mCanTap;
    }

    /**
     * 设置 单击切换选项或触发点击监听器
     *
     * @param canTap
     */
    public void setCanTap(boolean canTap) {
        mCanTap = canTap;
    }

    public boolean isHorizontal() {
        return mIsHorizontal;
    }

    public boolean isVertical() {
        return !mIsHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        if (mIsHorizontal == horizontal) {
            return;
        }
        mIsHorizontal = horizontal;
        reset();
        if (mIsHorizontal) {
            mItemSize = mItemWidth;
        } else {
            mItemSize = mItemHeight;
        }
        invalidate();
    }

    public void setVertical(boolean vertical) {
        if (mIsHorizontal == !vertical) {
            return;
        }
        mIsHorizontal = !vertical;
        reset();
        if (mIsHorizontal) {
            mItemSize = mItemWidth;
        } else {
            mItemSize = mItemHeight;
        }
        invalidate();
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

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            moveToCenter();
        }
    }
}