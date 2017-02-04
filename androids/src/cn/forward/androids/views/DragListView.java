package cn.forward.androids.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * item可拖拽的ListView
 * Created by huangziwei on 16-6-29.
 */
public class DragListView extends ListView {

    private int mLastPosition; // 上次的位置，用于判断是否跟当前交换位置
    private int mCurrentPosition; // 手指点击准备拖动的时候,当前拖动项在列表中的位置.

    private int mAutoScrollUpY; // 拖动的时候，开始向上滚动的边界
    private int mAutoScrollDownY; // 拖动的时候，开始向下滚动的边界

    private int mLastX, mLastY;
    private int mDownX, mDownY;

    private int mDragViewOffset; // 触摸点在itemView中的高度

    private DragItemListener mDragItemListener;

    private boolean mHasStart = false;

    private Bitmap mBitmap; // 拖拽的itemView图像

    private View mItemView;

    private int mTouchSlop;
    private long mLastScrollTime;
    private boolean mScrolling = false;
    private Runnable mScrollRunnable;

    public DragListView(Context context) {
        this(context, null);
    }

    public DragListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
        }
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

        mScrollRunnable = new Runnable() {
            @Override
            public void run() {
                mScrolling = false;
                if (mBitmap != null) {
                    mLastScrollTime = System.currentTimeMillis();
                    onMove((int) mMoveY);
                    invalidate();
                }
            }
        };
    }

    /**
     * 触摸事件处理
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
                if (mBitmap != null) {
                    mLastX = (int) ev.getX();
                    mLastY = (int) ev.getY();
                    stopDrag();
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mBitmap != null) {
                    if (!mHasStart) {
                        mDragItemListener.startDrag(mCurrentPosition, mItemView);
                        mHasStart = true;
                    }
                    int moveY = (int) ev.getY();
                    if (moveY < 0) { // 限制触摸范围在ＬｉｓｔＶｉｅｗ中
                        moveY = 0;
                    } else if (moveY > getHeight()) {
                        moveY = getHeight();
                    }
                    mMoveY = moveY;
                    onMove(moveY);
                    mLastY = moveY;
                    mLastX = (int) ev.getX();
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_DOWN: // 判断是否进拖拽
                stopDrag();
                mDownX = (int) ev.getX(); // 获取相对与ListView的x坐标
                mDownY = (int) ev.getY(); // 获取相应与ListView的y坐标
                int temp = pointToPosition(mDownX, mDownY);
                if (temp == AdapterView.INVALID_POSITION) { // 无效不进行处理
                    return super.dispatchTouchEvent(ev);
                }
                mLastPosition = mCurrentPosition = temp;

                // 获取当前位置的视图(可见状态)
                ViewGroup itemView = (ViewGroup) getChildAt(mCurrentPosition - getFirstVisiblePosition());

                if (itemView != null && mDragItemListener != null && mDragItemListener.canDrag(itemView, mDownX, mDownY)) {

                    // 触摸点在item项中的高度
                    mDragViewOffset = mDownY - itemView.getTop();
                    mDragItemListener.beforeDrawingCache(itemView);
                    itemView.setDrawingCacheEnabled(true); // 开启cache.
                    mBitmap = Bitmap.createBitmap(itemView.getDrawingCache()); // 根据cache创建一个新的bitmap对象.
                    itemView.setDrawingCacheEnabled(false);
                    Bitmap afterBitmap = mDragItemListener.afterDrawingCache(itemView, mBitmap);
                    mBitmap = afterBitmap != null ? afterBitmap : mBitmap;
                    mHasStart = false;
                    mLastY = mDownY;
                    mLastX = mDownX;

                    mItemView = itemView;
                    invalidate();
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private float mMoveY;

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 绘制拖拽的itemView
        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, mLastY - mDragViewOffset, null);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mAutoScrollUpY = dp2px(getContext(), 80); // 取得向上滚动的边际，大概为该控件的1/3
        mAutoScrollDownY = h - mAutoScrollUpY; // 取得向下滚动的边际，大概为该控件的2/3
    }


    /**
     * 拖动执行，在Move方法中执行
     */
    public void onMove(int moveY) {
        int endPos = pointToPosition(getWidth() / 2, moveY);
        if (endPos == INVALID_POSITION) { // 当itemView不可见时也会返回-１
            checkScroller(moveY); // listview移动.
            return;
        }
        int mask = mLastPosition > endPos ? -1 : 1;
        // 扫描从mLastPosition到endPos的所有ItemView，交换他们的位置
        // 不能只扫描endPos，否则会在快速拖动的时候跳过中间的itemView
        for (int i = mLastPosition; mask > 0 ? i <= endPos : i >= endPos; i += mask) {
            int index = i - getFirstVisiblePosition();
            if (index >= getChildCount() || index < 0) {
                continue;
            }
            int y = getChildAt(index).getTop();
            int tempPosition = pointToPosition(0, y);
            if (tempPosition != INVALID_POSITION) {
                mCurrentPosition = tempPosition;
            }
            if (y < getChildAt(0).getTop()) { // 超出边界处理(如果向上超过第二项Top的话，那么就放置在第一个位置)
                mCurrentPosition = 0;
            } else if (y > getChildAt(getChildCount() - 1).getBottom()) { // // 如果拖动超过最后一项的最下边那么就防止在最下边
                mCurrentPosition = getAdapter().getCount() - 1;
            }
            checkExchange(y); // 时时交换
        }
        checkScroller(moveY); // listview移动.
    }

    /***
     * 移动到底部或顶部时自动滚动列表
     * 当移动到底部时，ListView向上滑动，当移动到顶部时，ListView要向下滑动
     */
    public void checkScroller(final int y) {

        int offset = 0;
        if (y < mAutoScrollUpY) { // 拖动到顶部，ListView需要下滑
            if (y <= mDownY - mTouchSlop) {
                offset = dp2px(getContext(), 6); // 滑动的距离
            }
        } else if (y > mAutoScrollDownY) { // 拖动到底部，ListView需要上滑
            if (y >= mDownY + mTouchSlop) {
                offset = -dp2px(getContext(), 6); // 滑动的距离
            }
        }

        if (offset != 0) {
            View view = getChildAt(mCurrentPosition - getFirstVisiblePosition());
            if (view != null) {
                // 滚动列表
                setSelectionFromTop(mCurrentPosition, view.getTop() + offset);
                if (!mScrolling) {
                    mScrolling = true;
                    long passed = System.currentTimeMillis() - mLastScrollTime;
                    postDelayed(mScrollRunnable, passed > 15 ? 15 : 15 - passed);
                }
            }
        }
    }

    /**
     * 停止拖动，删除影像
     */
    public void stopDrag() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            if (mDragItemListener != null) {
                mDragItemListener.onRelease(mCurrentPosition, mItemView, mLastY - mDragViewOffset, mLastX, mLastY);
            }
        }
        if (mItemView != null) {
            mItemView = null;
        }
        mScrolling = false;
        removeCallbacks(mScrollRunnable);
    }

    /***
     * 拖动时时change
     */
    private void checkExchange(int y) {
        if (mCurrentPosition != mLastPosition) { // // 数据交换
            if (mDragItemListener != null) {
                if (mDragItemListener.canExchange(mLastPosition, mCurrentPosition)) { // 进行数据交换,true则表示交换成功
                    View lastView = mItemView;
                    mItemView = getChildAt(mCurrentPosition - getFirstVisiblePosition());
                    // 通知交换数据成功，可在此时设置交换的动画效果
                    mDragItemListener.onExchange(mLastPosition, mCurrentPosition, lastView, mItemView);
                    mLastPosition = mCurrentPosition;
                }
            }
        }
    }


    public void setDragItemListener(DragItemListener listener) {
        mDragItemListener = listener;
    }

    public DragItemListener getDragListener() {
        return mDragItemListener;
    }

    /**
     * 　拖拽监听器
     */
    public interface DragItemListener {

        /**
         * 是否进行数据交换
         *
         * @param srcPosition
         * @param position 当前拖拽的view的索引
         * @return 返回true，则确认数据交换;返回false则表示放弃
         */
        boolean canExchange(int srcPosition, int position);

        /**
         * 当完成数据交换时回调
         *
         * @param srcPosition
         * @param position    当前拖拽的view的索引
         * @param srcItemView
         * @param itemView 当前拖拽的view
         */
        void onExchange(int srcPosition, int position, View srcItemView, View itemView);

        /**
         * 释放手指
         *
         * @param position
         */
        void onRelease(int position, View itemView, int itemViewY, int releaseX, int releaseY);

        /**
         * 是否可以拖拽
         *
         * @param itemView
         * @param x        当前触摸的坐标
         * @param y
         * @return
         */
        boolean canDrag(View itemView, int x, int y);

        /**
         * 开始拖拽
         *
         * @param position
         */
        void startDrag(int position, View itemView);

        /**
         * 在生成拖影（itemView.getDrawingCache()）之前
         *
         * @param itemView
         */
        void beforeDrawingCache(View itemView);

        /**
         * 在生成拖影（itemView.getDrawingCache()）之后
         *
         * @param itemView
         * @param bitmap   由itemView.getDrawingCache()生成
         * @return 最终显示的拖影，如果返回为空则使用itemView.getDrawingCache()
         */
        Bitmap afterDrawingCache(View itemView, Bitmap bitmap);
    }

    /**
     * 交换item加入过渡动画
     */
    public static abstract class SimpleAnimationDragItemListener implements DragItemListener {
        @Override
        public void onRelease(int positon, View itemView, int itemViewY, int releaseX, int releaseY) {
            itemView.setVisibility(View.VISIBLE);
            if (itemView != null && Math.abs(itemViewY - itemView.getTop()) > itemView.getHeight() / 5) {
                AlphaAnimation animation = new AlphaAnimation(0.5f, 1);
                animation.setDuration(150);
                itemView.clearAnimation();
                itemView.startAnimation(animation);
            }

        }

        @Override
        public void startDrag(int position, View itemView) {
            if (itemView != null) { // 隐藏view
                itemView.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * 设置交换换动画
         */
        @Override
        public void onExchange(int srcPosition, int position, View srcItemView, View itemView) {
            if (srcItemView != null) {
                int height = srcPosition > position ? -srcItemView.getHeight() : srcItemView.getHeight();
                TranslateAnimation animation = new TranslateAnimation(0, 0, height, 0);
                animation.setDuration(200);
                srcItemView.clearAnimation();
                srcItemView.startAnimation(animation);
                srcItemView.setVisibility(View.VISIBLE);
            }
            if (itemView != null) {
                itemView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public int dp2px(Context context, float dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
